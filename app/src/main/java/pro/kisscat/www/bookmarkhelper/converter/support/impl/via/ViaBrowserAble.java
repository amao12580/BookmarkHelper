package pro.kisscat.www.bookmarkhelper.converter.support.impl.via;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import java.util.List;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.converter.support.BasicBrowser;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.via.impl.ViaStage1Browser;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.via.impl.ViaStage2Browser;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.App;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;
import pro.kisscat.www.bookmarkhelper.util.appList.AppListUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/14
 * Time:13:20
 */

public class ViaBrowserAble extends BasicBrowser {
    public static final String packageName = "mark.via";
    protected List<Bookmark> bookmarks;

    public static ViaBrowserAble fetchViaBrowser() {
        App via = AppListUtil.getAppInfo(ViaBrowserAble.packageName);
        ViaBrowserAble viaBrowserable = null;
        if (via != null) {
            viaBrowserable = ViaBrowserAble.fetchViaBrowser(via.getVersionName(), via.getVersionCode());
        }
        if (viaBrowserable == null) {
            viaBrowserable = ViaBrowserAble.chooseDefault();
        }
        return viaBrowserable;
    }

    private static ViaBrowserAble fetchViaBrowser(String versionName, int versionCode) {
        if (versionCode >= ViaStage2Browser.minVersionCode && versionName != null && !versionName.isEmpty()) {
            return new ViaStage2Browser();
        } else {
            return new ViaStage1Browser();
        }
    }


    private static ViaBrowserAble chooseDefault() {
        return fetchViaBrowser(null, 0);
    }

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
        this.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_via));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.browser_name_show_via));
    }
}
