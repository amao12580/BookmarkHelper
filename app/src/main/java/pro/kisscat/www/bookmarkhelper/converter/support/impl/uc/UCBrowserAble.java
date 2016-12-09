package pro.kisscat.www.bookmarkhelper.converter.support.impl.uc;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import pro.kisscat.www.bookmarkhelper.converter.support.BasicBrowser;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;
import pro.kisscat.www.bookmarkhelper.database.SQLite.DBHelper;
import pro.kisscat.www.bookmarkhelper.exception.ConverterException;
import pro.kisscat.www.bookmarkhelper.util.Path;
import pro.kisscat.www.bookmarkhelper.util.context.ContextUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.storage.ExternalStorageUtil;
import pro.kisscat.www.bookmarkhelper.util.storage.InternalStorageUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/22
 * Time:16:25
 */

public class UCBrowserAble extends BasicBrowser {
    public String TAG = null;
    public String packageName = null;
    private final static String[] columns = new String[]{"luid", "parent_id", "title", "url", "folder"};
    protected static final String fileName_origin = "bookmark.db";

    public String getPreExecuteConverterMessage() {
        return ContextUtil.buildNotSupportParseHomepageBookmarksMessage();
    }

    protected List<Bookmark> fetchBookmarksListByUserHasLogined(String dir, String filePath_cp) {
        LogHelper.v(TAG + ":开始读取已登录用户的书签SQLite数据库,root dir:" + dir);
        List<Bookmark> result = new LinkedList<>();
        String regularRule = "[1-9][0-9]{4,14}.db";//第一位1-9之间的数字，第二位0-9之间的数字，数字范围4-14个之间
        String searchRule = "*.db";
        List<String> fileNames = InternalStorageUtil.lsFileAndSortByRegular(dir, searchRule);
        if (fileNames == null || fileNames.isEmpty()) {
            LogHelper.v(TAG + ":已登录用户没有书签数据");
            return result;
        }
        String targetFilePath = null;
        String targetFileName = null;
        for (String item : fileNames) {
            LogHelper.v(TAG + ":item:" + item);
            if (item == null) {
                LogHelper.v(TAG + ":item is null.");
                break;
            }
            if (item.equals(fileName_origin)) {
                continue;
            }
            item = getFileNameByTrimPath(dir, item);
            if (item.matches(regularRule)) {
                targetFilePath = dir + item;
                targetFileName = item;
                break;
            } else {
                LogHelper.v(TAG + ":not match.");
            }
        }
        if (targetFilePath == null) {
            LogHelper.v(TAG + ":targetFilePath is miss.");
            return result;
        }
        LogHelper.v(TAG + ":targetFilePath is:" + targetFilePath);
        String tmpFilePath = filePath_cp + targetFileName;
        ExternalStorageUtil.copyFile(targetFilePath, tmpFilePath, this.getName());
        result.addAll(fetchBookmarksList(false, tmpFilePath, "bookmark", null, null, "create_time asc"));
        LogHelper.v(TAG + ":读取已登录用户书签SQLite数据库结束");
        return result;
    }

