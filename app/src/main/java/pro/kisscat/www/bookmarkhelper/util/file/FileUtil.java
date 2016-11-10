package pro.kisscat.www.bookmarkhelper.util.file;

import pro.kisscat.www.bookmarkhelper.util.file.pojo.File;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/9
 * Time:12:44
 */

public class FileUtil {
    public static File formatFileSize(java.io.File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        return new File(file);
    }
}
