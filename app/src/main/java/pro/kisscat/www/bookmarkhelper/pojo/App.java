package pro.kisscat.www.bookmarkhelper.pojo;

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
    public int versionCode = 0;
    @JSONField(serialize = false)
    public transient Drawable icon;

    public App() {
    }
}
