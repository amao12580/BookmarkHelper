package pro.kisscat.www.bookmarkhelper.converter.support.impl.via;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import java.util.List;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.converter.support.BasicBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.via.impl.ViaStage1Broswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.via.impl.ViaStage2Broswer;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/14
 * Time:13:20
 */

public class ViaBroswerable extends BasicBroswer {
    public static final String packageName = "mark.via";
    protected List<Bookmark> bookmarks;

    public static ViaBroswerable fetchViaBroswer(String versionName, int versionCode) {
        if (versionCode >= 20161113) {
            return new ViaStage2Broswer();
        } else {
            return new ViaStage1Broswer();
        }
    }


    public static ViaBroswerable chooseDefault() {
        return fetchViaBroswer(null, 0);
    }

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
        this.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_via));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.broswer_name_show_via));
    }
}
