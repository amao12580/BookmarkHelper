package pro.kisscat.www.bookmarkhelper.entry.init;

import android.os.Handler;

import lombok.Getter;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/24
 * Time:11:02
 */

public class Params {
    @Getter
    private Handler handler;

    public Params(Handler handler) {
        this.handler = handler;
    }
}
