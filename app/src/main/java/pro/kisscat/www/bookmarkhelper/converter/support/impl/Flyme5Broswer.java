package pro.kisscat.www.bookmarkhelper.converter.support.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;
import pro.kisscat.www.bookmarkhelper.converter.support.BasicBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;
import pro.kisscat.www.bookmarkhelper.database.SQLite.DBHelper;
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

public class Flyme5Broswer extends BasicBroswer {
    private static final String TAG = "Flyme5";
    private static final String packageName = "com.android.browser";
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
        this.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_flyme5));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.broswer_name_show_flyme5));
    }


    private static final String fileName_origin = "browser2.db";
    private static final String filePath_origin = Path.INNER_PATH_DATA + packageName + "/databases/";
    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + "/Flyme5/";

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
            File cpPath = new File(filePath_cp);
            cpPath.deleteOnExit();
            cpPath.mkdirs();
            LogHelper.v(TAG + ":tmp file path:" + filePath_cp + fileName_origin);
            ExternalStorageUtil.copyFile(context, originFilePathFull, filePath_cp + fileName_origin, this.getName());
            List<Bookmark> bookmarksList = fetchBookmarksList(context, filePath_cp + fileName_origin);
            LogHelper.v("书签数据:" + JsonUtil.toJson(bookmarksList));
            LogHelper.v("书签条数:" + bookmarksList.size());
            bookmarks = new LinkedList<>();
            for (Bookmark item : bookmarksList) {
                String bookmarkUrl = item.getUrl();
                String bookmarkName = item.getTitle();
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
        } catch (ConverterException converterException) {
            converterException.printStackTrace();
            LogHelper.e(MetaData.LOG_E_DEFAULT, converterException.getMessage());
            throw converterException;
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.e(MetaData.LOG_E_DEFAULT, e.getMessage());
            throw new ConverterException(ContextUtil.buildReadBookmarksErrorMessage(context, this.getName()));
        } finally {
            LogHelper.v(TAG + ":读取书签数据结束");
        }
        return bookmarks;
    }

    private final static String[] columns = new String[]{"title", "url"};

    private List<Bookmark> fetchBookmarksList(Context context, String dbFilePath) {
        LogHelper.v(TAG + ":开始读取书签SQLite数据库:" + dbFilePath);
        List<Bookmark> result = new LinkedList<>();
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        String tableName = "bookmarks";
        boolean tableExist;
        try {
            sqLiteDatabase = DBHelper.openReadOnlyDatabase(dbFilePath);
            tableExist = DBHelper.checkTableExist(sqLiteDatabase, tableName);
            if (!tableExist) {
                LogHelper.v(TAG + ":database table " + tableName + " not exist.");
                throw new ConverterException(ContextUtil.buildReadBookmarksTableNotExistErrorMessage(context, this.getName()));
            }
            cursor = sqLiteDatabase.query(false, tableName, columns, null, null, "url", null, "created asc", null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Bookmark item = new Bookmark();
                    item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    item.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                    result.add(item);
                }
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (sqLiteDatabase != null) {
                sqLiteDatabase.close();
            }
            LogHelper.v(TAG + ":读取书签SQLite数据库结束");
        }
        return result;
    }

    @Override
    public int appendBookmark(Context context, List<Bookmark> bookmarks) {
        return 0;
    }
}
