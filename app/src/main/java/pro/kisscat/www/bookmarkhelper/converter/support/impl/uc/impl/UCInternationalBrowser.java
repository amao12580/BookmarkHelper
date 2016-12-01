package pro.kisscat.www.bookmarkhelper.converter.support.impl.uc.impl;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import java.util.LinkedList;
import java.util.List;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.uc.UCBrowserAble;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;
import pro.kisscat.www.bookmarkhelper.exception.ConverterException;
import pro.kisscat.www.bookmarkhelper.util.Path;
import pro.kisscat.www.bookmarkhelper.util.context.ContextUtil;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.storage.ExternalStorageUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/1
 * Time:9:20
 * <p>
 * * UC浏览器-国际版
 */

public class UCInternationalBrowser extends UCBrowserAble {
    private List<Bookmark> bookmarks;

    public UCInternationalBrowser() {
        super.TAG = "UCInternational";
        super.packageName = "com.UCMobile.intl";
    }

    public String getPackageName() {
        return packageName;
    }

    @Override
    public int readBookmarkSum() {
        if (bookmarks == null) {
            readBookmark();
        }
        return bookmarks.size();
    }

    @Override
    public void fillDefaultIcon(Context context) {
        this.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_ucintl));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.browser_name_show_ucintl));
    }

    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + "/UCInternational/";

    @Override
    public List<Bookmark> readBookmark() {
        if (bookmarks != null) {
            LogHelper.v(TAG + ":bookmarks cache is hit.");
            return bookmarks;
        }
        LogHelper.v(TAG + ":bookmarks cache is miss.");
        LogHelper.v(TAG + ":开始读取书签数据");
        try {
            ExternalStorageUtil.mkdir(filePath_cp, this.getName());
            List<Bookmark> bookmarksList = new LinkedList<>();
            String filePath_origin = Path.INNER_PATH_DATA + packageName + "/databases/";
            List<Bookmark> bookmarksListPart1 = fetchBookmarksListByUserHasLogined(filePath_origin, filePath_cp);
            List<Bookmark> bookmarksListPart2 = fetchBookmarksListByNoUserLogined(filePath_origin + fileName_origin, filePath_cp + fileName_origin);
            LogHelper.v(TAG + ":已登录的用户书签数据:" + JsonUtil.toJson(bookmarksListPart1));
            LogHelper.v(TAG + ":已登录的用户书签条数:" + bookmarksListPart1.size());
            LogHelper.v(TAG + ":未登录的用户书签数据:" + JsonUtil.toJson(bookmarksListPart2));
            LogHelper.v(TAG + ":未登录的用户书签条数:" + bookmarksListPart2.size());
            bookmarksList.addAll(bookmarksListPart1);
            bookmarksList.addAll(bookmarksListPart2);
            LogHelper.v(TAG + ":总的书签条数:" + bookmarksList.size());
            bookmarks = new LinkedList<>();
            fetchValidBookmarks(bookmarks, bookmarksList);
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.e(e.getMessage());
            throw new ConverterException(ContextUtil.buildReadBookmarksErrorMessage(this.getName()));
        } finally {
            LogHelper.v(TAG + ":读取书签数据结束");
        }
        return bookmarks;
    }

    public String getPreExecuteConverterMessage() {
        return ContextUtil.buildNotSupportParseHomepageBookmarksMessage();
    }

    @Override
    public int appendBookmark(List<Bookmark> bookmarks) {
        return 0;
    }
}
