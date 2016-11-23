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

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/10
 * Time:12:27
 */

public class Qihoo360Broswer extends BasicBroswer {
    private static final String TAG = "360";
    private static final String packageName = "com.qihoo.browser";
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
        this.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_360));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.broswer_name_show_360));
    }

    private static final String filePath_origin = Path.INNER_PATH_DATA + packageName + "/";
    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + "/360/";

    @Override
    public List<Bookmark> readBookmark(Context context) {
        if (bookmarks != null) {
            LogHelper.v(TAG + ":bookmarks cache is hit.");
            return bookmarks;
        }
        LogHelper.v(TAG + ":bookmarks cache is miss.");
        LogHelper.v(TAG + ":开始读取书签数据");
        try {
            ExternalStorageUtil.mkdir(context, filePath_cp, this.getName());
            List<Bookmark> bookmarksList = new LinkedList<>();
            List<Bookmark> bookmarksListPart1 = fetchBookmarksListByUserHasLogined(context, filePath_origin);
            List<Bookmark> bookmarksListPart2 = fetchBookmarksListByNoUserLogined(context, filePath_origin);
            LogHelper.v("已登录用户书签数据:" + JsonUtil.toJson(bookmarksListPart1));
            LogHelper.v("已登录用户书签条数:" + bookmarksListPart1.size());
            LogHelper.v("未登录用户书签数据:" + JsonUtil.toJson(bookmarksListPart2));
            LogHelper.v("未登录用户书签条数:" + bookmarksListPart2.size());
            bookmarksList.addAll(bookmarksListPart1);
            bookmarksList.addAll(bookmarksListPart2);
            LogHelper.v("总的书签条数:" + bookmarksList.size());
            bookmarks = new LinkedList<>();
            fetchValidBookmarks(bookmarks, bookmarksList);
        } catch (ConverterException converterException) {
            LogHelper.e(converterException.getMessage());
            converterException.printStackTrace();
            throw converterException;
        } catch (Exception e) {
            LogHelper.e(e.getMessage());
            e.printStackTrace();
            throw new ConverterException(ContextUtil.buildReadBookmarksErrorMessage(context, this.getName()));
        } finally {
            LogHelper.v(TAG + ":读取书签数据结束");
        }
        return bookmarks;
    }

    private final static String[] columns_noLogin = new String[]{"_id", "title", "url", "folder", "parent"};
    private final static String[] columns_hasLogined = new String[]{"id", "parent_id", "is_folder", "title", "url"};

    private List<Bookmark> fetchBookmarksListByNoUserLogined(Context context, String rootDir) {
        LogHelper.v(TAG + ":开始读取未登录用户书签,rootDir:" + rootDir);
        String fileName_origin = "browser.db";
        String fileDir_origin = Path.FILE_SPLIT + "databases" + Path.FILE_SPLIT;
        String originFilePathFull = rootDir + fileDir_origin + fileName_origin;
        LogHelper.v(TAG + ":origin file path:" + originFilePathFull);
        LogHelper.v(TAG + ":tmp file path:" + filePath_cp + fileName_origin);
        ExternalStorageUtil.copyFile(context, originFilePathFull, filePath_cp + fileName_origin, this.getName());
        List<Bookmark> result = new LinkedList<>();
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        String tableName = "bookmarks";
        boolean tableExist;
        try {
            sqLiteDatabase = DBHelper.openReadOnlyDatabase(filePath_cp + fileName_origin);
            tableExist = DBHelper.checkTableExist(sqLiteDatabase, tableName);
            if (!tableExist) {
                LogHelper.v(TAG + ":database table " + tableName + " not exist.");
                throw new ConverterException(ContextUtil.buildReadBookmarksTableNotExistErrorMessage(context, this.getName()));
            }
            cursor = sqLiteDatabase.query(false, tableName, columns_noLogin, null, null, null, null, "created asc", null);
            if (cursor != null && cursor.getCount() > 0) {
                parseBookmarkWithoutLogin(cursor, result);
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (sqLiteDatabase != null) {
                sqLiteDatabase.close();
            }
            LogHelper.v(TAG + ":读取未登录书签SQLite数据库结束");
        }
        return result;
    }

    private void parseBookmarkWithoutLogin(Cursor cursor, List<Bookmark> result) {
        List<Qihoo360WithoutLoginBookmark> bookmarks = parseBookmarkWithoutLogin(cursor);
        Map<Long, Qihoo360WithoutLoginBookmark> folders = new HashMap<>();
        for (Qihoo360WithoutLoginBookmark item : bookmarks) {
            String folder = item.getFolder();
            if (folder != null && "1".equals(folder)) {
                folders.put(item.getId(), item);
            }
        }

        for (Qihoo360WithoutLoginBookmark item : bookmarks) {
            String folder = item.getFolder();
            if (folder != null && "0".equals(folder)) {
                String folderPath = trim(parseFolderPathWithoutLogin(folders, item.getParent()));
                Bookmark bookmark = new Bookmark();
                bookmark.setTitle(item.getTitle());
                bookmark.setUrl(item.getUrl());
                bookmark.setFolder(folderPath == null ? "" : folderPath);
                result.add(bookmark);
            }
        }
    }

    private void parseBookmarkWithLogined(Cursor cursor, List<Bookmark> result) {
        List<Qihoo360WithLoginedBookmark> bookmarks = parseBookmarkWithLogined(cursor);
        Map<Long, Qihoo360WithLoginedBookmark> folders = new HashMap<>();
        for (Qihoo360WithLoginedBookmark item : bookmarks) {
            int is_folder = item.getIs_folder();
            if (is_folder == 1) {
                folders.put(item.getId(), item);
            }
        }

        for (Qihoo360WithLoginedBookmark item : bookmarks) {
            int is_folder = item.getIs_folder();
            if (is_folder == 0) {
                String folderPath = trim(parseFolderPathWithLogined(folders, item.getParent_id()));
                Bookmark bookmark = new Bookmark();
                bookmark.setTitle(item.getTitle());
                bookmark.setUrl(item.getUrl());
                bookmark.setFolder(folderPath == null ? "" : folderPath);
                result.add(bookmark);
            }
        }
    }

    private String parseFolderPathWithLogined(Map<Long, Qihoo360WithLoginedBookmark> folders, long parent_id) {
        Qihoo360WithLoginedBookmark parent = folders.get(parent_id);
        if (parent == null) {
            return null;
        }
        return parseFolderPathWithLogined(folders, parent_id, "");
    }

    private String parseFolderPathWithoutLogin(Map<Long, Qihoo360WithoutLoginBookmark> folders, long parent_id) {
        Qihoo360WithoutLoginBookmark parent = folders.get(parent_id);
        if (parent == null) {
            return null;
        }
        return parseFolderPathWithoutLogin(folders, parent_id, "");
    }

    private String parseFolderPathWithLogined(Map<Long, Qihoo360WithLoginedBookmark> folders, long parent_id, String path) {
        Qihoo360WithLoginedBookmark parent = folders.get(parent_id);
        if (parent == null) {
            return path;
        }
        String title = parent.getTitle();
        if (!(title == null || title.isEmpty())) {
            path = title + Path.FILE_SPLIT + path;
        }
        return parseFolderPathWithLogined(folders, parent.getParent_id(), path);
    }

    private String parseFolderPathWithoutLogin(Map<Long, Qihoo360WithoutLoginBookmark> folders, long parent_id, String path) {
        Qihoo360WithoutLoginBookmark parent = folders.get(parent_id);
        if (parent == null) {
            return path;
        }
        String title = parent.getTitle();
        if (!(title == null || title.isEmpty())) {
            path = title + Path.FILE_SPLIT + path;
        }
        return parseFolderPathWithoutLogin(folders, parent.getParent(), path);
    }

    private List<Qihoo360WithLoginedBookmark> parseBookmarkWithLogined(Cursor cursor) {
        List<Qihoo360WithLoginedBookmark> result = new LinkedList<>();
        while (cursor.moveToNext()) {
            Qihoo360WithLoginedBookmark item = new Qihoo360WithLoginedBookmark();
            item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            item.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            item.setId(cursor.getLong(cursor.getColumnIndex("id")));
            item.setParent_id(cursor.getLong(cursor.getColumnIndex("parent_id")));
            item.setIs_folder(cursor.getInt(cursor.getColumnIndex("is_folder")));
            result.add(item);
        }
        return result;
    }

    private List<Qihoo360WithoutLoginBookmark> parseBookmarkWithoutLogin(Cursor cursor) {
        List<Qihoo360WithoutLoginBookmark> result = new LinkedList<>();
        while (cursor.moveToNext()) {
            Qihoo360WithoutLoginBookmark item = new Qihoo360WithoutLoginBookmark();
            item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            item.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            item.setFolder(cursor.getInt(cursor.getColumnIndex("folder")) + "");
            item.setId(cursor.getLong(cursor.getColumnIndex("_id")));
            item.setParent(cursor.getLong(cursor.getColumnIndex("parent")));
            result.add(item);
        }
        return result;
    }

    private class Qihoo360WithLoginedBookmark extends Bookmark {
        @Getter
        @Setter
        private long id;
        @Getter
        @Setter
        private long parent_id;
        @Getter
        @Setter
        private int is_folder;
    }

    private class Qihoo360WithoutLoginBookmark extends Bookmark {
        @Getter
        @Setter
        private long id;
        @Getter
        @Setter
        private long parent;
    }

    private List<Bookmark> fetchBookmarksListByUserHasLogined(Context context, String appRootDir) {
        LogHelper.v(TAG + ":开始读取已登录用户书签,appRootDir:" + appRootDir);
        String fileDir_origin = Path.FILE_SPLIT + "app_bookmark" + Path.FILE_SPLIT;
        String originFileDirPath = appRootDir + fileDir_origin;
        List<Bookmark> result = new LinkedList<>();
        if (!InternalStorageUtil.isExistDir(originFileDirPath)) {
            LogHelper.v(TAG + ":不存在已登录用户书签,根目录缺失");
            return result;
        }
        String regularRule = "[a-z0-9]{32}";
        String searchRule = "*.db";
        List<String> fileNames = InternalStorageUtil.lsFileAndSortByRegular(originFileDirPath, searchRule);
        if (fileNames == null || fileNames.isEmpty()) {
            LogHelper.v(TAG + ":已登录用户没有书签数据");
            return result;
        }
        List<String> targetFileNames = new LinkedList<>();
        String startKey1 = "qihoo_mobile_bookmark.";
        String startKey2 = "qihoo_online_bookmark.";
        for (String item : fileNames) {
            LogHelper.v("item:" + item);
            if (item == null) {
                LogHelper.v("item is null.");
                break;
            }
            item = getFileNameByTrimPath(appRootDir, item);
            String tmp = item;
            if (tmp.startsWith(startKey1) || tmp.startsWith(startKey2)) {
                tmp = tmp.replaceAll(startKey1, "");
                tmp = tmp.replaceAll(startKey2, "");
                tmp = tmp.replaceAll(".db", "");
                if (tmp.matches(regularRule)) {
                    LogHelper.v("match success regularRule.");
                    targetFileNames.add(item);
                    continue;
                }
            }
            LogHelper.v("not match.");
        }
        if (targetFileNames.isEmpty()) {
            LogHelper.v(TAG + ":已登录用户真的没有书签数据");
            return result;
        }
        for (String item : targetFileNames) {
            List<Bookmark> part = fetchBookmarksListByUserHasLogined(context, originFileDirPath, item);
            LogHelper.v("part:" + targetFileNames + ",size:" + part.size());
            result.addAll(part);
        }
        LogHelper.v(TAG + ":完成读取已登录用户书签,appRootDir:" + appRootDir);
        return result;
    }

    private List<Bookmark> fetchBookmarksListByUserHasLogined(Context context, String bookmarkDir, String fileName) {
        LogHelper.v(TAG + ":开始读取已登录用户书签,bookmarkDir:" + bookmarkDir + ",fileName:" + fileName);
        List<Bookmark> result = new LinkedList<>();
        String originFilePathFull = bookmarkDir + fileName;
        LogHelper.v(TAG + ":origin file path:" + originFilePathFull);
        LogHelper.v(TAG + ":tmp file path:" + filePath_cp + fileName);
        ExternalStorageUtil.copyFile(context, originFilePathFull, filePath_cp + fileName, this.getName());
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        String tableName = "tb_fav";
        boolean tableExist;
        try {
            sqLiteDatabase = DBHelper.openReadOnlyDatabase(filePath_cp + fileName);
            tableExist = DBHelper.checkTableExist(sqLiteDatabase, tableName);
            if (!tableExist) {
                LogHelper.v(TAG + ":database table " + tableName + " not exist.");
                return result;
            }
            cursor = sqLiteDatabase.query(false, tableName, columns_hasLogined, null, null, null, null, "create_time asc", null);
            if (cursor != null && cursor.getCount() > 0) {
                parseBookmarkWithLogined(cursor, result);
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (sqLiteDatabase != null) {
                sqLiteDatabase.close();
            }
            LogHelper.v(TAG + ":完成读取已登录用户书签,bookmarkDir:" + bookmarkDir + ",fileName:" + fileName);
        }
        return result;
    }

    @Override
    public int appendBookmark(Context context, List<Bookmark> bookmarks) {
        return 0;
    }
}
