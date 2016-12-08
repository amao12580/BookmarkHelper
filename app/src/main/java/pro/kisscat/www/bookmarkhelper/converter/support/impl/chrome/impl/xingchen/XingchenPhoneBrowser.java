package pro.kisscat.www.bookmarkhelper.converter.support.impl.chrome.impl.xingchen;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;

import java.util.LinkedList;
import java.util.List;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.chrome.ChromeBrowserAble;
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
 * Date:2016/12/8
 * Time:17:54
 * <p>
 * 星尘浏览器-手机版
 * <p>
 * http://www.coolapk.com/apk/com.chaozhuo.browser_phone
 */

public class XingchenPhoneBrowser extends ChromeBrowserAble {
    private static final String packageName = "com.chaozhuo.browser_phone";
    private List<Bookmark> bookmarks;

    public XingchenPhoneBrowser() {
        super.TAG = "XingchenPhone";
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
        this.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_xingchen_phone));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.browser_name_show_xingchen_phone));
    }

    private static final String fileName_origin = "Bookmarks";
    private static final String fileJsonPath_origin = Path.FILE_SPLIT + "app_chromeshell" + Path.FILE_SPLIT + "Default" + Path.FILE_SPLIT;
    private static final String filePath_origin = Path.INNER_PATH_DATA + packageName;
    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + Path.FILE_SPLIT + "XingchenPhone" + Path.FILE_SPLIT;

    @Override
    public List<Bookmark> readBookmark() {
        if (bookmarks != null) {
            LogHelper.v(TAG + ":bookmarks cache is hit.");
            return bookmarks;
        }
        LogHelper.v(TAG + ":bookmarks cache is miss.");
        LogHelper.v(TAG + ":开始读取书签数据");
        try {
            String originFilePathFull = filePath_origin + fileJsonPath_origin + fileName_origin;
            LogHelper.v(TAG + ":origin file path:" + originFilePathFull);
            ExternalStorageUtil.mkdir(filePath_cp, this.getName());
            LogHelper.v(TAG + ":tmp file path:" + filePath_cp + fileName_origin);
            java.io.File file = ExternalStorageUtil.copyFile(originFilePathFull, filePath_cp + fileName_origin, this.getName());
            List<Bookmark> bookmarks = new LinkedList<>();
            List<Bookmark> bookmarksPart1 = fetchBookmarks(file);
            List<Bookmark> bookmarksPart2 = fetchBookmarksByHomePage();
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

    private List<Bookmark> fetchBookmarksByHomePage() {
        String origin_dir = filePath_origin + Path.FILE_SPLIT + "databases" + Path.FILE_SPLIT;
        String fileUserDBName_origin = "useraction.db";
        String origin_file = origin_dir + fileUserDBName_origin;
        String cp_file = filePath_cp + fileUserDBName_origin;
        LogHelper.v(TAG + ":开始读取已登录用户的书签sqlite数据库:" + origin_dir);
        List<Bookmark> result = new LinkedList<>();
        LogHelper.v(TAG + ":origin file path:" + origin_file);
        LogHelper.v(TAG + ":tmp file path:" + cp_file);
        try {
            ExternalStorageUtil.copyFile(origin_file, cp_file, this.getName());
        } catch (Exception e) {
            LogHelper.e(e.getMessage());
            return result;
        }
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        String tableName = "ntp";
        boolean tableExist;
        String[] columns = new String[]{"title", "url", "timestamp"};
        try {
            sqLiteDatabase = DBHelper.openReadOnlyDatabase(cp_file);
            tableExist = DBHelper.checkTableExist(sqLiteDatabase, tableName);
            if (!tableExist) {
                LogHelper.v(TAG + ":database table " + tableName + " not exist.");
                return result;
            }
            cursor = sqLiteDatabase.query(false, tableName, columns, null, null, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    long timestamp = cursor.getLong(cursor.getColumnIndex("timestamp"));
                    if (timestamp > 0) {
                        Bookmark item = new Bookmark();
                        item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                        item.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                        result.add(item);
                    }
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (sqLiteDatabase != null) {
                sqLiteDatabase.close();
            }
        }
        LogHelper.v(TAG + ":读取已登录用户书签sqlite数据库结束");
        return result;
    }

    @Override
    public int appendBookmark(List<Bookmark> bookmarks) {
        return 0;
    }
}