    protected List<Bookmark> fetchBookmarksListByNoUserLogined(String sourceFilePath, String dbFilePath) {
        LogHelper.v(TAG + ":开始读取未登录用户的书签SQLite数据库:" + dbFilePath);
        List<Bookmark> result = new LinkedList<>();
        LogHelper.v(TAG + ":origin file path:" + sourceFilePath);
        LogHelper.v(TAG + ":tmp file path:" + dbFilePath);
        try {
            if (InternalStorageUtil.isExistFile(sourceFilePath)) {
                ExternalStorageUtil.copyFile(sourceFilePath, dbFilePath, this.getName());
            } else {
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.e(e);
            return result;
        }
        result.addAll(fetchBookmarksList(dbFilePath, "bookmark", null, null, "create_time asc"));
        LogHelper.v(TAG + ":读取未登录用户书签SQLite数据库结束");
        return result;
    }

    protected List<Bookmark> fetchBookmarksList(String dbFilePath, String tableName, String where, String[] whereArgs, String orderBy) {
        return fetchBookmarksList(true, dbFilePath, tableName, where, whereArgs, orderBy);
    }

    protected List<Bookmark> fetchBookmarksList(boolean needThrowException, String dbFilePath, String tableName, String where, String[] whereArgs, String orderBy) {
        LogHelper.v(TAG + ":读取SQLite数据库开始,dbFilePath:" + dbFilePath + ",tableName:" + tableName);
        List<Bookmark> result = new LinkedList<>();
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        boolean tableExist;
        try {
            sqLiteDatabase = DBHelper.openReadOnlyDatabase(dbFilePath);
            tableExist = DBHelper.checkTableExist(sqLiteDatabase, tableName);
            if (!tableExist) {
                LogHelper.v(TAG + ":database table " + tableName + " not exist.");
                if (needThrowException) {
                    throw new ConverterException(ContextUtil.buildReadBookmarksTableNotExistErrorMessage(this.getName()));
                } else {
                    return result;
                }
            }
            cursor = sqLiteDatabase.query(false, tableName, columns, where, whereArgs, null, null, orderBy, null);
            if (cursor != null && cursor.getCount() > 0) {
                parseBookmarkWithFolder(cursor, result);
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (sqLiteDatabase != null) {
                sqLiteDatabase.close();
            }
            LogHelper.v(TAG + ":读取SQLite数据库结束,dbFilePath:" + dbFilePath);
        }
        return result;
    }

    private void parseBookmarkWithFolder(Cursor cursor, List<Bookmark> result) {
        List<UCBookmark> bookmarks = parseBookmark(cursor);
        Map<Long, UCBookmark> folders = new HashMap<>();
        for (UCBookmark item : bookmarks) {
            int folder = item.getIsFolder();
            if (folder == 1) {
                folders.put(item.getLuid(), item);
            }
        }

        for (UCBookmark item : bookmarks) {
            int folder = item.getIsFolder();
            if (folder == 0) {
                String folderPath = trim(parseFolderPath(folders, item.getParent_id()));
                Bookmark bookmark = new Bookmark();
                bookmark.setTitle(item.getTitle());
                bookmark.setUrl(item.getUrl());
                bookmark.setFolder(folderPath == null ? "" : folderPath);
                result.add(bookmark);
            }
        }
    }

    private String parseFolderPath(Map<Long, UCBookmark> folders, long parent_uuid) {
        UCBookmark parent = folders.get(parent_uuid);
        if (parent == null) {
            return null;
        }
        return parseFolderPath(folders, parent_uuid, "");
    }

    private String parseFolderPath(Map<Long, UCBookmark> folders, long parent_uuid, String path) {
        UCBookmark parent = folders.get(parent_uuid);
        if (parent == null) {
            return path;
        }
        String title = parent.getTitle();
        if (!(title == null || title.isEmpty())) {
            path = title + Path.FILE_SPLIT + path;
        }
        return parseFolderPath(folders, parent.getParent_id(), path);
    }

    private List<UCBookmark> parseBookmark(Cursor cursor) {
        List<UCBookmark> result = new LinkedList<>();
        while (cursor.moveToNext()) {
            UCBookmark item = new UCBookmark();
            item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            item.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            item.setLuid(cursor.getLong(cursor.getColumnIndex("luid")));
            item.setParent_id(cursor.getLong(cursor.getColumnIndex("parent_id")));
            item.setIsFolder(cursor.getInt(cursor.getColumnIndex("folder")));
            result.add(item);
        }
        return result;
    }

    private class UCBookmark extends Bookmark {
        @Getter
        @Setter
        protected long luid;
        @Getter
        @Setter
        protected long parent_id;
        @Getter
        @Setter
        protected int isFolder;
    }
}
