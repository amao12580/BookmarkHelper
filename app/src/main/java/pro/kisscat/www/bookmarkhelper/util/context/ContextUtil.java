package pro.kisscat.www.bookmarkhelper.util.context;

import android.content.Context;

import pro.kisscat.www.bookmarkhelper.R;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/20
 * Time:11:15
 */

public class ContextUtil {
    public static String buildReadBookmarksErrorMessage(Context context, String broswerName) {
        return broswerName + " " + context.getResources().getString(R.string.readBookmarksError);
    }

    public static String buildReadBookmarksEmptyMessage(Context context, String broswerName) {
        return broswerName + " " + context.getResources().getString(R.string.readBookmarksEmpty);
    }

    public static String buildAppendBookmarksErrorMessage(Context context, String broswerName) {
        return broswerName + " " + context.getResources().getString(R.string.appendBookmarksError);
    }

//    public static String buildReadBookmarksDataMissMessage(Context context, String broswerName) {
//        return broswerName + " " + context.getResources().getString(R.string.readBookmarksDataMiss);
//    }

    public static String buildFileCPErrorMessage(Context context, String broswerName) {
        return broswerName + " " + context.getResources().getString(R.string.fileCPError);
    }

    public static String buildFileDeleteErrorMessage(Context context, String broswerName) {
        return broswerName + " " + context.getResources().getString(R.string.fileDeleteError);
    }

    public static String buildViaBookmarksFileMiss(Context context, String broswerName) {
        return broswerName + " " + context.getResources().getString(R.string.viaBookmarksFileMiss);
    }

    public static String buildReadBookmarksTableNotExistErrorMessage(Context context, String broswerName) {
        return broswerName + " " + context.getResources().getString(R.string.flyme5BookmarksFileMiss);
    }
}
