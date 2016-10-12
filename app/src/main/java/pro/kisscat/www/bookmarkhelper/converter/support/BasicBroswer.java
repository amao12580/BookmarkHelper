package pro.kisscat.www.bookmarkhelper.converter.support;

import android.content.Context;

import lombok.Getter;
import lombok.Setter;
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
    @Setter
    @Getter
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
    public void readBookmarkSum(Context context) {

    }

    @Override
    public void fillDefaultShow(Context context) {

    }

    @Override
    public String getPackageName() {
        return null;
    }
}
