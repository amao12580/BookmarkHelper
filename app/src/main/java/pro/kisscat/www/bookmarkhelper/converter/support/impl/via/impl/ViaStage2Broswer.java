package pro.kisscat.www.bookmarkhelper.converter.support.impl.via.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.via.ViaBroswerable;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.via.ViaBookmark;
import pro.kisscat.www.bookmarkhelper.database.SQLite.DBHelper;
import pro.kisscat.www.bookmarkhelper.exception.ConverterException;
import pro.kisscat.www.bookmarkhelper.util.Path;
import pro.kisscat.www.bookmarkhelper.util.context.ContextUtil;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.storage.ExternalStorageUtil;
import pro.kisscat.www.bookmarkhelper.util.storage.InternalStorageUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/14
 * Time:13:18
 * <p>
 * versionName>=2.1.1  && versionCode>=20161113
 * <p>
 * stage2较stage1主要是书签存取由json txt转变为sqlite3
 */

public class ViaStage2Broswer extends ViaBroswerable {
    private static final String TAG = "ViaStage2";
    private static final String fileName_origin = "via";
    private static final String filePath_origin = Path.INNER_PATH_DATA + packageName + "/databases/";
    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + "/Via/";

    @Override
    public List<Bookmark> readBookmark(Context context) {
        if (bookmarks != null) {
            LogHelper.v(TAG + ":bookmarks cache is hit.");
            return bookmarks;
        }
        LogHelper.v(TAG + ":bookmarks cache is miss.");
        LogHelper.v(TAG + ":开始读取书签数据");
        try {
            String originFilePath = filePath_origin + fileName_origin;
            LogHelper.v(TAG + ":origin file path:" + originFilePath);
            boolean isExist = InternalStorageUtil.isExistFile(originFilePath);
            if (!isExist) {
                throw new ConverterException(ContextUtil.buildViaBookmarksFileMiss(context, this.getName()));
            }
            File cpPath = new File(filePath_cp);
            cpPath.deleteOnExit();
            cpPath.mkdirs();
            String tmpFilePath = filePath_cp + fileName_origin;
            LogHelper.v(TAG + ":tmp file path:" + tmpFilePath);
            ExternalStorageUtil.copyFile(context, originFilePath, tmpFilePath, this.getName());
            List<ViaBookmark> bookmarksList = fetchBookmarksList(context, tmpFilePath);
            LogHelper.v("书签数据:" + JsonUtil.toJson(bookmarksList));
            LogHelper.v("书签条数:" + bookmarksList.size());
            bookmarks = new LinkedList<>();
            int index = 0;
            int size = bookmarksList.size();
            for (ViaBookmark item : bookmarksList) {
                index++;
                String bookmarkUrl = item.getUrl();
                String bookmarkTitle = item.getTitle();
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

    private final static String[] columns = new String[]{"id", "url", "title", "folder"};
    private final static String tableName = "bookmarks";
    private Integer latestIndex = null;

    private List<ViaBookmark> fetchBookmarksList(Context context, String dbFilePath) {
        LogHelper.v(TAG + ":开始读取书签SQLite数据库:" + dbFilePath);
        List<ViaBookmark> result = new LinkedList<>();
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        boolean tableExist;
        try {
            sqLiteDatabase = DBHelper.openReadOnlyDatabase(dbFilePath);
            tableExist = DBHelper.checkTableExist(sqLiteDatabase, tableName);
            if (!tableExist) {
                LogHelper.v(TAG + ":database table " + tableName + " not exist.");
                throw new ConverterException(ContextUtil.buildReadBookmarksTableNotExistErrorMessage(context, this.getName()));
            }
            cursor = sqLiteDatabase.query(false, tableName, columns, null, null, "url", null, "id asc", null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ViaBookmark item = new ViaBookmark();
                    item.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                    item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    item.setFolder(cursor.getString(cursor.getColumnIndex("folder")));
                    item.setOrder(cursor.getInt(cursor.getColumnIndex("id")));
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
        if (!result.isEmpty()) {
            latestIndex = result.get(result.size() - 1).getOrder();
        } else {
            latestIndex = 1;
        }
        return result;
    }

    @Override
    public int appendBookmark(Context context, List<Bookmark> appends) {
        LogHelper.v(TAG + ":开始合并书签数据，bookmarks appends size:" + appends.size());
        int successCount = 0;
        SQLiteDatabase sqLiteDatabase = null;
        try {
            List<Bookmark> exists = this.readBookmark(context);
            Set<Bookmark> increment = buildNoRepeat(appends, exists);
            LogHelper.v(TAG + ":bookmarks increment size:" + increment.size());
            if (increment.isEmpty()) {
                return 0;
            }
            String originFilePath = filePath_origin + fileName_origin;
            String tmpFilePath = filePath_cp + fileName_origin;
            LogHelper.v(TAG + ":tmp file path:" + tmpFilePath);
            sqLiteDatabase = DBHelper.openDatabase(tmpFilePath);
            int count = 0;
            for (Bookmark item : increment) {
                latestIndex++;
                ContentValues cv = new ContentValues();
                cv.put("id", latestIndex);
                cv.put("url", item.getUrl());
                cv.put("title", item.getTitle());
                cv.put("folder", item.getFolder());
                long ret = sqLiteDatabase.insert(tableName, null, cv);
                if (ret <= -1) {
                    LogHelper.e(MetaData.LOG_E_DEFAULT, "database insert error");
                    continue;
                }
                count++;
            }
            ExternalStorageUtil.copyFile(context, tmpFilePath, originFilePath, this.getName());
            String cleanFilePath = filePath_origin + "bookmarks.html";//干掉这个缓存文件，以便via重新生成书签页面
            InternalStorageUtil.deleteFile(context, cleanFilePath, this.getName());
            successCount = count;
        } catch (ConverterException converterException) {
            converterException.printStackTrace();
            LogHelper.e(MetaData.LOG_E_DEFAULT, converterException.getMessage());
            throw converterException;
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.e(MetaData.LOG_E_DEFAULT, e.getMessage());
            throw new ConverterException(ContextUtil.buildAppendBookmarksErrorMessage(context, this.getName()));
        } finally {
            if (sqLiteDatabase != null) {
                sqLiteDatabase.close();
            }
            this.bookmarks = null;
            this.latestIndex = null;
            LogHelper.v(TAG + ":合并书签数据结束");
        }
        return successCount;
    }
}
