package pro.kisscat.www.bookmarkhelper.converter.support.pojo.chrome;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.chrome.module.Children;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.chrome.module.Root;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/1
 * Time:18:07
 */

public class ChromeBookmark {
    @Setter
    @Getter
    private Root roots;

    public List<Children> getChildren() {
        if (getRoots() == null) {
            return null;
        }
        if (getRoots().getSynced() == null) {
            return null;
        }
        return getRoots().getSynced().getChildren();
    }
}
