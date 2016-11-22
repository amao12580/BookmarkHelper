package pro.kisscat.www.bookmarkhelper.util.context;

import android.content.Context;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.rule.Rule;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/20
 * Time:11:15
 */

public class ContextUtil {
    private static final String split = "：";

    public static String buildReadBookmarksErrorMessage(Context context, String broswerName) {
        return broswerName + split + context.getResources().getString(R.string.readBookmarksError);
    }

    public static String buildReadBookmarksEmptyMessage(Context context, String broswerName) {
        return broswerName + split + context.getResources().getString(R.string.readBookmarksEmpty);
    }

    public static String buildAppendBookmarksErrorMessage(Context context, String broswerName) {
        return broswerName + split + context.getResources().getString(R.string.appendBookmarksError);
    }

    public static String buildFileCPErrorMessage(Context context, String broswerName) {
        return broswerName + split + context.getResources().getString(R.string.fileCPError);
    }

    public static String buildFileMkdirErrorMessage(Context context, String broswerName) {
        if (broswerName == null || broswerName.isEmpty()) {
            return context.getResources().getString(R.string.fileMkdirError);
        }
        return broswerName + split + context.getResources().getString(R.string.fileMkdirError);
    }

    public static String buildFileDeleteErrorMessage(Context context, String broswerName) {
        return broswerName + split + context.getResources().getString(R.string.fileDeleteError);
    }

    public static String buildViaBookmarksFileMiss(Context context, String broswerName) {
        return broswerName + split + context.getResources().getString(R.string.viaBookmarksFileMiss);
    }

    public static String buildReadBookmarksTableNotExistErrorMessage(Context context, String broswerName) {
        return broswerName + split + context.getResources().getString(R.string.flyme5BookmarksFileMiss);
    }

    public static String buildAppNotInstalledMessage(Context context, String broswerName) {
        return broswerName + split + context.getResources().getString(R.string.appUninstall);
    }

    public static String buildRuleNotSupportedNowMessage(Context context, Rule rule) {
        return context.getResources().getString(R.string.notSupport);
    }
}
