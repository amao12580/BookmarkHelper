package pro.kisscat.www.bookmarkhelper.pojo.converter.qiyu;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;
import pro.kisscat.www.bookmarkhelper.entry.app.Bookmark;
import pro.kisscat.www.bookmarkhelper.util.Path;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/12/7
 * Time:15:13
 */

public class QiyuBookmark {
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
    private List<QiyuBookmark> children;

    public List<Bookmark> fetchAll() {
        List<Bookmark> bookmarks = new LinkedList<>();
        return parseNode(bookmarks, "", this);
    }

    private List<Bookmark> parseNode(List<Bookmark> bookmarks, String folderPath, QiyuBookmark node) {
        if (node == null) {
            return bookmarks;
        }
        List<QiyuBookmark> children = node.getChildren();
        if (children != null) {
            String folderName = node.getName();
            String myFolderPath = addPath(folderPath, folderName);
            LogHelper.v("bookmarkBar part;folderName:" + myFolderPath + ",size:" + children.size());
            for (QiyuBookmark item : children) {
                if (checkIsFolder(item)) {
                    parseNode(bookmarks, myFolderPath, item);
                } else {
                    addAll(bookmarks, myFolderPath, item);
                }
            }
        }
        return bookmarks;
    }

    private boolean checkIsFolder(QiyuBookmark node) {
        return node != null && node.getType() != null && MetaData.folderTypeDefaultName.equals(node.getType());
    }

    private String addPath(String folderPath, String name) {
        if (folderPath.isEmpty()) {
            return name;
        }
        return folderPath + Path.FILE_SPLIT + name;
    }

    private void addAll(List<Bookmark> result, String folderName, QiyuBookmark nodes) {
        String url = nodes.getUrl();
        if (url != null && !url.isEmpty()) {
            Bookmark bookmark = new Bookmark();
            bookmark.setFolder(folderName);
            bookmark.setUrl(url);
            bookmark.setTitle(nodes.getName());
            result.add(bookmark);
        }
    }
}
