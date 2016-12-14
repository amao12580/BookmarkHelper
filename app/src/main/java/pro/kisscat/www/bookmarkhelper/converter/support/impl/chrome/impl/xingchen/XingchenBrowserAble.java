package pro.kisscat.www.bookmarkhelper.converter.support.impl.chrome.impl.xingchen;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import pro.kisscat.www.bookmarkhelper.converter.support.impl.chrome.ChromeBrowserAble;
import pro.kisscat.www.bookmarkhelper.entry.app.Bookmark;
import pro.kisscat.www.bookmarkhelper.database.SQLite.DBHelper;
import pro.kisscat.www.bookmarkhelper.util.Path;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.storage.ExternalStorageUtil;
import pro.kisscat.www.bookmarkhelper.util.storage.InternalStorageUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/12/8
 * Time:17:54
 * <p>
 * 星尘浏览器
 */

public class XingchenBrowserAble extends ChromeBrowserAble {

    protected List<Bookmark> fetchBookmarksByHomePage(String filePath_origin, String filePath_cp) {
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

    protected List<Bookmark> fetchBookmarks(String originPathDir, String originFileName, String cpPath) {
        List<Bookmark> result = new LinkedList<>();
        String originFilePathFull = originPathDir + originFileName;
        if (InternalStorageUtil.isExistFile(originFilePathFull)) {
            LogHelper.v(TAG + ":origin file path:" + originFilePathFull);
            ExternalStorageUtil.mkdir(cpPath, this.getName());
            LogHelper.v(TAG + ":tmp file path:" + cpPath + originFileName);
            java.io.File file = ExternalStorageUtil.copyFile(originFilePathFull, cpPath + originFileName, this.getName());
            try {
                return fetchBookmarks(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                LogHelper.e(e);
                return result;
            }
        } else {
            return result;
        }
    }
}
