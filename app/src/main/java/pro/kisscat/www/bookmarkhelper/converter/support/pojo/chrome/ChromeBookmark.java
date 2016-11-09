package pro.kisscat.www.bookmarkhelper.converter.support.pojo.chrome;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.chrome.module.BookmarkBar;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.chrome.module.Children;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.chrome.module.Other;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.chrome.module.Root;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.chrome.module.Synced;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

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


    public List<Children> fetchChildrens() {
        Root root = getRoots();
        if (root == null) {
            return null;
        }
        List<Children> result = new LinkedList<>();
        BookmarkBar bookmarkBar = root.getBookmark_bar();
        if (bookmarkBar != null) {
            List<Children> children = bookmarkBar.getChildren();
            if (children != null) {
                LogHelper.v("bookmarkBar part size:" + children.size());
                result.addAll(children);
            }
        }
        Other other = root.getOther();
        if (other != null) {
            List<Children> children = other.getChildren();
            if (children != null) {
                LogHelper.v("other part size:" + children.size());
                result.addAll(children);
            }
        }
        Synced synced = root.getSynced();
        if (synced != null) {
            List<Children> children = synced.getChildren();
            if (children != null) {
                LogHelper.v("synced part size:" + children.size());
                result.addAll(children);
            }
        }
        return result;
    }
}
