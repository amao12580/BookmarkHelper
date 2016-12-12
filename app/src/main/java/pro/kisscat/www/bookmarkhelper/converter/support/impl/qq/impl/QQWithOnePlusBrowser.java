package pro.kisscat.www.bookmarkhelper.converter.support.impl.qq.impl;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import java.util.LinkedList;
import java.util.List;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.qq.QQBrowserAble;
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
 * Date:2016/12/12
 * Time:16:10
 * <p>
 * QQ浏览器，一加手机版
 */

public class QQWithOnePlusBrowser extends QQBrowserAble {
    private static final String packageName = "com.android.browser";
    private List<Bookmark> bookmarks;

    public QQWithOnePlusBrowser() {
        super.TAG = "QQWithOnePlus";
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
        this.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_qqwithoneplus));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.browser_name_show_qqwithoneplus));
    }

    private static final String filePath_origin = Path.INNER_PATH_DATA + packageName + Path.FILE_SPLIT + "databases" + Path.FILE_SPLIT;
    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + Path.FILE_SPLIT + "QQWithOnePlus" + Path.FILE_SPLIT;

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
            List<Bookmark> bookmarksListPart1 = fetchBookmarksListByUserHasLogined(filePath_origin, filePath_cp);
            List<Bookmark> bookmarksListPart2 = fetchBookmarksListByNoUserLogined(filePath_origin, filePath_cp);
            LogHelper.v(TAG + ":已登录用户书签数据:" + JsonUtil.toJson(bookmarksListPart1));
            LogHelper.v(TAG + ":已登录用户书签条数:" + bookmarksListPart1.size());
            LogHelper.v(TAG + ":未登录的用户书签数据:" + JsonUtil.toJson(bookmarksListPart2));
            LogHelper.v(TAG + ":未登录的用户书签条数:" + bookmarksListPart2.size());
            bookmarksList.addAll(bookmarksListPart1);
            bookmarksList.addAll(bookmarksListPart2);
            LogHelper.v(TAG + ":总的书签条数:" + bookmarksList.size());
            bookmarks = new LinkedList<>();
            fetchValidBookmarks(bookmarks, bookmarksList);
        } catch (ConverterException converterException) {
            converterException.printStackTrace();
            LogHelper.e(converterException);
            throw converterException;
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.e(e);
            throw new ConverterException(ContextUtil.buildReadBookmarksErrorMessage(this.getName()));
        } finally {
            LogHelper.v(TAG + ":读取书签数据结束");
        }
        return bookmarks;
    }

    @Override
    public int appendBookmark(List<Bookmark> bookmarks) {
        return 0;
    }
}
