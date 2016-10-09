package pro.kisscat.www.bookmarkhelper.converter.support.impl;

import android.content.Context;

import lombok.Data;
import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.converter.support.Broswer;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/9
 * Time:15:38
 */

public class Flyme5Broswer extends Broswer {

    public Flyme5Broswer(Context context) {
        super(context);
    }

    @Override
    public Show getBroswerShowInfo() {
        Show show=new Show();
        show.setName(getContext().getString(R.string.broswer_name_show_flyme5));
        return show;
    }

    @Data
    public abstract class Flyme5Bookmark extends Broswer.Bookmark{
        private String folder;
        private int order;
    }
}
