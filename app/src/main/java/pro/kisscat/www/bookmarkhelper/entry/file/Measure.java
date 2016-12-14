package pro.kisscat.www.bookmarkhelper.entry.file;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/10
 * Time:15:46
 */

public enum Measure {
    BYTE, KB, MB, GB;

    @Override
    public String toString() {
        if (this == BYTE) {
            return "byte";
        } else {
            return super.toString();
        }
    }
}
