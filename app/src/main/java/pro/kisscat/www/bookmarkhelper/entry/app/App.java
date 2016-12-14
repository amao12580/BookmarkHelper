package pro.kisscat.www.bookmarkhelper.entry.app;

import android.graphics.drawable.Drawable;

import com.alibaba.fastjson.annotation.JSONField;

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
    public String name = "";
    public String packageName = "";
    public String versionName = "";
    public long versionCode = 0;
    @JSONField(serialize = false)
    public transient Drawable icon;
}
