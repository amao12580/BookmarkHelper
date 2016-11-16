package pro.kisscat.www.bookmarkhelper.converter.support.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;

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
 * Date:2016/11/7
 * Time:15:08
 */

public class XBroswer extends BasicBroswer {
    private static final String TAG = "X";
    private static final String packageName = "com.mmbox.xbrowser";
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
        this.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_x));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.broswer_name_show_x));
    }


    private static final String fileName_origin = "mbrowser";
    private static final String filePath_origin = Path.INNER_PATH_DATA + packageName + "/databases/";
    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + "/X/";

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
            ExternalStorageUtil.copyFile(context, originFilePathFull, filePath_cp + fileName_origin, this.getName());
            List<Bookmark> bookmarksList = fetchBookmarksList(context, filePath_cp + fileName_origin);
            LogHelper.v("书签数据:" + JsonUtil.toJson(bookmarksList));
            LogHelper.v("书签条数:" + bookmarksList.size());
            bookmarks = new LinkedList<>();
            int index = 0;
            int size = bookmarksList.size();
            for (Bookmark item : bookmarksList) {
                index++;
                String bookmarkUrl = item.getUrl();
                String bookmarkTitle = item.getTitle();
                String bookmarkFolder = item.getFolder();
                if (allowPrintBookmark(index, size)) {
                    LogHelper.v("title:" + bookmarkTitle);
                    LogHelper.v("url:" + bookmarkUrl);
                }
                if (!isValidUrl(bookmarkUrl)) {
                    continue;
                }
                if (bookmarkTitle == null || bookmarkTitle.isEmpty()) {
                    LogHelper.v("url:" + bookmarkTitle + ",set to default value.");
                    bookmarkTitle = MetaData.BOOKMARK_TITLE_DEFAULT;
                }
                Bookmark bookmark = new Bookmark();
                bookmark.setTitle(bookmarkTitle);
                bookmark.setUrl(bookmarkUrl);
                if (!(bookmarkFolder == null || bookmarkFolder.isEmpty())) {
                    bookmark.setFolder(bookmarkFolder);
                }
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

    private final static String[] columns = new String[]{"title", "url", "parent"};

    private List<Bookmark> fetchBookmarksList(Context context, String dbFilePath) {
        LogHelper.v(TAG + ":开始读取书签SQLite数据库:" + dbFilePath);
        List<Bookmark> result = new LinkedList<>();
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        String tableName = "bookmark";
        boolean tableExist;
        try {
            sqLiteDatabase = DBHelper.openReadOnlyDatabase(dbFilePath);
            tableExist = DBHelper.checkTableExist(sqLiteDatabase, tableName);
            if (!tableExist) {
                LogHelper.v(TAG + ":database table " + tableName + " not exist.");
                throw new ConverterException(ContextUtil.buildReadBookmarksTableNotExistErrorMessage(context, this.getName()));
            }
            cursor = sqLiteDatabase.query(false, tableName, columns, "type=?", new String[]{"0"}, "url", null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Bookmark item = new Bookmark();
                    item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    item.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                    String folder = cursor.getString(cursor.getColumnIndex("parent"));
                    folder = trim(folder);
                    item.setFolder(folder);
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
