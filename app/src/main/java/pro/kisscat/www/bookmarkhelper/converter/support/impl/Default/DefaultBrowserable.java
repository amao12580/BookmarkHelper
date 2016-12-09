package pro.kisscat.www.bookmarkhelper.converter.support.impl.Default;

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

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/12/1
 * Time:9:29
 */

public class DefaultBrowserAble extends BasicBrowser {
    protected String TAG = null;
    protected String packageName = "com.android.browser";

    private final static String[] columns = new String[]{"_id", "title", "url", "folder", "parent"};

    protected List<Bookmark> fetchBookmarksList(String dbFilePath) {
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
                throw new ConverterException(ContextUtil.buildReadBookmarksTableNotExistErrorMessage(this.getName()));
            }
            cursor = sqLiteDatabase.query(false, tableName, columns, null, null, null, null, "created asc", null);
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
            LogHelper.v(TAG + ":读取书签SQLite数据库结束");
        }
        return result;
    }

    private void parseBookmarkWithFolder(Cursor cursor, List<Bookmark> result) {
        List<DefaultBookmark> bookmarks = parseBookmark(cursor);
        Map<Integer, DefaultBookmark> folders = new HashMap<>();
        for (DefaultBookmark item : bookmarks) {
            int folder = item.getIsFolder();
            if (folder == 1) {
                folders.put(item.getId(), item);
            }
        }

        for (DefaultBookmark item : bookmarks) {
            int folder = item.getIsFolder();
            if (folder == 0) {
                String folderPath = trim(parseFolderPath(folders, item.getId(), item.getParent()));
                Bookmark bookmark = new Bookmark();
                bookmark.setTitle(item.getTitle());
                bookmark.setUrl(item.getUrl());
                bookmark.setFolder(folderPath == null ? "" : folderPath);
                result.add(bookmark);
            }
        }
    }

    private String parseFolderPath(Map<Integer, DefaultBookmark> folders, Integer currentId, Integer parentId) {
        DefaultBookmark parent = folders.get(parentId);
        if (parent == null) {
            return null;
        }
        return parseFolderPath(folders, currentId, parentId, "");
    }

    private String parseFolderPath(Map<Integer, DefaultBookmark> folders, Integer currentId, Integer parentId, String path) {
        if (parentId == null || parentId <= 0) {
            return path;
        }
        if (currentId == null || currentId <= 0) {
            return path;
        }
        if (currentId.intValue() == parentId.intValue()) {
            return path;
        }
        DefaultBookmark parent = folders.get(parentId);
        if (parent == null) {
            return path;
        }
        String title = parent.getTitle();
        if (!(title == null || title.isEmpty())) {
            path = title + Path.FILE_SPLIT + path;
        }
        return parseFolderPath(folders, parent.getId(), parent.getParent(), path);
    }

    private List<DefaultBookmark> parseBookmark(Cursor cursor) {
        List<DefaultBookmark> result = new LinkedList<>();
        while (cursor.moveToNext()) {
            DefaultBookmark item = new DefaultBookmark();
            item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            item.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            item.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            item.setIsFolder(cursor.getInt(cursor.getColumnIndex("folder")));
            item.setParent(cursor.getInt(cursor.getColumnIndex("parent")));
            result.add(item);
        }
        return result;
    }

    private class DefaultBookmark extends Bookmark {
        @Getter
        @Setter
        private int id;
        @Getter
        @Setter
        private int isFolder;
        @Getter
        @Setter
        private int parent;
    }
}
