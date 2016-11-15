package pro.kisscat.www.bookmarkhelper.converter.support.pojo.chrome;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.chrome.module.Node;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.chrome.module.Root;
import pro.kisscat.www.bookmarkhelper.util.Path;
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

    public List<Bookmark> fetchAll() {
        Root root = getRoots();
        if (root == null) {
            return null;
        }
        List<Bookmark> result = new LinkedList<>();
        List<Bookmark> bookmarkBarBookmarks = parseNode(root.getBookmark_bar());
        LogHelper.v("bookmarkBar part size:" + bookmarkBarBookmarks.size());
        result.addAll(bookmarkBarBookmarks);

        List<Bookmark> otherBookmarks = parseNode(root.getOther());
        LogHelper.v("other part size:" + otherBookmarks.size());
        result.addAll(otherBookmarks);

        List<Bookmark> SyncedBookmarks = parseNode(root.getSynced());
        LogHelper.v("synced part size:" + SyncedBookmarks.size());
        result.addAll(SyncedBookmarks);

        return result;
    }

    private List<Bookmark> parseNode(Node node) {
        if (node != null) {
            List<Bookmark> bookmarks = new LinkedList<>();
            return parseNode(bookmarks, "", node);
        }
        return new LinkedList<>();
    }

    private List<Bookmark> parseNode(List<Bookmark> bookmarks, String folderPath, Node node) {
        if (node == null) {
            return bookmarks;
        }
        List<Node> children = node.getChildren();
        if (children != null) {
            String folderName = node.getName();
            String myFolderPath = addPath(folderPath, folderName);
            LogHelper.v("bookmarkBar part;folderName:" + myFolderPath + ",size:" + children.size());
            for (Node item : children) {
                if (checkIsFolder(item)) {
                    parseNode(bookmarks, myFolderPath, item);
                } else {
                    addAll(bookmarks, myFolderPath, item);
                }
            }
        }
        return bookmarks;
    }

    private boolean checkIsFolder(Node node) {
        return node != null && node.getType() != null && MetaData.folderTypeDefaultName.equals(node.getType());
    }

    private String addPath(String folderPath, String name) {
        if (folderPath.isEmpty()) {
            return name;
        }
        return folderPath + Path.FILE_SPLIT + name;
    }

    private void addAll(List<Bookmark> result, String folderName, Node nodes) {
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
