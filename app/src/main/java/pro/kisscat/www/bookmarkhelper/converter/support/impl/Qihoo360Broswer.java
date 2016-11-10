package pro.kisscat.www.bookmarkhelper.converter.support.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

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
            File cpPath = new File(filePath_cp);
            cpPath.deleteOnExit();
            cpPath.mkdirs();
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
            int index = 0;
            int size = bookmarksList.size();
            for (Bookmark item : bookmarksList) {
                index++;
                String bookmarkUrl = item.getUrl();
                String bookmarkName = item.getTitle();
                if (allowPrintBookmark(index, size)) {
                    LogHelper.v("name:" + bookmarkName);
                    LogHelper.v("url:" + bookmarkUrl);
                }
                if (!isGoodUrl(bookmarkUrl)) {
                    continue;
                }
                if (bookmarkName == null || bookmarkName.isEmpty()) {
                    LogHelper.v("url:" + bookmarkName + ",set to default value.");
                    bookmarkName = MetaData.BOOKMARK_TITLE_DEFAULT;
                }
                Bookmark bookmark = new Bookmark();
                bookmark.setTitle(bookmarkName);
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

    private final static String[] columns = new String[]{"title", "url"};

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
            cursor = sqLiteDatabase.query(false, tableName, columns, null, null, "url", null, "created asc", null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Bookmark item = new Bookmark();
                    item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    item.setUrl(cursor.getString(cursor.getColumnIndex("url")));
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
            LogHelper.v(TAG + ":读取未登录书签SQLite数据库结束");
        }
        return result;
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
        List<String> fileNames = InternalStorageUtil.lsFileByRegular(originFileDirPath, searchRule);
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
            cursor = sqLiteDatabase.query(false, tableName, columns, null, null, "url", null, "create_time asc", null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Bookmark item = new Bookmark();
                    item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    item.setUrl(cursor.getString(cursor.getColumnIndex("url")));
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
            LogHelper.v(TAG + ":完成读取已登录用户书签,bookmarkDir:" + bookmarkDir + ",fileName:" + fileName);
        }
        return result;
    }

    @Override
    public int appendBookmark(Context context, List<Bookmark> bookmarks) {
        return 0;
    }
}
