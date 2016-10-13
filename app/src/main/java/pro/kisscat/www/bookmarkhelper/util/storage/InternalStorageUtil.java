package pro.kisscat.www.bookmarkhelper.util.storage;

import android.os.Environment;

import java.io.File;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/9
 * Time:15:01
 */

public final class InternalStorageUtil implements BasicStorageUtil {
    /**
     * 获取内置SD卡路径
     */
    @Override
    public String getRootPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

}
