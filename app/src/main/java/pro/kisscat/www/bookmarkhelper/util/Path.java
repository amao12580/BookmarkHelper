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
    public final static String FILE_SPLIT = File.separator;

    private static final String INNER_PATH_ROOT = FILE_SPLIT + "data";
    public static final String INNER_PATH_DATA = INNER_PATH_ROOT + INNER_PATH_ROOT + FILE_SPLIT;
    public static String SDCARD_ROOTPATH = "";
    public static final String SDCARD_APP_ROOTPATH = FILE_SPLIT + "BookmarkHelper" + FILE_SPLIT;
    public static final String SDCARD_LOG_ROOTPATH = "logs" + FILE_SPLIT;
    public static final String SDCARD_TMP_ROOTPATH = "tmp" + FILE_SPLIT;
}
