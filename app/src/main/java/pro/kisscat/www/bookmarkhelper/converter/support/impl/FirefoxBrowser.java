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
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;
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
 * Date:2016/11/7
 * Time:15:08
 */

public class FirefoxBrowser extends BasicBrowser {
    private static final String TAG = "Firefox";
    //    private static final String packageName = "org.mozilla.fennec";//每夜版
    private static final String packageName = "org.mozilla.firefox";//正式版
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
        this.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_firefox));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.browser_name_show_firefox));
    }


    private static final String fileName_origin = "browser.db";
    private static final String filePath_origin = Path.INNER_PATH_DATA + packageName + "/files/mozilla/";
    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + "/Firefox/";

    @Override
    public List<Bookmark> readBookmark() {
        if (bookmarks != null) {
            LogHelper.v(TAG + ":bookmarks cache is hit.");
            return bookmarks;
        }
        LogHelper.v(TAG + ":bookmarks cache is miss.");
        LogHelper.v(TAG + ":开始读取书签数据");
        try {
            ExternalStorageUtil.mkdir(filePath_cp, this.getName());
            List<Bookmark> bookmarksList = fetchBookmarksList(filePath_origin);
            LogHelper.v(TAG + ":书签数据:" + JsonUtil.toJson(bookmarksList));
            LogHelper.v(TAG + ":书签条数:" + bookmarksList.size());
            bookmarks = new LinkedList<>();
            fetchValidBookmarks(bookmarks, bookmarksList);
        } catch (ConverterException converterException) {
            converterException.printStackTrace();
            LogHelper.e(converterException);
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

    private final static String[] columns = new String[]{"_id", "title", "url", "type", "parent"};

    private List<Bookmark> fetchBookmarksList(String dir) {
        List<Bookmark> result = new LinkedList<>();
        List<String> dirs = InternalStorageUtil.lsDir(dir);
        if (dirs == null || dirs.isEmpty()) {
            LogHelper.v(TAG + ":没有找到目标文件夹");
            return result;
        }
        String targetDir = null;
        String rule = "[a-z0-9]{8}";
        String endKey = ".default";
        for (String item : dirs) {
            if (item != null) {
                if (endKey.endsWith(endKey)) {
                    String tmp = item.replace(endKey, "");
                    if (tmp.matches(rule)) {
                        targetDir = item;
                        break;
                    }
                }
            }
        }
        if (targetDir == null || targetDir.isEmpty()) {
            LogHelper.v(TAG + ":真的没有找到目标文件夹");
            return result;
        } else {
            LogHelper.v(TAG + ":targetDir:" + targetDir);
        }
        dir += targetDir + Path.FILE_SPLIT;
        String sourceFilePath = dir + fileName_origin;
        LogHelper.v(TAG + ":sourceFilePath:" + sourceFilePath);
        ExternalStorageUtil.copyFile(sourceFilePath, filePath_cp + fileName_origin, this.getName());
        LogHelper.v(TAG + ":targetFilePath:" + filePath_cp + fileName_origin);
        return fetchBookmarksList(filePath_cp, fileName_origin);
    }

    private List<Bookmark> fetchBookmarksList(String dir, String fileName) {
        String dbFilePath = dir + fileName;
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
            cursor = sqLiteDatabase.query(false, tableName, columns, null, null, null, null, null, null);
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
        List<FirefoxBookmark> bookmarks = parseBookmark(cursor);
        Map<Long, FirefoxBookmark> folders = new HashMap<>();
        for (FirefoxBookmark item : bookmarks) {
            int type = item.getType();
            String title = item.getTitle();
            if (title != null && !title.isEmpty() && type == 0) {
                folders.put(item.getId(), item);
            }
        }

        for (FirefoxBookmark item : bookmarks) {
            int type = item.getType();
            if (type == 1) {
                String folderPath = trim(parseFolderPath(folders, item.getParent()));
                Bookmark bookmark = new Bookmark();
                bookmark.setTitle(item.getTitle());
                bookmark.setUrl(item.getUrl());
                bookmark.setFolder(folderPath == null ? "" : folderPath);
                result.add(bookmark);
            }
        }
    }

    private String parseFolderPath(Map<Long, FirefoxBookmark> folders, long parent_uuid) {
        FirefoxBookmark parent = folders.get(parent_uuid);
        if (parent == null) {
            return null;
        }
        return parseFolderPath(folders, parent_uuid, "");
    }

    private String parseFolderPath(Map<Long, FirefoxBookmark> folders, long parent_uuid, String path) {
        FirefoxBookmark parent = folders.get(parent_uuid);
        if (parent == null) {
            return path;
        }
        String title = parent.getTitle();
        if (!(title == null || title.isEmpty() || "root".equals(title))) {
            path = title + Path.FILE_SPLIT + path;
        }
        return parseFolderPath(folders, parent.getParent(), path);
    }

    private List<FirefoxBookmark> parseBookmark(Cursor cursor) {
        List<FirefoxBookmark> result = new LinkedList<>();
        while (cursor.moveToNext()) {
            FirefoxBookmark item = new FirefoxBookmark();
            item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            item.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            item.setId(cursor.getLong(cursor.getColumnIndex("_id")));
            item.setParent(cursor.getLong(cursor.getColumnIndex("parent")));
            item.setType(cursor.getInt(cursor.getColumnIndex("type")));
            result.add(item);
        }
        return result;
    }

    private class FirefoxBookmark extends Bookmark {
        @Getter
        @Setter
        private long id;
        @Getter
        @Setter
        private long parent;
        @Getter
        @Setter
        private int type;
    }

    @Override
    public int appendBookmark(List<Bookmark> bookmarks) {
        return 0;
    }
}
