package pro.kisscat.www.bookmarkhelper.converter.support.impl;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.alibaba.fastjson.JSONReader;

import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;
import pro.kisscat.www.bookmarkhelper.converter.support.BasicBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.chrome.ChromeBookmark;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.chrome.module.Children;
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
        JSONReader jsonReader = null;
        LogHelper.v(TAG + ":开始读取书签数据");
        try {
            String originFilePathFull = filePath_origin + fileName_origin;
            LogHelper.v(TAG + ":origin file path:" + originFilePathFull);
            File cpPath = new File(filePath_cp);
            cpPath.deleteOnExit();
            cpPath.mkdirs();
            LogHelper.v(TAG + ":tmp file path:" + filePath_cp + fileName_origin);
            File file = ExternalStorageUtil.copyFile(context, originFilePathFull, filePath_cp + fileName_origin, this.getName());
            jsonReader = new JSONReader(new FileReader(file));
            bookmarks = new LinkedList<>();
            ChromeBookmark chromeBookmark = jsonReader.readObject(ChromeBookmark.class);
            if (chromeBookmark == null) {
                LogHelper.v("chromeBookmark is null.");
                return bookmarks;
            }
            LogHelper.v("书签数据:" + JsonUtil.toJson(chromeBookmark));
            List<Children> childrens = chromeBookmark.getChildren();
            if (childrens == null) {
                LogHelper.v("childrens is null.");
                return bookmarks;
            }
            LogHelper.v("书签条数:" + childrens.size());
            for (Children item : childrens) {
                String bookmarkUrl = item.getUrl();
                String bookmarkName = item.getName();
                LogHelper.v("name:" + bookmarkName);
                LogHelper.v("url:" + bookmarkUrl);
                if (!isGoodUrl(bookmarkUrl)) {
                    continue;
                }
                if (bookmarkName == null || bookmarkName.isEmpty()) {
                    LogHelper.v("url:" + bookmarkName + ",set to default value.");
                    bookmarkName = MetaData.BOOKMARK_TITLE_DEFAULT;
                }
                Bookmark bookmark = new Bookmark();
                bookmark.setTitle(bookmarkName);
                bookmark.setUrl(bookmarkUrl);
                bookmarks.add(bookmark);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.e(MetaData.LOG_E_DEFAULT, e.getMessage());
            throw new ConverterException(ContextUtil.buildReadBookmarksErrorMessage(context, this.getName()));
        } finally {
            if (jsonReader != null) {
                jsonReader.close();
            }
            LogHelper.v(TAG + ":读取书签数据结束");
        }
        return bookmarks;
    }

    @Override
    public int appendBookmark(Context context, List<Bookmark> bookmarks) {
        return 0;
    }
}
