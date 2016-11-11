package pro.kisscat.www.bookmarkhelper.util.phone;

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

    /**
     * http://blog.csdn.net/xx326664162/article/details/52438706
     */
    private static String getHandSetInfo() {
        return "BOARD: " + android.os.Build.BOARD +
                ", BOOTLOADER: " + android.os.Build.BOOTLOADER +
                ", BRAND: " + android.os.Build.BRAND +
                ", CPU_ABI: " + android.os.Build.CPU_ABI +
                ", CPU_ABI2: " + android.os.Build.CPU_ABI2 +
                ", DEVICE: " + android.os.Build.DEVICE +
                ", DISPLAY: " + android.os.Build.DISPLAY +
                ", FINGERPRINT: " + android.os.Build.FINGERPRINT +
                ", HARDWARE: " + android.os.Build.HARDWARE +
                ", HOST: " + android.os.Build.HOST +
                ", ID: " + android.os.Build.ID +
                ", MANUFACTURER: " + android.os.Build.MANUFACTURER +
                ", MODEL: " + android.os.Build.MODEL +
                ", PRODUCT: " + android.os.Build.PRODUCT +
                ", RADIO: " + android.os.Build.RADIO +
                ", RADITAGSO: " + android.os.Build.TAGS +
                ", TIME: " + android.os.Build.TIME +
                ", TYPE: " + android.os.Build.TYPE +
                ", USER: " + android.os.Build.USER +
                ", VERSION.RELEASE: " + android.os.Build.VERSION.RELEASE +
                ", VERSION.CODENAME: " + android.os.Build.VERSION.CODENAME +
                ", VERSION.INCREMENTAL: " + android.os.Build.VERSION.INCREMENTAL +
                ", VERSION.SDK: " + android.os.Build.VERSION.SDK +
                ", VERSION.SDK_INT: " + android.os.Build.VERSION.SDK_INT;
    }
}
