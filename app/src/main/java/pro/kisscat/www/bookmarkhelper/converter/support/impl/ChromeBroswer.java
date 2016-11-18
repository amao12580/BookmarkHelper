package pro.kisscat.www.bookmarkhelper.converter.support.impl;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.alibaba.fastjson.JSONReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.converter.support.BasicBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.chrome.ChromeBookmark;
import pro.kisscat.www.bookmarkhelper.exception.ConverterException;
import pro.kisscat.www.bookmarkhelper.util.Path;
import pro.kisscat.www.bookmarkhelper.util.context.ContextUtil;
import pro.kisscat.www.bookmarkhelper.util.file.FileUtil;
import pro.kisscat.www.bookmarkhelper.util.file.pojo.File;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.storage.ExternalStorageUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/9
 * Time:15:38
 */

public class ChromeBroswer extends BasicBroswer {
    private static final String TAG = "Chrome";
    private static final String packageName = "com.android.chrome";
    private List<Bookmark> bookmarks;

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
        this.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_chrome));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.broswer_name_show_chrome));
    }

    private static final String fileName_origin = "Bookmarks";
    private static final String filePath_origin = Path.INNER_PATH_DATA + packageName + "/app_chrome/Default/";
    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + "/Chrome/";

    @Override
    public List<Bookmark> readBookmark(Context context) {
        if (bookmarks != null) {
            LogHelper.v(TAG + ":bookmarks cache is hit.");
            return bookmarks;
        }
        LogHelper.v(TAG + ":bookmarks cache is miss.");
        LogHelper.v(TAG + ":开始读取书签数据");
        try {
            String originFilePathFull = filePath_origin + fileName_origin;
            LogHelper.v(TAG + ":origin file path:" + originFilePathFull);
            ExternalStorageUtil.mkdir(context, filePath_cp, this.getName());
            LogHelper.v(TAG + ":tmp file path:" + filePath_cp + fileName_origin);
            java.io.File file = ExternalStorageUtil.copyFile(context, originFilePathFull, filePath_cp + fileName_origin, this.getName());
            List<Bookmark> chromeBookmarks = fetchBookmarks(file);
            bookmarks = new LinkedList<>();
            fetchValidBookmarks(bookmarks, chromeBookmarks);
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.e(e.getMessage());
            throw new ConverterException(ContextUtil.buildReadBookmarksErrorMessage(context, this.getName()));
        } finally {
            LogHelper.v(TAG + ":读取书签数据结束");
        }
        return bookmarks;
    }

    private List<Bookmark> fetchBookmarks(java.io.File file) throws FileNotFoundException {
        JSONReader jsonReader = null;
        List<Bookmark> result = new LinkedList<>();
        try {
            jsonReader = new JSONReader(new FileReader(file));
            bookmarks = new LinkedList<>();
            ChromeBookmark chromeBookmark = jsonReader.readObject(ChromeBookmark.class);
            if (chromeBookmark == null) {
                LogHelper.v("chromeBookmark is null.");
                return result;
            }
            File fileShow = FileUtil.formatFileSize(file);
            if (fileShow.isOver10KB()) {
                LogHelper.v("书签数据文件大小超过10KB,skip print.size:" + fileShow.toString());
            } else {
                LogHelper.v("书签数据:" + JsonUtil.toJson(chromeBookmark));
            }
            List<Bookmark> bookmarks = chromeBookmark.fetchAll();
            if (bookmarks == null) {
                LogHelper.v("bookmarks is null.");
                return result;
            }
            LogHelper.v("书签条数:" + bookmarks.size());
            return bookmarks;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (jsonReader != null) {
                jsonReader.close();
            }
        }
    }

    @Override
    public int appendBookmark(Context context, List<Bookmark> bookmarks) {
        return 0;
    }
}
