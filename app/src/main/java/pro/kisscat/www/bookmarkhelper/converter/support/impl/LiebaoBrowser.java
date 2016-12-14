package pro.kisscat.www.bookmarkhelper.converter.support.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.converter.support.BasicBrowser;
import pro.kisscat.www.bookmarkhelper.entry.app.Bookmark;
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
 * Date:2016/11/17
 * Time:16:34
 */

public class LiebaoBrowser extends BasicBrowser {
    private static final String TAG = "Liebao";
    private static final String packageName = "com.ijinshan.browser_fast";
    private List<Bookmark> bookmarks;

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
        this.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_liebao));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.browser_name_show_liebao));
    }

    private static final String fileName_origin = "browser.db";
    private static final String filePath_origin = Path.INNER_PATH_DATA + packageName + Path.FILE_SPLIT + "databases" + Path.FILE_SPLIT;
    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + Path.FILE_SPLIT + "Liebao" + Path.FILE_SPLIT;

    @Override
    public List<Bookmark> readBookmark() {
        if (bookmarks != null) {
            LogHelper.v(TAG + ":bookmarks cache is hit.");
            return bookmarks;
        }
        LogHelper.v(TAG + ":bookmarks cache is miss.");
        LogHelper.v(TAG + ":开始读取书签数据");
        try {
            String originFilePathFull = filePath_origin + fileName_origin;
            LogHelper.v(TAG + ":origin file path:" + originFilePathFull);
            ExternalStorageUtil.mkdir(filePath_cp, this.getName());
            LogHelper.v(TAG + ":tmp file path:" + filePath_cp + fileName_origin);
            ExternalStorageUtil.copyFile(originFilePathFull, filePath_cp + fileName_origin, this.getName());
            List<Bookmark> bookmarksList = fetchBookmarksList(filePath_cp + fileName_origin);
            LogHelper.v(TAG + ":书签数据:" + JsonUtil.toJson(bookmarksList));
            LogHelper.v(TAG + ":书签条数:" + bookmarksList.size());
            bookmarks = new LinkedList<>();
            fetchValidBookmarks(bookmarks, bookmarksList);
        } catch (ConverterException converterException) {
            LogHelper.e(converterException);
            converterException.printStackTrace();
            throw converterException;
        } catch (Exception e) {
            LogHelper.e(e);
            e.printStackTrace();
            throw new ConverterException(ContextUtil.buildReadBookmarksErrorMessage(this.getName()));
        } finally {
            LogHelper.v(TAG + ":读取书签数据结束");
        }
        return bookmarks;
    }

    private final static String[] columns_boomark = new String[]{"title", "url", "folder_id"};
    private final static String[] columns_folder = new String[]{"_id", "title", "parent"};

    private List<Bookmark> fetchBookmarksList(String dbFilePath) {
        LogHelper.v(TAG + ":开始读取书签SQLite数据库:" + dbFilePath);
        List<Bookmark> result = new LinkedList<>();
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        String tableName = "bookmarks";
        boolean tableExist;
        try {
            sqLiteDatabase = DBHelper.openReadOnlyDatabase(dbFilePath);
            Map<Integer, LiebaoFolder> folders = fetchAllFolder(sqLiteDatabase);
            tableExist = DBHelper.checkTableExist(sqLiteDatabase, tableName);
            if (!tableExist) {
                LogHelper.v(TAG + ":database table " + tableName + " not exist.");
                throw new ConverterException(ContextUtil.buildReadBookmarksTableNotExistErrorMessage(this.getName()));
            }
            cursor = sqLiteDatabase.query(false, tableName, columns_boomark, "bookmark=?", new String[]{"1"}, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Bookmark item = new Bookmark();
                    item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    item.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                    Integer folderId = cursor.getInt(cursor.getColumnIndex("folder_id"));
                    String folderPath = trim(parseFolderPath(folderId, folders));
                    item.setFolder(folderPath == null ? "" : folderPath);
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

    private String parseFolderPath(Integer folderId, Map<Integer, LiebaoFolder> folders) {
        if (folderId == null || folderId <= 0) {
            return null;
        }
        return parseFolderPath(folders, folderId, "");
    }

    private String parseFolderPath(Map<Integer, LiebaoFolder> folders, Integer parentId, String path) {
        if (parentId == null || parentId <= 0) {
            return path;
        }
        LiebaoFolder parent = folders.get(parentId);
        if (parent == null) {
            return path;
        }
        String title = parent.getTitle();
        if (!(title == null || title.isEmpty())) {
            path = title + Path.FILE_SPLIT + path;
        }
        return parseFolderPath(folders, parent.getParent(), path);
    }

    private Map<Integer, LiebaoFolder> fetchAllFolder(SQLiteDatabase sqLiteDatabase) {
        Map<Integer, LiebaoFolder> result = new HashMap<>();
        Cursor cursor = null;
        String tableName = "folders";
        boolean tableExist;
        try {
            tableExist = DBHelper.checkTableExist(sqLiteDatabase, tableName);
            if (!tableExist) {
                LogHelper.v(TAG + ":database table " + tableName + " not exist.");
                throw new ConverterException(ContextUtil.buildReadBookmarksTableNotExistErrorMessage(this.getName()));
            }
            cursor = sqLiteDatabase.query(false, tableName, columns_folder, null, null, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    LiebaoFolder item = new LiebaoFolder();
                    item.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                    item.setParent(cursor.getInt(cursor.getColumnIndex("parent")));
                    item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    result.put(item.getId(), item);
                }
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    private class LiebaoFolder {
        @Getter
        @Setter
        private int id;
        @Getter
        @Setter
        private String title;
        @Getter
        @Setter
        private int parent;

    }

    @Override
    public int appendBookmark(List<Bookmark> bookmarks) {
        return 0;
    }
}
