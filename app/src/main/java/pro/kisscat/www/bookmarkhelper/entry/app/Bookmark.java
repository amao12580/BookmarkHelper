package pro.kisscat.www.bookmarkhelper.entry.app;

import lombok.Getter;
import lombok.Setter;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/10
 * Time:14:21
 */

public class Bookmark {
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private String url;
    @Getter
    @Setter
    private String folder;

//    public boolean equals(Object anObject) {
//        if (this == anObject) {
//            return true;
//        }
//        if (anObject instanceof Bookmark) {
//            Bookmark anotherBookmark = (Bookmark) anObject;
//            if (getUrl() == anotherBookmark.getUrl()) {
//                return true;
//            }
//        }
//        return false;
//    }
}
