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
import pro.kisscat.www.bookmarkhelper.util.toast.ToastUtil;

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

public class UCIntlBroswer extends UCBroswerable {
    private List<Bookmark> bookmarks;

    public UCIntlBroswer() {
        super.TAG = "UCIntl";
        super.packageName = "com.UCMobile.intl";
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
        this.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_ucintl));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.broswer_name_show_ucintl));
    }

    private static final String fileName_origin = "bookmark.db";

    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + "/UCIntl/";
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
            String filePath_origin = Path.INNER_PATH_DATA + packageName + "/databases/";
            List<Bookmark> bookmarksList = fetchBookmarksListByNoUserLogined(context, filePath_origin + fileName_origin, filePath_cp + fileName_origin);
            LogHelper.v("用户书签数据:" + JsonUtil.toJson(bookmarksList));
            LogHelper.v("书签条数:" + bookmarksList.size());
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

    @Override
    public int appendBookmark(Context context, List<Bookmark> bookmarks) {
        return 0;
    }
}
