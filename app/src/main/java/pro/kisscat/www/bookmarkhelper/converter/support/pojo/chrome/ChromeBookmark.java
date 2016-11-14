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
            StringBuilder folderNameBuilder = new StringBuilder();
            return parseNode(bookmarks, folderNameBuilder, node);
        }
        return new LinkedList<>();
    }

    private List<Bookmark> parseNode(List<Bookmark> bookmarks, StringBuilder folderNameBuilder, Node node) {
        if (node == null) {
            return bookmarks;
        }
        String folderName = node.getName();
        folderNameBuilder.append(folderName);
        List<Node> children = node.getChildren();
        if (children != null) {
            LogHelper.v("bookmarkBar part;folderName:" + folderName + ",size:" + children.size());
            for (Node item : children) {
                if (checkIsFolder(item)) {
                    addPath(folderNameBuilder, item.getName());
                    parseNode(bookmarks, folderNameBuilder, item);
                } else {
                    addAll(bookmarks, folderNameBuilder.toString(), children);
                }
            }
        }
        return bookmarks;
    }

    private boolean checkIsFolder(Node node) {
        return node != null && node.getType() != null && MetaData.folderTypeDefaultName.equals(node.getType());
    }

    private void addPath(StringBuilder folderNameBuilder, String name) {
        if (!folderNameBuilder.toString().isEmpty()) {
            folderNameBuilder.append(Path.FILE_SPLIT);
        }
        folderNameBuilder.append(name);
    }

    private void addAll(List<Bookmark> result, String folderName, List<Node> nodes) {
        for (Node item : nodes) {
            Bookmark bookmark = new Bookmark();
            bookmark.setFolder(folderName);
            bookmark.setUrl(item.getUrl());
            bookmark.setTitle(item.getName());
            result.add(bookmark);
        }
    }
}
