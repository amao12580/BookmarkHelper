package pro.kisscat.www.bookmarkhelper.util.phone;

import android.os.Build;

import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/11
 * Time:9:19
 */

public class PhoneUtil {
    private static volatile boolean isRecordPhoneInfo = false;

    public static synchronized void record() {
        if (!isRecordPhoneInfo) {
            LogHelper.v(getHandSetInfo(), false);
            isRecordPhoneInfo = true;
        }
    }

    private static final Character split = '\n';

    private static String getHandSetInfo() {
        return "BOARD=" + Build.BOARD + split +
                "BRAND=" + Build.BRAND + split +
                "CPU_ABI=" + Build.CPU_ABI + split +
                "DEVICE=" + Build.DEVICE + split +
                "DISPLAY=" + Build.DISPLAY + split +
                "FINGERPRINT=" + Build.FINGERPRINT + split +
                "HOST=" + Build.HOST + split +
                "ID=" + Build.ID + split +
                "MANUFACTURER=" + Build.MANUFACTURER + split +
                "MODEL=" + Build.MODEL + split +
                "PRODUCT=" + Build.PRODUCT + split +
                "TAGS=" + Build.TAGS + split +
                "TIME=" + Build.TIME + split +
                "TYPE=" + Build.TYPE + split +
                "USER=" + Build.USER + split +
                "VERSION.CODENAME=" + Build.VERSION.CODENAME + split +
                "VERSION.INCREMENTAL=" + Build.VERSION.INCREMENTAL + split +
                "VERSION.RELEASE=" + Build.VERSION.RELEASE + split +
                "VERSION.SDK_INT=" + Build.VERSION.SDK_INT + split;
    }
}
