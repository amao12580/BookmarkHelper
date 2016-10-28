package pro.kisscat.www.bookmarkhelper.converter.support.impl;

import android.content.Context;

import com.alibaba.fastjson.JSONReader;

import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;
import pro.kisscat.www.bookmarkhelper.converter.support.BasicBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;
import pro.kisscat.www.bookmarkhelper.exception.ConverterException;
import pro.kisscat.www.bookmarkhelper.util.Path;
import pro.kisscat.www.bookmarkhelper.util.context.ContextUtil;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.storage.ExternalStorageUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/9
 * Time:15:38
 */

public class ChromeBroswer extends BasicBroswer {
    private String packageName = "com.android.chrome";
    private List<Bookmark> bookmarks;

    @Override
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
        this.setIcon(context.getResources().getDrawable(R.drawable.ic_chrome));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.broswer_name_show_chrome));
    }

    private static final String fileName_origin = "Bookmarks";
    private static final String filePath_origin = "/data/data/com.android.chrome/app_chrome/Default/";
    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + "/Chrome/";

    @Override
    public List<Bookmark> readBookmark(Context context) {
        if (bookmarks != null) {
            LogHelper.v("Chrome:bookmarks cache is hit.");
            return bookmarks;
        }
        LogHelper.v("Chrome:bookmarks cache is miss.");
        JSONReader jsonReader = null;
        LogHelper.v("Chrome:开始读取书签数据");
        try {
            String originFilePathFull = filePath_origin + fileName_origin;
            LogHelper.v("Chrome:origin file path:" + originFilePathFull);
            File cpPath = new File(filePath_cp);
            cpPath.deleteOnExit();
            cpPath.mkdirs();
            LogHelper.v("Chrome:tmp file path:" + filePath_cp + fileName_origin);
            File file = ExternalStorageUtil.copyFile(context, originFilePathFull, filePath_cp + fileName_origin, this.getName());
            jsonReader = new JSONReader(new FileReader(file));
            ChromeBookmark chromeBookmark = jsonReader.readObject(ChromeBookmark.class);
            LogHelper.v("书签数据:" + JsonUtil.toJson(chromeBookmark));
            List<Children> childrens = chromeBookmark.getRoots().getSynced().getChildren();
            LogHelper.v("书签条数:" + childrens.size());
            bookmarks = new LinkedList<>();
            for (Children item : childrens) {
                String bookmarkUrl = item.getUrl();
                String bookmarkName = item.getName();
                LogHelper.v("name:" + bookmarkName);
                LogHelper.v("url:" + bookmarkUrl);
                if (bookmarkUrl == null || bookmarkUrl.isEmpty()) {
                    LogHelper.v("url:" + bookmarkUrl + ",skip.");
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
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.e(MetaData.LOG_E_DEFAULT, e.getMessage());
            throw new ConverterException(ContextUtil.buildReadBookmarksErrorMessage(context, this.getName()));
        } finally {
            if (jsonReader != null) {
                jsonReader.close();
            }
            LogHelper.v("Chrome:读取书签数据结束");
        }
        return bookmarks;
    }

    @Override
    public int appendBookmark(Context context, List<Bookmark> bookmarks) {
        return 0;
    }

    private static class ChromeBookmark {
        @Setter
        @Getter
        private Root roots;
    }

    private static class Root {
        @Setter
        @Getter
        private Synced synced;
    }

    private static class Synced {
        @Setter
        @Getter
        private List<Children> children;
    }

    private static class Children {
        @Setter
        @Getter
        private String name;
        @Setter
        @Getter
        private String url;
    }
}
