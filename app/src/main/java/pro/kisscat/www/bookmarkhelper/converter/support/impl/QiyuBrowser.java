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
import pro.kisscat.www.bookmarkhelper.entry.app.Bookmark;
import pro.kisscat.www.bookmarkhelper.pojo.converter.chrome.ChromeBookmark;
import pro.kisscat.www.bookmarkhelper.pojo.converter.qiyu.QiyuBookmark;
import pro.kisscat.www.bookmarkhelper.pojo.converter.qiyu.QiyuHasLoginedBookmark;
import pro.kisscat.www.bookmarkhelper.database.SQLite.DBHelper;
import pro.kisscat.www.bookmarkhelper.exception.ConverterException;
import pro.kisscat.www.bookmarkhelper.util.Path;
import pro.kisscat.www.bookmarkhelper.util.context.ContextUtil;
import pro.kisscat.www.bookmarkhelper.util.file.FileUtil;
import pro.kisscat.www.bookmarkhelper.entry.file.File;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.storage.ExternalStorageUtil;
import pro.kisscat.www.bookmarkhelper.util.storage.InternalStorageUtil;

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
    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + Path.FILE_SPLIT + "Qiyu" + Path.FILE_SPLIT;

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
            List<Bookmark> bookmarksListPart1 = fetchBookmarksListByUserHasLogined();
            List<Bookmark> bookmarksListPart2 = fetchBookmarksListByNoUserLogined();
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
        List<Bookmark> result = new LinkedList<>();
        String origin_dir = filePath_origin + dirJsonName_origin + Path.FILE_SPLIT + "profiles" + Path.FILE_SPLIT;
        LogHelper.v(TAG + ":开始读取已登录用户的书签json文件:" + origin_dir);
        if (!InternalStorageUtil.isExistDir(origin_dir)) {
            LogHelper.v(origin_dir + " is not existed.skip");
            return result;
        }
        List<String> dirs = InternalStorageUtil.lsDir(origin_dir);
        if (dirs == null || dirs.isEmpty()) {
            LogHelper.v(TAG + ":没有找到目标文件夹");
            return result;
        }
        String targetDir = null;
        String rule = "[0-9]{6,10}";
        for (String item : dirs) {
            if (item != null) {
                if (item.matches(rule)) {
                    targetDir = item;
                    break;
                }
            }
        }
        if (targetDir == null || targetDir.isEmpty()) {
            LogHelper.v(TAG + ":真的没有找到目标文件夹");
            return result;
        } else {
            LogHelper.v(TAG + ":targetDir:" + targetDir);
        }
        String fileName_origin = "Bookmarks";
        origin_dir += targetDir + Path.FILE_SPLIT;
        String sourceFilePath = origin_dir + fileName_origin;
        LogHelper.v(TAG + ":sourceFilePath:" + sourceFilePath);
        try {
            if (InternalStorageUtil.isExistFile(sourceFilePath)) {
                List<Bookmark> part1 = fetchBookmarksByJsonFile(ExternalStorageUtil.copyFile(sourceFilePath, filePath_cp + fileName_origin, this.getName()), true);
                LogHelper.v(TAG + ":" + fileName_origin + " part size:" + part1.size());
                result.addAll(part1);
            }
        } catch (Exception e) {
            LogHelper.e(e.getMessage());
            return result;
        }

        fileName_origin = "pc_bm.json";
        sourceFilePath = origin_dir + fileName_origin;
        LogHelper.v(TAG + ":sourceFilePath:" + sourceFilePath);
        try {
            if (InternalStorageUtil.isExistFile(sourceFilePath)) {
                List<Bookmark> part2 = fetchBookmarksByJsonFileWithChrome(ExternalStorageUtil.copyFile(sourceFilePath, filePath_cp + fileName_origin, this.getName()));
                LogHelper.v(TAG + ":" + fileName_origin + " part size:" + part2.size());
                result.addAll(part2);
            }
        } catch (Exception e) {
            LogHelper.e(e.getMessage());
            return result;
        }

        fileName_origin = "user_bm.json";
        sourceFilePath = origin_dir + fileName_origin;
        LogHelper.v(TAG + ":sourceFilePath:" + sourceFilePath);
        try {
            if (InternalStorageUtil.isExistFile(sourceFilePath)) {
                List<Bookmark> part2 = fetchBookmarksByJsonFile(ExternalStorageUtil.copyFile(sourceFilePath, filePath_cp + fileName_origin, this.getName()), true);
                LogHelper.v(TAG + ":" + fileName_origin + " part size:" + part2.size());
                result.addAll(part2);
            }
        } catch (Exception e) {
            LogHelper.e(e.getMessage());
            return result;
        }
        LogHelper.v(TAG + ":读取已登录用户的书签json文件结束");
        return result;
    }

    private List<Bookmark> fetchBookmarksByJsonFileWithChrome(java.io.File file) throws FileNotFoundException {
        JSONReader jsonReader = null;
        List<Bookmark> result = new LinkedList<>();
        try {
            jsonReader = new JSONReader(new FileReader(file));
            ChromeBookmark qiyuJsonBookmark;
            qiyuJsonBookmark = jsonReader.readObject(ChromeBookmark.class);
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

    private List<Bookmark> fetchBookmarksListByNoUserLogined() {
        List<Bookmark> result = new LinkedList<>();
        List<Bookmark> part1 = fetchBookmarksListByNoUserLoginedWithDB();
        List<Bookmark> part2 = fetchBookmarksListByNoUserLoginedWithJson();
        LogHelper.v(TAG + ":未登录的用户书签，json部分，条数:" + part1.size());
        LogHelper.v(TAG + ":未登录的用户书签，sqlite部分，条数:" + part2.size());
        result.addAll(part1);
        result.addAll(part2);
        return result;
    }

    private List<Bookmark> fetchBookmarksListByNoUserLoginedWithDB() {
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

    private List<Bookmark> fetchBookmarksListByNoUserLoginedWithJson() {
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
            result.addAll(fetchBookmarksByJsonFile(file, false));
        } catch (Exception e) {
            LogHelper.e(e.getMessage());
            return result;
        }
        LogHelper.v(TAG + ":读取未登录用户书签Json文件结束");
        return result;
    }

    private List<Bookmark> fetchBookmarksByJsonFile(java.io.File file, boolean isLogined) throws FileNotFoundException {
        JSONReader jsonReader = null;
        List<Bookmark> result = new LinkedList<>();
        try {
            jsonReader = new JSONReader(new FileReader(file));
            QiyuBookmark qiyuJsonBookmark;
            if (isLogined) {
                qiyuJsonBookmark = jsonReader.readObject(QiyuHasLoginedBookmark.class);
            } else {
                qiyuJsonBookmark = jsonReader.readObject(QiyuBookmark.class);
            }
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
