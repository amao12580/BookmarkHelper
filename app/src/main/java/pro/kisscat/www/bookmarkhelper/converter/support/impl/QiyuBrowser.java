package pro.kisscat.www.bookmarkhelper.converter.support.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;

import com.alibaba.fastjson.JSONReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.converter.support.BasicBrowser;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.qiyu.json.QiyuJsonBookmark;
import pro.kisscat.www.bookmarkhelper.database.SQLite.DBHelper;
import pro.kisscat.www.bookmarkhelper.exception.ConverterException;
import pro.kisscat.www.bookmarkhelper.util.Path;
import pro.kisscat.www.bookmarkhelper.util.context.ContextUtil;
import pro.kisscat.www.bookmarkhelper.util.file.FileUtil;
import pro.kisscat.www.bookmarkhelper.util.file.pojo.File;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.storage.ExternalStorageUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/12/7
 * Time:14:33
 */

public class QiyuBrowser extends BasicBrowser {
    private static final String TAG = "Qiyu";
    private static final String packageName = "com.ruanmei.qiyubrowser";
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
        this.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_qiyu));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.browser_name_show_qiyu));
    }


    private static final String fileJsonName_origin = Path.FILE_SPLIT + "local_bm.json";
    private static final String dirJsonName_origin = "files";
    private static final String fileUserDBName_origin = Path.FILE_SPLIT + "bookmarkhistory";
    private static final String fileHomepageDBName_origin = Path.FILE_SPLIT + "homepage";
    private static final String dirDBName_origin = "databases";
    private static final String filePath_origin = Path.INNER_PATH_DATA + packageName + Path.FILE_SPLIT;
    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + "/Qiyu/";

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
            List<Bookmark> bookmarksListPart1 = fetchBookmarksListByNoUserLogined();
            List<Bookmark> bookmarksListPart2 = fetchBookmarksListByUserHasLogined();
            List<Bookmark> bookmarksListPart3 = fetchBookmarksListByHomepage();
            LogHelper.v(TAG + ":已登录用户书签数据:" + JsonUtil.toJson(bookmarksListPart1));
            LogHelper.v(TAG + ":已登录用户书签条数:" + bookmarksListPart1.size());
            LogHelper.v(TAG + ":未登录的用户书签数据:" + JsonUtil.toJson(bookmarksListPart2));
            LogHelper.v(TAG + ":未登录的用户书签条数:" + bookmarksListPart2.size());
            LogHelper.v(TAG + ":首页的书签数据:" + JsonUtil.toJson(bookmarksListPart3));
            LogHelper.v(TAG + ":首页的书签条数:" + bookmarksListPart3.size());
            bookmarksList.addAll(bookmarksListPart1);
            bookmarksList.addAll(bookmarksListPart2);
            bookmarksList.addAll(bookmarksListPart3);
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

    private List<Bookmark> fetchBookmarksListByHomepage() {
        String origin_dir = filePath_origin + dirDBName_origin;
        String origin_file = origin_dir + fileHomepageDBName_origin;
        String cp_file = filePath_cp + fileHomepageDBName_origin;
        LogHelper.v(TAG + ":开始读取首页的书签sqlite数据库:" + origin_dir);
        List<Bookmark> result = new LinkedList<>();
        LogHelper.v(TAG + ":origin file path:" + origin_file);
        LogHelper.v(TAG + ":tmp file path:" + cp_file);
        try {
            ExternalStorageUtil.copyFile(origin_file, cp_file, this.getName());
        } catch (Exception e) {
            LogHelper.e(e.getMessage());
            return result;
        }
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        String tableName = "customnavigations";
        boolean tableExist;
        String[] columns = new String[]{"name", "url", "imageurl"};
        try {
            sqLiteDatabase = DBHelper.openReadOnlyDatabase(cp_file);
            tableExist = DBHelper.checkTableExist(sqLiteDatabase, tableName);
            if (!tableExist) {
                LogHelper.v(TAG + ":database table " + tableName + " not exist.");
                return result;
            }
            cursor = sqLiteDatabase.query(false, tableName, columns, null, null, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String imageurl = cursor.getString(cursor.getColumnIndex("imageurl"));
                    if (imageurl == null || imageurl.isEmpty()) {
                        Bookmark item = new Bookmark();
                        item.setTitle(cursor.getString(cursor.getColumnIndex("name")));
                        item.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                        result.add(item);
                    }
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (sqLiteDatabase != null) {
                sqLiteDatabase.close();
            }
        }
        LogHelper.v(TAG + ":读取首页的书签sqlite数据库结束");
        return result;

    }

    private List<Bookmark> fetchBookmarksListByUserHasLogined() {
        String origin_dir = filePath_origin + dirDBName_origin;
        String origin_file = origin_dir + fileUserDBName_origin;
        String cp_file = filePath_cp + fileUserDBName_origin;
        LogHelper.v(TAG + ":开始读取已登录用户的书签sqlite数据库:" + origin_dir);
        List<Bookmark> result = new LinkedList<>();
        LogHelper.v(TAG + ":origin file path:" + origin_file);
        LogHelper.v(TAG + ":tmp file path:" + cp_file);
        try {
            ExternalStorageUtil.copyFile(origin_file, cp_file, this.getName());
        } catch (Exception e) {
            LogHelper.e(e.getMessage());
            return result;
        }
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        String tableName = "bookmark";
        boolean tableExist;
        String[] columns = new String[]{"title", "url"};
        try {
            sqLiteDatabase = DBHelper.openReadOnlyDatabase(cp_file);
            tableExist = DBHelper.checkTableExist(sqLiteDatabase, tableName);
            if (!tableExist) {
                LogHelper.v(TAG + ":database table " + tableName + " not exist.");
                return result;
            }
            cursor = sqLiteDatabase.query(false, tableName, columns, null, null, null, null, null, null);
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
        }
        LogHelper.v(TAG + ":读取已登录用户书签sqlite数据库结束");
        return result;
    }

    private List<Bookmark> fetchBookmarksListByNoUserLogined() {
        String origin_dir = filePath_origin + dirJsonName_origin;
        String origin_file = origin_dir + fileJsonName_origin;
        String cp_file = filePath_cp + fileJsonName_origin;
        LogHelper.v(TAG + ":开始读取未登录用户的书签Json文件:" + origin_dir);
        List<Bookmark> result = new LinkedList<>();
        LogHelper.v(TAG + ":origin file path:" + origin_file);
        LogHelper.v(TAG + ":tmp file path:" + cp_file);
        java.io.File file;
        try {
            file = ExternalStorageUtil.copyFile(origin_file, cp_file, this.getName());
            result.addAll(fetchBookmarksByJsonFile(file));
        } catch (Exception e) {
            LogHelper.e(e.getMessage());
            return result;
        }
        LogHelper.v(TAG + ":读取未登录用户书签Json文件结束");
        return result;
    }

    private List<Bookmark> fetchBookmarksByJsonFile(java.io.File file) throws FileNotFoundException {
        JSONReader jsonReader = null;
        List<Bookmark> result = new LinkedList<>();
        try {
            jsonReader = new JSONReader(new FileReader(file));
            QiyuJsonBookmark qiyuJsonBookmark = jsonReader.readObject(QiyuJsonBookmark.class);
            if (qiyuJsonBookmark == null) {
                LogHelper.v("qiyuJsonBookmark is null.");
                return result;
            }
            File fileShow = FileUtil.formatFileSize(file);
            if (fileShow.isOver10KB()) {
                LogHelper.v("书签数据文件大小超过10KB,skip print.size:" + fileShow.toString());
            } else {
                LogHelper.v("书签数据:" + JsonUtil.toJson(qiyuJsonBookmark));
            }
            List<Bookmark> bookmarks = qiyuJsonBookmark.fetchAll();
            if (bookmarks == null) {
                LogHelper.v("bookmarks is null.");
                return result;
            }
            LogHelper.v("书签条数:" + bookmarks.size());
            return bookmarks;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (jsonReader != null) {
                jsonReader.close();
            }
        }
    }

    @Override
    public int appendBookmark(List<Bookmark> bookmarks) {
        return 0;
    }
}
