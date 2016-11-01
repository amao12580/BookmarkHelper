package pro.kisscat.www.bookmarkhelper.converter.support.pojo.via;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/1
 * Time:18:06
 */

public class ViaBookmark {
    @Setter
    @Getter
    @JSONField(ordinal = 1)
    String title;
    @Setter
    @Getter
    @JSONField(ordinal = 2)
    String url;
    @Setter
    @Getter
    @JSONField(ordinal = 3)
    String folder = "";
    @Setter
    @Getter
    @JSONField(ordinal = 4)
    int order = 0;
}
