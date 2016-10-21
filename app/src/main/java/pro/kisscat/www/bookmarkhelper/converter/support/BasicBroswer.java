package pro.kisscat.www.bookmarkhelper.converter.support;

import android.content.Context;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Setter;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;
import pro.kisscat.www.bookmarkhelper.pojo.App;
import pro.kisscat.www.bookmarkhelper.util.appList.AppListUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/9
 * Time:15:31
 */
public class BasicBroswer extends App implements Broswerable {
    protected int bookmarkSum;
    @Setter
    protected boolean installed;

    public boolean isInstalled() {
        setInstalled(AppListUtil.isInstalled(getPackageName()));
        return installed;
    }

    public void fillName(Context context) {
        if (installed) {
            super.setName(AppListUtil.getAppName(getPackageName()));
        }
        fillDefaultAppName(context);
    }

    @Override
    public void fillDefaultAppName(Context context) {

    }

    @Override
    public int readBookmarkSum(Context context) {
        return 0;
    }

    @Override
    public void fillDefaultIcon(Context context) {

    }

    @Override
    public String getPackageName() {
        return null;
    }


    @Override
    public List<Bookmark> readBookmark(Context context) {
        return null;
    }

    @Override
    public int appendBookmark(Context context, List<Bookmark> bookmarks) {
        return 0;
    }

    protected Set<Bookmark> buildNoRepeat(List<Bookmark> bookmarks, List<Bookmark> exists) {
        Set<String> urls = new HashSet<>();
        for (Bookmark item : exists) {
            urls.add(item.getUrl());
        }
        Set<Bookmark> result = new HashSet<>();
        for (Bookmark item : bookmarks) {
            if (!urls.contains(item.getUrl())) {
                result.add(item);
            }
        }
        return result;
    }
}
