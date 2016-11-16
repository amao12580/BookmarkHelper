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
import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;
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
 * Date:2016/10/9
 * Time:15:38
 */

public class SogouBroswer extends BasicBroswer {
    private static final String TAG = "Sogou";
    private static final String packageName = "sogou.mobile.explorer";
    private final static String[] columns = new String[]{"f_server_id", "f_server_pid", "f_title", "f_url", "f_is_folder"};
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
        this.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_sougou));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.broswer_name_show_sougou));
    }


    private static final String fileName_origin = "sogou_cloud_default.db";
    private static final String filePath_origin = Path.INNER_PATH_DATA + packageName + "/databases/";
    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + "/Sogou/";

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
            List<Bookmark> bookmarksListPart2 = fetchBookmarksListByNoUserLogined(context, filePath_cp + fileName_origin);
            LogHelper.v("已登录用户书签数据:" + JsonUtil.toJson(bookmarksListPart1));
            LogHelper.v("已登录用户书签条数:" + bookmarksListPart1.size());
            LogHelper.v("未登录的用户书签数据:" + JsonUtil.toJson(bookmarksListPart2));
            LogHelper.v("未登录的用户书签条数:" + bookmarksListPart2.size());
            bookmarksList.addAll(bookmarksListPart1);
            bookmarksList.addAll(bookmarksListPart2);
            LogHelper.v("总的书签条数:" + bookmarksList.size());
            bookmarks = new LinkedList<>();
            int index = 0;
            int size = bookmarksList.size();
            for (Bookmark item : bookmarksList) {
                index++;
                String bookmarkUrl = item.getUrl();
                String bookmarkFolder = item.getFolder();
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
                if (!(bookmarkFolder == null || bookmarkFolder.isEmpty())) {
                    bookmark.setFolder(bookmarkFolder);
                }
                bookmarks.add(bookmark);
            }
            LogHelper.v("result:" + JsonUtil.toJson(bookmarks));
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

    private List<Bookmark> fetchBookmarksListByUserHasLogined(Context context, String appRootDir) {
        LogHelper.v(TAG + ":开始读取已登录用户书签,appRootDir:" + appRootDir);
        List<Bookmark> result = new LinkedList<>();
        if (!InternalStorageUtil.isExistDir(appRootDir)) {
            LogHelper.v(TAG + ":不存在已登录用户书签,根目录缺失");
            return result;
        }
        String regularRule = "[a-z0-9\\-]{33}";
        String searchRule = "*.db";
        List<String> fileNames = InternalStorageUtil.lsFileByRegular(appRootDir, searchRule);
        if (fileNames == null || fileNames.isEmpty()) {
            LogHelper.v(TAG + ":已登录用户没有书签数据");
            return result;
        }
        String targetFileNames = null;
        String startKey = "sogou_cloud_";
        for (String item : fileNames) {
            LogHelper.v("item:" + item);
            if (item == null) {
                LogHelper.v("item is null.");
                break;
            }
            String tmp = item;
            if (tmp.startsWith(startKey)) {
                tmp = tmp.replaceAll(startKey, "");
                tmp = tmp.replaceAll(".db", "");
                LogHelper.v("tmp:" + tmp);
                if (tmp.matches(regularRule)) {
                    LogHelper.v("match success regularRule.");
                    targetFileNames = item;
                    break;
                }
            }
            LogHelper.v("not match.");
        }
        if (targetFileNames == null || targetFileNames.isEmpty()) {
            LogHelper.v(TAG + ":已登录用户真的没有书签数据");
            return result;
        }
        result.addAll(fetchBookmarksListByUserHasLogined(context, appRootDir, targetFileNames));
        LogHelper.v(TAG + ":完成读取已登录用户书签,appRootDir:" + appRootDir);
        return result;
    }

    private List<Bookmark> fetchBookmarksListByUserHasLogined(Context context, String dir, String fileName) {
        String targetFilePath = dir + fileName;
        LogHelper.v("targetFilePath is:" + targetFilePath);
        List<Bookmark> result = new LinkedList<>();
        String tmpFilePath = filePath_cp + fileName;
        try {
            ExternalStorageUtil.copyFile(context, targetFilePath, tmpFilePath, this.getName());
        } catch (Exception e) {
            LogHelper.e(MetaData.LOG_E_DEFAULT, e.getMessage());
            return result;
        }
        result.addAll(fetchBookmarksList(false, context, tmpFilePath, "cloud_favorite", null, null, null));
        result.addAll(fetchBookmarksList(false, context, tmpFilePath, "cloud_favorite_base", null, null, null));
        return result;
    }

    private List<Bookmark> fetchBookmarksListByNoUserLogined(Context context, String dbFilePath) {
        LogHelper.v(TAG + ":开始读取未登录用户的书签SQLite数据库:" + dbFilePath);
        List<Bookmark> result = new LinkedList<>();
        String originFilePathFull = filePath_origin + fileName_origin;
        LogHelper.v(TAG + ":origin file path:" + originFilePathFull);
        LogHelper.v(TAG + ":tmp file path:" + dbFilePath);
        try {
            ExternalStorageUtil.copyFile(context, originFilePathFull, dbFilePath, this.getName());
        } catch (Exception e) {
            LogHelper.e(MetaData.LOG_E_DEFAULT, e.getMessage());
            return result;
        }
        result.addAll(fetchBookmarksList(context, dbFilePath, "cloud_favorite", null, null, null));
        result.addAll(fetchBookmarksList(context, dbFilePath, "cloud_favorite_base", null, null, null));
        LogHelper.v(TAG + ":读取未登录用户书签SQLite数据库结束");
        return result;
    }

    private List<Bookmark> fetchBookmarksList(Context context, String dbFilePath, String tableName, String where, String[] whereArgs, String orderBy) {
        return fetchBookmarksList(true, context, dbFilePath, tableName, columns, where, whereArgs, orderBy);
    }

    private List<Bookmark> fetchBookmarksList(boolean needThrowException, Context context, String dbFilePath, String tableName, String where, String[] whereArgs, String orderBy) {
        return fetchBookmarksList(needThrowException, context, dbFilePath, tableName, columns, where, whereArgs, orderBy);
    }

    private List<Bookmark> fetchBookmarksList(boolean needThrowException, Context context, String dbFilePath, String tableName, String[] columns, String where, String[] whereArgs, String orderBy) {
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
        List<SogouBookmark> bookmarks = parseBookmark(cursor);
        Map<String, SogouBookmark> folders = new HashMap<>();
        for (SogouBookmark item : bookmarks) {
            int folder = item.getF_is_folder();
            if (folder == 1) {
                folders.put(item.getF_server_id(), item);
            }
        }

        for (SogouBookmark item : bookmarks) {
            int folder = item.getF_is_folder();
            if (folder == 0) {
                String folderPath = trim(parseFolderPath(folders, item.getF_server_pid()));
                Bookmark bookmark = new Bookmark();
                bookmark.setTitle(item.getTitle());
                bookmark.setUrl(item.getUrl());
                bookmark.setFolder(folderPath == null ? "" : folderPath);
                result.add(bookmark);
            }
        }
    }

    private String parseFolderPath(Map<String, SogouBookmark> folders, String parent_uuid) {
        SogouBookmark parent = folders.get(parent_uuid);
        if (parent == null) {
            return null;
        }
        return parseFolderPath(folders, parent_uuid, "");
    }

    private String parseFolderPath(Map<String, SogouBookmark> folders, String parent_uuid, String path) {
        SogouBookmark parent = folders.get(parent_uuid);
        if (parent == null) {
            return path;
        }
        String title = parent.getTitle();
        if (!(title == null || title.isEmpty())) {
            path = title + Path.FILE_SPLIT + path;
        }
        return parseFolderPath(folders, parent.getF_server_pid(), path);
    }

    private List<SogouBookmark> parseBookmark(Cursor cursor) {
        List<SogouBookmark> result = new LinkedList<>();
        while (cursor.moveToNext()) {
            SogouBookmark item = new SogouBookmark();
            item.setTitle(cursor.getString(cursor.getColumnIndex("f_title")));
            item.setUrl(cursor.getString(cursor.getColumnIndex("f_url")));
            item.setF_server_id(cursor.getString(cursor.getColumnIndex("f_server_id")));
            item.setF_server_pid(cursor.getString(cursor.getColumnIndex("f_server_pid")));
            item.setF_is_folder(cursor.getInt(cursor.getColumnIndex("f_is_folder")));
            result.add(item);
        }
        return result;
    }

    private class SogouBookmark extends Bookmark {
        @Getter
        @Setter
        private String f_server_id;
        @Getter
        @Setter
        private String f_server_pid;

        @Getter
        @Setter
        private int f_is_folder;
    }

    @Override
    public int appendBookmark(Context context, List<Bookmark> bookmarks) {
        return 0;
    }
}
