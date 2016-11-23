package pro.kisscat.www.bookmarkhelper.converter.support.impl.uc.impl;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import java.util.LinkedList;
import java.util.List;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.uc.UCBroswerable;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;
import pro.kisscat.www.bookmarkhelper.exception.ConverterException;
import pro.kisscat.www.bookmarkhelper.util.Path;
import pro.kisscat.www.bookmarkhelper.util.context.ContextUtil;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.storage.ExternalStorageUtil;
import pro.kisscat.www.bookmarkhelper.util.storage.InternalStorageUtil;
import pro.kisscat.www.bookmarkhelper.util.toast.ToastUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/1
 * Time:9:20
 * <p>
 * UC浏览器-国内版
 */

public class UCBroswer extends UCBroswerable {
    private List<Bookmark> bookmarks;


    public UCBroswer() {
        super.TAG = "UC";
        super.packageName = "com.UCMobile";
    }

    public String getPackageName() {
        return packageName;
    }

    @Override
    public int readBookmarkSum(Context context) {
        if (bookmarks == null) {
            readBookmark(context);
        }
        return bookmarks.size();
    }

    @Override
    public void fillDefaultIcon(Context context) {
        this.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_uc));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.broswer_name_show_uc));
    }

    private static final String fileName_origin = "bookmark.db";
    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + "/UC/";
    private static String notSupportParseHomepageBookmarks;

    @Override
    public List<Bookmark> readBookmark(Context context) {
        if (bookmarks != null) {
            LogHelper.v(TAG + ":bookmarks cache is hit.");
            return bookmarks;
        }
        LogHelper.v(TAG + ":bookmarks cache is miss.");
        LogHelper.v(TAG + ":开始读取书签数据");
        try {
            if (notSupportParseHomepageBookmarks == null) {
                notSupportParseHomepageBookmarks = context.getResources().getString(R.string.notSupportParseHomepageBookmarks);
            }
            ToastUtil.showMessage(context, notSupportParseHomepageBookmarks);
            ExternalStorageUtil.mkdir(context, filePath_cp, this.getName());
            List<Bookmark> bookmarksList = new LinkedList<>();
            String filePath_origin = Path.INNER_PATH_DATA + packageName + "/databases/";
            List<Bookmark> bookmarksListPart1 = fetchBookmarksListByUserHasLogined(context, filePath_origin);
            List<Bookmark> bookmarksListPart2 = fetchBookmarksListByNoUserLogined(context, filePath_origin + fileName_origin, filePath_cp + fileName_origin);
            LogHelper.v("已登录的用户书签数据:" + JsonUtil.toJson(bookmarksListPart1));
            LogHelper.v("已登录的用户书签条数:" + bookmarksListPart1.size());
            LogHelper.v("未登录的用户书签数据:" + JsonUtil.toJson(bookmarksListPart2));
            LogHelper.v("未登录的用户书签条数:" + bookmarksListPart2.size());
            bookmarksList.addAll(bookmarksListPart1);
            bookmarksList.addAll(bookmarksListPart2);
            LogHelper.v("总的书签条数:" + bookmarksList.size());
            bookmarks = new LinkedList<>();
            fetchValidBookmarks(bookmarks, bookmarksList);
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.e(e.getMessage());
            throw new ConverterException(ContextUtil.buildReadBookmarksErrorMessage(context, this.getName()));
        } finally {
            LogHelper.v(TAG + ":读取书签数据结束");
        }
        return bookmarks;
    }

    private List<Bookmark> fetchBookmarksListByUserHasLogined(Context context, String dir) {
        LogHelper.v(TAG + ":开始读取已登录用户的书签SQLite数据库,root dir:" + dir);
        List<Bookmark> result = new LinkedList<>();
        String regularRule = "[1-9][0-9]{4,14}.db";//第一位1-9之间的数字，第二位0-9之间的数字，数字范围4-14个之间
        String searchRule = "*.db";
        List<String> fileNames = InternalStorageUtil.lsFileAndSortByRegular(dir, searchRule);
        if (fileNames == null || fileNames.isEmpty()) {
            LogHelper.v(TAG + ":已登录用户没有书签数据");
            return result;
        }
        String targetFilePath = null;
        String targetFileName = null;
        for (String item : fileNames) {
            LogHelper.v("item:" + item);
            if (item == null) {
                LogHelper.v("item is null.");
                break;
            }
            if (item.equals(fileName_origin)) {
                continue;
            }
            if (item.matches(regularRule)) {
                targetFilePath = dir + item;
                targetFileName = item;
                break;
            } else {
                LogHelper.v("not match.");
            }
        }
        if (targetFilePath == null) {
            LogHelper.v("targetFilePath is miss.");
            return result;
        }
        LogHelper.v("targetFilePath is:" + targetFilePath);
        String tmpFilePath = filePath_cp + targetFileName;
        ExternalStorageUtil.copyFile(context, targetFilePath, tmpFilePath, this.getName());
        result.addAll(fetchBookmarksList(false, context, tmpFilePath, "bookmark", null, null, "create_time asc"));
        LogHelper.v(TAG + ":读取已登录用户书签SQLite数据库结束");
        return result;
    }

    @Override
    public int appendBookmark(Context context, List<Bookmark> bookmarks) {
        return 0;
    }
}
