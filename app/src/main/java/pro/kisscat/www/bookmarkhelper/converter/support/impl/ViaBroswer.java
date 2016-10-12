package pro.kisscat.www.bookmarkhelper.converter.support.impl;

import android.content.Context;

import lombok.Getter;
import lombok.Setter;
import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.converter.support.BasicBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;

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
    public void readBookmarkSum(Context context) {
        super.setBookmarkSum(99);
    }

    @Override
    public void fillDefaultShow(Context context) {
        this.setPackageName(packageName);
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.broswer_name_show_via));
    }

    public abstract class ViaBookmark extends Bookmark {
        @Setter
        @Getter
        private String folder;
        @Setter
        @Getter
        private int order;
    }
}
