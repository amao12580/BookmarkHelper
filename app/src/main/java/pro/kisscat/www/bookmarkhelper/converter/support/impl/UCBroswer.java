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
import pro.kisscat.www.bookmarkhelper.converter.support.BasicBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;
import pro.kisscat.www.bookmarkhelper.database.SQLite.DBHelper;
import pro.kisscat.www.bookmarkhelper.exception.ConverterException;
import pro.kisscat.www.bookmarkhelper.util.Path;
import pro.kisscat.www.bookmarkhelper.util.context.ContextUtil;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.storage.ExternalStorageUtil;
import pro.kisscat.www.bookmarkhelper.util.storage.InternalStorageUtil;
import pro.kisscat.www.bookmarkhelper.util.toast.ToastUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/1
 * Time:9:20
 */

public class UCBroswer extends BasicBroswer {
    private static final String TAG = "UC";
    private static final String packageName = "com.UCMobile";
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
        this.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_uc));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.broswer_name_show_uc));
    }

    private static final String fileName_origin = "bookmark.db";
    private static final String filePath_origin = Path.INNER_PATH_DATA + packageName + "/databases/";
    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + "/UC/";
    private static String notSupportParseHomepageBookmarks;

    @Override
    public List<Bookmark> readBookmark(Context context) {
        if (bookmarks != null) {
            LogHelper.v(TAG + ":bookmarks cache is hit.");
            return bookmarks;
        }
        LogHelper.v(TAG + ":bookmarks cache is miss.");
        LogHelper.v(TAG + ":开始读取书签数据");
        try {
            if (notSupportParseHomepageBookmarks == null) {
                notSupportParseHomepageBookmarks = context.getResources().getString(R.string.notSupportParseHomepageBookmarks);
            }
            ToastUtil.showMessage(context, notSupportParseHomepageBookmarks);
            ExternalStorageUtil.mkdir(context, filePath_cp, this.getName());
            List<Bookmark> bookmarksList = new LinkedList<>();
            List<Bookmark> bookmarksListPart1 = fetchBookmarksListByUserHasLogined(context, filePath_origin);
            List<Bookmark> bookmarksListPart2 = fetchBookmarksListByNoUserLogined(context, filePath_cp + fileName_origin);
            LogHelper.v("已登录的用户书签数据:" + JsonUtil.toJson(bookmarksListPart1));
            LogHelper.v("已登录的用户书签条数:" + bookmarksListPart1.size());
            LogHelper.v("未登录的用户书签数据:" + JsonUtil.toJson(bookmarksListPart2));
            LogHelper.v("未登录的用户书签条数:" + bookmarksListPart2.size());
            bookmarksList.addAll(bookmarksListPart1);
            bookmarksList.addAll(bookmarksListPart2);
            LogHelper.v("总的书签条数:" + bookmarksList.size());
            bookmarks = new LinkedList<>();
            fetchValidBookmarks(bookmarks, bookmarksList);
            LogHelper.v("result:" + JsonUtil.toJson(bookmarks));
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.e(e.getMessage());
            throw new ConverterException(ContextUtil.buildReadBookmarksErrorMessage(context, this.getName()));
        } finally {
            LogHelper.v(TAG + ":读取书签数据结束");
        }
        return bookmarks;
    }

    private final static String[] columns = new String[]{"luid", "parent_id", "title", "url", "folder"};

    private List<Bookmark> fetchBookmarksListByNoUserLogined(Context context, String dbFilePath) {
        LogHelper.v(TAG + ":开始读取未登录用户的书签SQLite数据库:" + dbFilePath);
        List<Bookmark> result = new LinkedList<>();
        String originFilePathFull = filePath_origin + fileName_origin;
        LogHelper.v(TAG + ":origin file path:" + originFilePathFull);
        LogHelper.v(TAG + ":tmp file path:" + dbFilePath);
        try {
            ExternalStorageUtil.copyFile(context, originFilePathFull, dbFilePath, this.getName());
        } catch (Exception e) {
            LogHelper.e(e.getMessage());
            return result;
        }
        result.addAll(fetchBookmarksList(context, dbFilePath, "bookmark", null, null, "create_time asc"));
        LogHelper.v(TAG + ":读取未登录用户书签SQLite数据库结束");
        return result;
    }

    private List<Bookmark> fetchBookmarksListByUserHasLogined(Context context, String dir) {
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
            LogHelper.v("item:" + item);
            if (item == null) {
                LogHelper.v("item is null.");
                break;
            }
            if (item.equals(fileName_origin)) {
                continue;
            }
            if (item.matches(regularRule)) {
                targetFilePath = dir + item;
                targetFileName = item;
                break;
            } else {
                LogHelper.v("not match.");
            }
        }
        if (targetFilePath == null) {
            LogHelper.v("targetFilePath is miss.");
            return result;
        }
        LogHelper.v("targetFilePath is:" + targetFilePath);
        String tmpFilePath = filePath_cp + targetFileName;
        ExternalStorageUtil.copyFile(context, targetFilePath, tmpFilePath, this.getName());
        result.addAll(fetchBookmarksList(false, context, tmpFilePath, "bookmark", null, null, "create_time asc"));
        LogHelper.v(TAG + ":读取已登录用户书签SQLite数据库结束");
        return result;
    }

    private List<Bookmark> fetchBookmarksList(Context context, String dbFilePath, String tableName, String where, String[] whereArgs, String orderBy) {
        return fetchBookmarksList(true, context, dbFilePath, tableName, where, whereArgs, orderBy);
    }

    private List<Bookmark> fetchBookmarksList(boolean needThrowException, Context context, String dbFilePath, String tableName, String where, String[] whereArgs, String orderBy) {
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
                    throw new ConverterException(ContextUtil.buildReadBookmarksTableNotExistErrorMessage(context, this.getName()));
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
        private long luid;
        @Getter
        @Setter
        private long parent_id;
        @Getter
        @Setter
        private int isFolder;
    }

    @Override
    public int appendBookmark(Context context, List<Bookmark> bookmarks) {
        return 0;
    }
}
