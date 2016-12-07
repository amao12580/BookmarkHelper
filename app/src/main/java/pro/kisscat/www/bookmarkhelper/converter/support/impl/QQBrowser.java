package pro.kisscat.www.bookmarkhelper.converter.support.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
 * Date:2016/10/9
 * Time:15:38
 */

public class QQBrowser extends BasicBrowser {
    private static final String TAG = "QQ";
    private static final String packageName = "com.tencent.mtt";
    private final static String[] columns = new String[]{"title", "url", "folder", "parent_uuid", "uuid"};
    private final static String[] columns_snapshot = new String[]{"title", "url", "parent_uuid", "uuid"};
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
        this.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_qq));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.browser_name_show_qq));
    }


    private static final String fileName_origin = "default_user.db";
    private static final String filePath_origin = Path.INNER_PATH_DATA + packageName + "/databases/";
    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + "/QQ/";

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
            List<Bookmark> bookmarksList = new LinkedList<>();
            List<Bookmark> bookmarksListPart1 = fetchBookmarksListByUserHasLogined(filePath_origin);
            List<Bookmark> bookmarksListPart2 = fetchBookmarksListByNoUserLogined(filePath_cp + fileName_origin);
            LogHelper.v(TAG + ":已登录用户书签数据:" + JsonUtil.toJson(bookmarksListPart1));
            LogHelper.v(TAG + ":已登录用户书签条数:" + bookmarksListPart1.size());
            LogHelper.v(TAG + ":未登录的用户书签数据:" + JsonUtil.toJson(bookmarksListPart2));
            LogHelper.v(TAG + ":未登录的用户书签条数:" + bookmarksListPart2.size());
            bookmarksList.addAll(bookmarksListPart1);
            bookmarksList.addAll(bookmarksListPart2);
            LogHelper.v(TAG + ":总的书签条数:" + bookmarksList.size());
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

    private List<Bookmark> fetchBookmarksListByUserHasLogined(String dir) {
        LogHelper.v(TAG + ":开始读取已登录用户的书签SQLite数据库,root dir:" + dir);
        List<Bookmark> result = new LinkedList<>();
        LogHelper.v(TAG + ":尝试解析已登录用户的书签数据");
        String QQRegularRule = "[1-9][0-9]{4,14}";//第一位1-9之间的数字，第二位0-9之间的数字，数字范围4-14个之间
        String WechatRegularRule = "[a-z0-9A-Z\\-]{28}";
        String searchRule = "*.db";
        List<String> fileNames = InternalStorageUtil.lsFileAndSortByRegular(dir, searchRule);
        if (fileNames == null || fileNames.isEmpty()) {
            LogHelper.v(TAG + ":已登录用户没有书签数据");
            return result;
        }
        Map<String, String> QQUser = new LinkedHashMap<>();
        Map<String, String> WechatUser = new LinkedHashMap<>();
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
            String name = item.replaceAll(".db", "");
            if (name.matches(QQRegularRule)) {
                LogHelper.v(TAG + ":match success QQRegularRule.");
                QQUser.put(name, item);

            } else {
                LogHelper.v(TAG + ":not match QQRegularRule.");
            }
            if (name.matches(WechatRegularRule)) {
                LogHelper.v(TAG + ":match success WechatRegularRule.");
                WechatUser.put(name, item);
            } else {
                LogHelper.v(TAG + ":not match WechatRegularRule.");
            }
        }
        for (Map.Entry<String, String> entry : QQUser.entrySet()) {
            String name = entry.getKey();
            String item = entry.getValue();
            List<Bookmark> QQUserBookmarks = fetchBookmarksListByUserHasLogined(dir, item);
            LogHelper.v(TAG + ":QQ用户：" + name + "，书签条数：" + QQUserBookmarks.size());
            result.addAll(QQUserBookmarks);
        }
        for (Map.Entry<String, String> entry : WechatUser.entrySet()) {
            String name = entry.getKey();
            String item = entry.getValue();
            List<Bookmark> WechatUserBookmarks = fetchBookmarksListByUserHasLogined(dir, item);
            LogHelper.v(TAG + ":微信用户：" + name + "，书签条数：" + WechatUserBookmarks.size());
            result.addAll(WechatUserBookmarks);
        }
        LogHelper.v(TAG + ":读取已登录用户书签SQLite数据库结束");
        return result;
    }

    private List<Bookmark> fetchBookmarksListByUserHasLogined(String dir, String fileName) {
        String targetFilePath = dir + fileName;
        LogHelper.v(TAG + ":targetFilePath is:" + targetFilePath);
        List<Bookmark> result = new LinkedList<>();
        String tmpFilePath = filePath_cp + fileName;
        try {
            ExternalStorageUtil.copyFile(targetFilePath, tmpFilePath, this.getName());
        } catch (Exception e) {
            LogHelper.e(e.getMessage());
            return result;
        }
        result.addAll(fetchBookmarksList(false, tmpFilePath, "mtt_bookmarks", null, null, "created asc"));
        result.addAll(fetchBookmarksList(false, tmpFilePath, "pad_bookmark", null, null, "created asc"));
        result.addAll(fetchBookmarksList(false, tmpFilePath, "pc_bookmark", null, null, "created asc"));
        result.addAll(fetchBookmarksList(false, tmpFilePath, "snapshot", columns_snapshot, "type=?", new String[]{"-1"}, null));
        return result;
    }

    private List<Bookmark> fetchBookmarksListByNoUserLogined(String dbFilePath) {
        LogHelper.v(TAG + ":开始读取未登录用户的书签SQLite数据库:" + dbFilePath);
        List<Bookmark> result = new LinkedList<>();
        String originFilePathFull = filePath_origin + fileName_origin;
        LogHelper.v(TAG + ":origin file path:" + originFilePathFull);
        LogHelper.v(TAG + ":tmp file path:" + dbFilePath);
        try {
            ExternalStorageUtil.copyFile(originFilePathFull, dbFilePath, this.getName());
        } catch (Exception e) {
            LogHelper.e(e.getMessage());
            return result;
        }
        result.addAll(fetchBookmarksList(false, dbFilePath, "mtt_bookmarks", null, null, "created asc"));
        result.addAll(fetchBookmarksList(false, dbFilePath, "pad_bookmark", null, null, "created asc"));
        result.addAll(fetchBookmarksList(false, dbFilePath, "pc_bookmark", null, null, "created asc"));
        result.addAll(fetchBookmarksList(false, dbFilePath, "snapshot", columns_snapshot, "type=?", new String[]{"-1"}, null));
        LogHelper.v(TAG + ":读取未登录用户书签SQLite数据库结束");
        return result;
    }

    private List<Bookmark> fetchBookmarksList(boolean needThrowException, String dbFilePath, String tableName, String where, String[] whereArgs, String orderBy) {
        return fetchBookmarksList(needThrowException, dbFilePath, tableName, columns, where, whereArgs, orderBy);
    }

    private List<Bookmark> fetchBookmarksList(boolean needThrowException, String dbFilePath, String tableName, String[] columns, String where, String[] whereArgs, String orderBy) {
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
                boolean needParseFolder = checkNeedParseFolder(columns);
                LogHelper.v(TAG + ":needParseFolder:" + needParseFolder);
                if (!needParseFolder) {
                    parseBookmarkWithoutFolder(cursor, result);
                } else {
                    parseBookmarkWithFolder(cursor, result);
                }
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

    private void parseBookmarkWithoutFolder(Cursor cursor, List<Bookmark> result) {
        while (cursor.moveToNext()) {
            Bookmark item = new Bookmark();
            item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            item.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            item.setFolder("主页");
            result.add(item);
        }
    }

    private void parseBookmarkWithFolder(Cursor cursor, List<Bookmark> result) {
        List<QQBookmark> qqBookmarks = parseQQBookmark(cursor);
        Map<Long, QQBookmark> folders = new HashMap<>();
        for (QQBookmark item : qqBookmarks) {
            String folder = item.getFolder();
            if (folder != null && "1".equals(folder)) {
                folders.put(item.getUuid(), item);
            }
        }

        for (QQBookmark item : qqBookmarks) {
            String folder = item.getFolder();
            if (folder != null && "0".equals(folder)) {
                String folderPath = trim(parseFolderPath(folders, item.getParent_uuid()));
                Bookmark bookmark = new Bookmark();
                bookmark.setTitle(item.getTitle());
                bookmark.setUrl(item.getUrl());
                bookmark.setFolder(folderPath == null ? "" : folderPath);
                result.add(bookmark);
            }
        }
    }

    private String parseFolderPath(Map<Long, QQBookmark> folders, long parent_uuid) {
        QQBookmark parent = folders.get(parent_uuid);
        if (parent == null) {
            return null;
        }
        return parseFolderPath(folders, parent_uuid, "");
    }

    private String parseFolderPath(Map<Long, QQBookmark> folders, long parent_uuid, String path) {
        QQBookmark parent = folders.get(parent_uuid);
        if (parent == null) {
            return path;
        }
        String title = parent.getTitle();
        if (!(title == null || title.isEmpty() || "root".equals(title))) {
            path = title + Path.FILE_SPLIT + path;
        }
        return parseFolderPath(folders, parent.getParent_uuid(), path);
    }

    private List<QQBookmark> parseQQBookmark(Cursor cursor) {
        List<QQBookmark> result = new LinkedList<>();
        while (cursor.moveToNext()) {
            QQBookmark item = new QQBookmark();
            item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            item.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            item.setFolder(cursor.getInt(cursor.getColumnIndex("folder")) + "");
            item.setParent_uuid(cursor.getLong(cursor.getColumnIndex("parent_uuid")));
            item.setUuid(cursor.getLong(cursor.getColumnIndex("uuid")));
            result.add(item);
        }
        return result;
    }

    private class QQBookmark extends Bookmark {
        @Getter
        @Setter
        private long parent_uuid;
        @Getter
        @Setter
        private long uuid;
    }

    private boolean checkNeedParseFolder(String[] current) {
        return Arrays.equals(current, columns);
    }

    @Override
    public int appendBookmark(List<Bookmark> bookmarks) {
        return 0;
    }
}
