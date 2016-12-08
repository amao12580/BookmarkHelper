package pro.kisscat.www.bookmarkhelper.converter.support.pojo.qiyu.json;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/12/6
 * Time:11:23
 */

public class QiyuHasLoginedBookmark extends QiyuBookmark {
    @Setter
    @Getter
    private List<HomepageBookmark> homeBookmark;

    public List<Bookmark> fetchAll() {
        List<Bookmark> bookmarks = new LinkedList<>();
        bookmarks.addAll(super.fetchAll());
        bookmarks.addAll(this.fetchAllHomepageBookmark());
        return bookmarks;
    }

    private List<Bookmark> fetchAllHomepageBookmark() {
        List<Bookmark> bookmarks = new LinkedList<>();
        if (this.homeBookmark == null || this.homeBookmark.isEmpty()) {
            return bookmarks;
        }
        for (HomepageBookmark item : this.homeBookmark) {
            if (item.getData_added() > 0) {
                Bookmark bookmark = new Bookmark();
                bookmark.setTitle(item.getName());
                bookmark.setUrl(item.getUrl());
                bookmarks.add(bookmark);
            }
        }
        LogHelper.v("homeBookmark size:" + homeBookmark.size());
        return bookmarks;
    }

}
