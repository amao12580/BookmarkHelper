package pro.kisscat.www.bookmarkhelper.pojo.converter.qiyu;

import lombok.Getter;
import lombok.Setter;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/12/8
 * Time:11:23
 */

class HomepageBookmark {
    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private String url;
    @Setter
    @Getter
    private long data_added;
}
