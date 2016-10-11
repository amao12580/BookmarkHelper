package pro.kisscat.www.bookmarkhelper.util.log;

import android.util.Log;

import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/11
 * Time:14:18
 */

public class LogHelper {

    public static int v(String msg) {
        return v(MetaData.LOG_V_DEFAULT,msg);
    }
    public static int v(String tag, String msg) {
        return Log.println(Log.VERBOSE, tag, msg);
    }
}
