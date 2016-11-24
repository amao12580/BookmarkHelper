package pro.kisscat.www.bookmarkhelper.util.context;

import android.content.Context;

import lombok.Getter;
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

    private static String readBookmarksError;
    private static String readBookmarksEmpty;
    private static String appendBookmarksError;
    private static String fileCPError;
    private static String fileMkdirError;
    private static String fileDeleteError;
    private static String viaBookmarksFileMiss;
    private static String flyme5BookmarksFileMiss;
    private static String appUninstall;
    private static String notSupport;
    private static String notSupportParseHomepageBookmarks;
    @Getter
    private static String packageCodePath;
    @Getter
    private static String SystemNotReadOrWriteable;
    @Getter
    private static String SDCardNotReadOrWriteable;


    public static void init(Context context) {
        if (readBookmarksError == null) {
            readBookmarksError = context.getResources().getString(R.string.readBookmarksError);
        }
        if (readBookmarksEmpty == null) {
            readBookmarksEmpty = context.getResources().getString(R.string.readBookmarksEmpty);
        }
        if (appendBookmarksError == null) {
            appendBookmarksError = context.getResources().getString(R.string.appendBookmarksError);
        }
        if (fileCPError == null) {
            fileCPError = context.getResources().getString(R.string.fileCPError);
        }
        if (fileMkdirError == null) {
            fileMkdirError = context.getResources().getString(R.string.fileMkdirError);
        }
        if (fileDeleteError == null) {
            fileDeleteError = context.getResources().getString(R.string.fileDeleteError);
        }
        if (viaBookmarksFileMiss == null) {
            viaBookmarksFileMiss = context.getResources().getString(R.string.viaBookmarksFileMiss);
        }
        if (flyme5BookmarksFileMiss == null) {
            flyme5BookmarksFileMiss = context.getResources().getString(R.string.flyme5BookmarksFileMiss);
        }
        if (appUninstall == null) {
            appUninstall = context.getResources().getString(R.string.appUninstall);
        }
        if (notSupport == null) {
            notSupport = context.getResources().getString(R.string.notSupport);
        }
        if (notSupportParseHomepageBookmarks == null) {
            notSupportParseHomepageBookmarks = context.getResources().getString(R.string.notSupportParseHomepageBookmarks);
        }
        if (packageCodePath == null) {
            packageCodePath = context.getPackageCodePath();
        }
        if (SystemNotReadOrWriteable == null) {
            SystemNotReadOrWriteable = context.getResources().getString(R.string.SystemNotReadOrWriteable);
        }
        if (SDCardNotReadOrWriteable == null) {
            SDCardNotReadOrWriteable = context.getResources().getString(R.string.SDCardNotReadOrWriteable);
        }
    }

    public static String buildNotSupportParseHomepageBookmarksMessage() {
        return notSupportParseHomepageBookmarks;
    }

    public static String buildReadBookmarksErrorMessage(String browserName) {
        return browserName + split + readBookmarksError;
    }

    public static String buildReadBookmarksEmptyMessage(String browserName) {
        return browserName + split + readBookmarksEmpty;
    }

    public static String buildAppendBookmarksErrorMessage(String browserName) {
        return browserName + split + appendBookmarksError;
    }

    public static String buildFileCPErrorMessage(String browserName) {
        return browserName + split + fileCPError;
    }

    public static String buildFileMkdirErrorMessage(String browserName) {
        if (browserName == null || browserName.isEmpty()) {
            return fileMkdirError;
        }
        return browserName + split + fileMkdirError;
    }

    public static String buildFileDeleteErrorMessage(String browserName) {
        return browserName + split + fileDeleteError;
    }

    public static String buildViaBookmarksFileMiss(String browserName) {
        return browserName + split + viaBookmarksFileMiss;
    }

    public static String buildReadBookmarksTableNotExistErrorMessage(String browserName) {
        return browserName + split + flyme5BookmarksFileMiss;
    }

    public static String buildAppNotInstalledMessage(String browserName) {
        return browserName + split + appUninstall;
    }

    public static String buildRuleNotSupportedNowMessage(Rule rule) {
        return notSupport;
    }
}
