package pro.kisscat.www.bookmarkhelper.converter.support.pojo.chrome.module;

import lombok.Getter;
import lombok.Setter;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/1
 * Time:18:08
 */

public class Root {
    @Setter
    @Getter
    private BookmarkBar bookmark_bar;
    @Setter
    @Getter
    private Other other;
    @Setter
    @Getter
    private Synced synced;
}
