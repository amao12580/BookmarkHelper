package pro.kisscat.www.bookmarkhelper.entry.app;

import android.graphics.drawable.Drawable;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/10
 * Time:10:48
 */
public class App {
    @Setter
    @Getter
    public String name = "";
    @Setter
    @Getter
    public String packageName = "";
    @Setter
    @Getter
    public String versionName = "";
    @Setter
    @Getter
    public long versionCode = 0;
    @Setter
    @Getter
    @JSONField(serialize = false)
    public transient Drawable icon;

    public String toString(String packageName) {
        return "\"name\":\"" + name + "\"," + "\"packageName\":\"" + packageName + "\"," + "\"versionName\":\"" + versionName + "\"," + "\"versionCode\":" + versionCode;
    }
}
