package pro.kisscat.www.bookmarkhelper.converter.support.impl.chrome;

import com.alibaba.fastjson.JSONReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import pro.kisscat.www.bookmarkhelper.converter.support.BasicBrowser;
import pro.kisscat.www.bookmarkhelper.entry.app.Bookmark;
import pro.kisscat.www.bookmarkhelper.pojo.converter.chrome.ChromeBookmark;
import pro.kisscat.www.bookmarkhelper.util.file.FileUtil;
import pro.kisscat.www.bookmarkhelper.entry.file.File;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/25
 * Time:10:53
 */

public class ChromeBrowserAble extends BasicBrowser {
    public String TAG = null;

    protected List<Bookmark> fetchBookmarks(java.io.File file) throws FileNotFoundException {
        JSONReader jsonReader = null;
        List<Bookmark> result = new LinkedList<>();
        try {
            jsonReader = new JSONReader(new FileReader(file));
            ChromeBookmark chromeBookmark = jsonReader.readObject(ChromeBookmark.class);
            if (chromeBookmark == null) {
                LogHelper.v("chromeBookmark is null.");
                return result;
            }
            File fileShow = FileUtil.formatFileSize(file);
            if (fileShow.isOver10KB()) {
                LogHelper.v("书签数据文件大小超过10KB,skip print.size:" + fileShow.toString());
            } else {
                LogHelper.v("书签数据:" + JsonUtil.toJson(chromeBookmark));
            }
            List<Bookmark> bookmarks = chromeBookmark.fetchAll();
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
}
