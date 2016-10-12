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

public class Flyme5Broswer extends BasicBroswer {
    private String packageName = "com.android.broswer";

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public void readBookmarkSum(Context context) {
        super.setBookmarkSum(55);
    }

    @Override
    public void fillDefaultIcon(Context context) {
        this.setIcon(context.getResources().getDrawable(R.drawable.ic_flyme5));
    }

    @Override
    public void fillDefaultAppName(Context context) {
        this.setName(context.getString(R.string.broswer_name_show_flyme5));
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
