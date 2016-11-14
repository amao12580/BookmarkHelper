package pro.kisscat.www.bookmarkhelper.database.SQLite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/28
 * Time:14:25
 */

public class DBHelper {
    public synchronized static SQLiteDatabase openReadOnlyDatabase(String dbFilePath) {
        try {
            return SQLiteDatabase.openDatabase(dbFilePath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception e) {
            LogHelper.e(MetaData.LOG_E_DEFAULT, e.getMessage());
            throw e;
        }
    }

    public synchronized static SQLiteDatabase openDatabase(String dbFilePath) {
        try {
            return SQLiteDatabase.openDatabase(dbFilePath, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception e) {
            LogHelper.e(MetaData.LOG_E_DEFAULT, e.getMessage());
            throw e;
        }
    }

    public static boolean checkTableExist(SQLiteDatabase sqLiteDatabase, String tableName) {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='" + tableName + "'", null);
        if (cursor.moveToNext()) {
            int count = cursor.getInt(0);
            if (count > 0) {
                return true;
            }
        }
        cursor.close();
        return false;
    }
}
