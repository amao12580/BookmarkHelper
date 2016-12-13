package pro.kisscat.www.bookmarkhelper.database.SQLite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.random.RandomUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/28
 * Time:14:25
 */

public class DBHelper {
    /**
     * read-only
     */
    public synchronized static SQLiteDatabase openReadOnlyDatabase(String dbFilePath) {
        return openDatabase(dbFilePath, true);
    }

    /**
     * read-write
     */
    public synchronized static SQLiteDatabase openDatabase(String dbFilePath) {
        return openDatabase(dbFilePath, false);
    }

    private static SQLiteDatabase openDatabase(String dbFilePath, boolean isReadOnly) {
        try {
            return SQLiteDatabase.openDatabase(dbFilePath, null, isReadOnly ? SQLiteDatabase.OPEN_READONLY : SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception e) {
            LogHelper.w("first open " + (isReadOnly ? "read-only" : "read-write") + " database error,will try agin later.");
            LogHelper.e(e);
            e.printStackTrace();
            try {
                int sleep = 50 + RandomUtil.nextInt(50);
                LogHelper.v("sleep:" + sleep);
                Thread.sleep(sleep);
            } catch (InterruptedException e1) {
                LogHelper.e("got a interruptedException.");
                LogHelper.e(e1);
                e1.printStackTrace();
                return null;
            }
            LogHelper.v("sleep completed,try agin begining.");
            try {
                return SQLiteDatabase.openDatabase(dbFilePath, null, SQLiteDatabase.OPEN_READWRITE);
            } catch (Exception e1) {
                LogHelper.e("second open " + (isReadOnly ? "read-only" : "read-write") + " database error,will throw exception." + e1.getMessage());
                LogHelper.e(e1);
                e1.printStackTrace();
                throw e1;
            }
        }
    }

    /**
     * if table exists
     */
    public static boolean checkTableExist(SQLiteDatabase sqLiteDatabase, String tableName) {
        if (sqLiteDatabase == null) {
            LogHelper.e("checkTableExist.sqLiteDatabase is null.");
            return false;
        }
        if (tableName == null || tableName.isEmpty()) {
            LogHelper.e("checkTableExist.tableName is isEmpty.");
            return false;
        }
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
