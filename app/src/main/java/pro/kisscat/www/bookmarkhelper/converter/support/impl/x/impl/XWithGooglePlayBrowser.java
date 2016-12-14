package pro.kisscat.www.bookmarkhelper.converter.support.impl.x.impl;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import java.util.LinkedList;
import java.util.List;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.x.XBrowserAble;
import pro.kisscat.www.bookmarkhelper.entry.app.Bookmark;
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
 * Date:2016/12/13
 * Time:14:50
 * <p>
 * X浏览器-Google play版
 */

public class XWithGooglePlayBrowser extends XBrowserAble {
    private static final String packageName = "com.mmbox.xbrowser.gp";
    private List<Bookmark> bookmarks;


    public XWithGooglePlayBrowser() {
        super.TAG = "XWithGooglePlay";
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
        this.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_x_gp));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.browser_name_show_x_gp));
    }

    private static final String filePath_origin = Path.INNER_PATH_DATA + packageName + Path.FILE_SPLIT + "databases" + Path.FILE_SPLIT;
    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + Path.FILE_SPLIT + "XWithGooglePlay" + Path.FILE_SPLIT;

    @Override
    public List<Bookmark> readBookmark() {
        if (bookmarks != null) {
            LogHelper.v(TAG + ":bookmarks cache is hit.");
            return bookmarks;
        }
        LogHelper.v(TAG + ":bookmarks cache is miss.");
        LogHelper.v(TAG + ":开始读取书签数据");
        try {
            String originFilePathFull = filePath_origin + fileName_origin;
            LogHelper.v(TAG + ":origin file path:" + originFilePathFull);
            ExternalStorageUtil.mkdir(filePath_cp, this.getName());
            LogHelper.v(TAG + ":tmp file path:" + filePath_cp + fileName_origin);
            ExternalStorageUtil.copyFile(originFilePathFull, filePath_cp + fileName_origin, this.getName());
            List<Bookmark> bookmarksList = fetchBookmarksList(filePath_cp + fileName_origin);
            LogHelper.v(TAG + ":书签数据:" + JsonUtil.toJson(bookmarksList));
            LogHelper.v(TAG + ":书签条数:" + bookmarksList.size());
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
