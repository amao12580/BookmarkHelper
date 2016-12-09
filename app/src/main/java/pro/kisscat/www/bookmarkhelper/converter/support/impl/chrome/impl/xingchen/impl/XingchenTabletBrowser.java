package pro.kisscat.www.bookmarkhelper.converter.support.impl.chrome.impl.xingchen.impl;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import java.util.LinkedList;
import java.util.List;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.chrome.impl.xingchen.XingchenBrowserAble;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;
import pro.kisscat.www.bookmarkhelper.exception.ConverterException;
import pro.kisscat.www.bookmarkhelper.util.Path;
import pro.kisscat.www.bookmarkhelper.util.context.ContextUtil;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/12/9
 * Time:09:56
 * <p>
 * 星尘浏览器-平板版
 * <p>
 * http://www.coolapk.com/apk/com.chaozhuo.browser
 */

public class XingchenTabletBrowser extends XingchenBrowserAble {
    private static final String packageName = "com.chaozhuo.browser";
    private List<Bookmark> bookmarks;

    public XingchenTabletBrowser() {
        super.TAG = "XingchenTablet";
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
        this.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_xingchen_tablet));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.browser_name_show_xingchen_tablet));
    }

    private static final String fileName_origin = "Bookmarks";
    private static final String fileJsonPath_origin = Path.FILE_SPLIT + "app_chromeshell" + Path.FILE_SPLIT + "Default" + Path.FILE_SPLIT;
    private static final String filePath_origin = Path.INNER_PATH_DATA + packageName;
    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + Path.FILE_SPLIT + "XingchenTablet" + Path.FILE_SPLIT;

    @Override
    public List<Bookmark> readBookmark() {
        if (bookmarks != null) {
            LogHelper.v(TAG + ":bookmarks cache is hit.");
            return bookmarks;
        }
        LogHelper.v(TAG + ":bookmarks cache is miss.");
        LogHelper.v(TAG + ":开始读取书签数据");
        try {
            List<Bookmark> bookmarks = new LinkedList<>();
            List<Bookmark> bookmarksPart1 = this.fetchBookmarks(filePath_origin + fileJsonPath_origin, fileName_origin, filePath_cp);
            List<Bookmark> bookmarksPart2 = fetchBookmarksByHomePage(filePath_origin, filePath_cp);
            LogHelper.v(TAG + ":Json书签数据:" + JsonUtil.toJson(bookmarksPart1));
            LogHelper.v(TAG + ":Json书签条数:" + bookmarksPart1.size());
            LogHelper.v(TAG + ":sqlite书签数据:" + JsonUtil.toJson(bookmarksPart2));
            LogHelper.v(TAG + ":sqlite书签条数:" + bookmarksPart2.size());
            bookmarks.addAll(bookmarksPart1);
            bookmarks.addAll(bookmarksPart2);
            this.bookmarks = new LinkedList<>();
            fetchValidBookmarks(this.bookmarks, bookmarks);
        } catch (ConverterException converterException) {
            converterException.printStackTrace();
            LogHelper.e(converterException.getMessage());
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
