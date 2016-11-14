package pro.kisscat.www.bookmarkhelper.converter.support.pojo.chrome.module;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/9
 * Time:12:23
 */

public class Node {
    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private String type;
    @Setter
    @Getter
    private String url;
    @Setter
    @Getter
    private List<Node> children;
}
