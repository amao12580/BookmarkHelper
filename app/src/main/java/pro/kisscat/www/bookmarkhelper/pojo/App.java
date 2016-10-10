package pro.kisscat.www.bookmarkhelper.pojo;

import android.graphics.drawable.Drawable;

import lombok.Data;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/10
 * Time:10:48
 */
@Data
public class App {
    private String name = "";
    private String packageName = "";
    private String versionName = "";
    private int versionCode = 0;
    private Drawable icon = null;
}
