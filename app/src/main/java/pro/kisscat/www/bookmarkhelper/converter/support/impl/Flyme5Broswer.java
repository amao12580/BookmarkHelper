package pro.kisscat.www.bookmarkhelper.converter.support.impl;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.converter.support.BasicBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/9
 * Time:15:38
 */

public class Flyme5Broswer extends BasicBroswer {
    private String packageName = "com.android.broswer";
    private List<Bookmark> bookmarks;

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public int readBookmarkSum(Context context) {
        if (bookmarks == null) {
            LogHelper.v("Flyme5:bookmarks cache is hit.");
            readBookmark(context);
        }
        return bookmarks.size();
    }

    @Override
    public void fillDefaultIcon(Context context) {
        this.setIcon(context.getResources().getDrawable(R.drawable.ic_flyme5));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.broswer_name_show_flyme5));
    }

    @Override
    public List<Bookmark> readBookmark(Context context) {
        if (bookmarks != null) {
            return bookmarks;
        }
        bookmarks = new LinkedList<>();
        return bookmarks;
    }
    private static final String dataBase_filePath = "";
    private static final String dataBase_bookmark_dbName = "";

    @Override
    public int appendBookmark(Context context, List<Bookmark> bookmarks) {
        return 0;
    }


    public abstract class Flyme5Bookmark extends Bookmark {
        @Setter
        @Getter
        private String folder;
        @Setter
        @Getter
        private int order;
    }
}
