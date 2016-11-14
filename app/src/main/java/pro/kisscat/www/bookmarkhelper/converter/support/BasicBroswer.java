package pro.kisscat.www.bookmarkhelper.converter.support;

import android.content.Context;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import lombok.Setter;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.App;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;
import pro.kisscat.www.bookmarkhelper.util.appList.AppListUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

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

    public boolean isInstalled(Context context, BasicBroswer broswer) {
        setInstalled(AppListUtil.isInstalled(context, broswer.getPackageName()));
        return installed;
    }

    public void fillName(Context context) {
        if (installed) {
            super.setName(AppListUtil.getAppName(context, getPackageName()));
        }
        fillDefaultAppName(context);
    }

    public void fillVersion(Context context) {
        if (installed) {
            App app = AppListUtil.getAppInfo(context, getPackageName());
            if (app != null) {
                super.setVersionName(app.getVersionName());
                super.setVersionCode(app.getVersionCode());
            }
        }
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

    protected Set<Bookmark> buildNoRepeat(List<Bookmark> appends, List<Bookmark> exists) {
        Set<String> appendURLs = new HashSet<>();
        Set<Bookmark> noRepeatAppends = new LinkedHashSet<>();
        for (Bookmark item : appends) {
            String url = item.getUrl();
            if (url != null && !url.isEmpty() && !appendURLs.contains(url)) {
                appendURLs.add(url);
                noRepeatAppends.add(item);
            }
        }
        Set<String> existURLs = new HashSet<>();
        for (Bookmark item : exists) {
            existURLs.add(item.getUrl());
        }
        Set<Bookmark> result = new LinkedHashSet<>();
        for (Bookmark item : noRepeatAppends) {
            if (!existURLs.contains(item.getUrl())) {
                result.add(item);
            }
        }
        return result;
    }

    protected boolean isValidUrl(String bookmarkUrl) {
        if (bookmarkUrl == null || bookmarkUrl.isEmpty() || !bookmarkUrl.startsWith("http") || !bookmarkUrl.contains("://")) {
            LogHelper.v("url is damage,url:" + bookmarkUrl + ",skip.");
            return false;
        }
        return true;
    }

    private transient boolean denyPrintBookmarkHasShow = false;

    protected boolean allowPrintBookmark(int currentIndex, int allSize) {
        if (currentIndex <= 1) {
            denyPrintBookmarkHasShow = false;
        }
        int threshold = 20;
        if (allSize < threshold) {
            return true;
        }
        if (currentIndex > threshold) {
            if (!denyPrintBookmarkHasShow) {
                LogHelper.v("There too many bookmark,skip unnecessary print.currentIndex:" + currentIndex + ",allSize:" + allSize);
                denyPrintBookmarkHasShow = true;
            }
            return false;
        }
        return true;
    }
}
