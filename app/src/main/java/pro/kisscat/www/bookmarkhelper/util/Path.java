package pro.kisscat.www.bookmarkhelper.util;

import java.io.File;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/20
 * Time:15:07
 */

public class Path {
    public static String SDCARD_ROOTPATH = "";
    public static String FILE_SPLIT = File.separator;
    public static final String SDCARD_APP_ROOTPATH = FILE_SPLIT + "BookmarkHelper" + FILE_SPLIT;
    public static final String SDCARD_LOG_ROOTPATH = "logs" + FILE_SPLIT+FILE_SPLIT;
    public static final String SDCARD_TMP_ROOTPATH = "tmp" + FILE_SPLIT;
}
