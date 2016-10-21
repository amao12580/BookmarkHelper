package pro.kisscat.www.bookmarkhelper.converter.support.impl;

import android.content.Context;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

public class ViaBroswer extends BasicBroswer {
    private static final String packageName = "mark.via";

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
        this.setIcon(context.getResources().getDrawable(R.drawable.ic_via));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.broswer_name_show_via));
    }

    private static final String fileName_origin = "/bookmarks.dat";
    private static final String filePath_origin = "/data/data/mark.via/files/";
    private static final String filePath_cp = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.SDCARD_TMP_ROOTPATH + "/Via/";

    @Override
    public List<Bookmark> readBookmark(Context context) {
        if (bookmarks != null) {
            return bookmarks;
        }
        LogHelper.v("Via:开始读取书签数据");
        BufferedReader reader = null;
        try {
            String originFilePathFull = filePath_origin + fileName_origin;
            File cpPath = new File(filePath_cp);
            cpPath.deleteOnExit();
            cpPath.mkdirs();
            File file = ExternalStorageUtil.CP2SDCard(context, originFilePathFull, filePath_cp + fileName_origin, this.getName());
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            List<ViaBookmark> list = new ArrayList<>();
            int line = 1;
            while ((tempString = reader.readLine()) != null) {
                ViaBookmark item = JsonUtil.fromJson(tempString, ViaBookmark.class);
                list.add(item);
                line++;
            }
            reader.close();
            LogHelper.v("书签数据:" + JsonUtil.toJson(list));
            LogHelper.v("书签条数:" + list.size());
            bookmarks = new LinkedList<>();
            for (ViaBookmark item : list) {
                String bookmarkUrl = item.getUrl();
                String bookmarkName = item.getTitle();
                LogHelper.v("name:" + bookmarkName);
                LogHelper.v("url:" + bookmarkUrl);
                if (bookmarkUrl == null || bookmarkUrl.isEmpty()) {
                    LogHelper.v("url:" + bookmarkUrl + ",skip.");
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
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    LogHelper.e(MetaData.LOG_E_DEFAULT, e1.getMessage());
                }
            }
            LogHelper.v("Via:读取书签数据结束");
        }
        return bookmarks;
    }

    @Override
    public int appendBookmark(Context context, List<Bookmark> bookmarks) {
        List<Bookmark> exists = readBookmark(context);
        Set<Bookmark> increment = buildNoRepeat(bookmarks, exists);
        if (increment.isEmpty()) {
            return 0;
        }
        return 99;
    }

    private static class ViaBookmark extends Bookmark {
        @Setter
        @Getter
        @JSONField(ordinal = 1)
        String title;
        @Setter
        @Getter
        @JSONField(ordinal = 2)
        String url;
        @Setter
        @Getter
        @JSONField(ordinal = 3)
        String folder = "";
        @Setter
        @Getter
        @JSONField(ordinal = 4)
        int order = 0;
    }
}
