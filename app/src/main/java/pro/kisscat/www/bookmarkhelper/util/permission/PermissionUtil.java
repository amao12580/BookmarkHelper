package pro.kisscat.www.bookmarkhelper.util.permission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import lombok.Getter;

/**
 * http://jijiaxin89.com/2015/08/30/Android-s-Runtime-Permission/
 * <p>
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/14
 * Time:14:46
 */

public class PermissionUtil {
    @Getter
    private static final int STORAGE_REQUEST_CODE = 1;

    public static boolean checkOnly(Activity activity) {
        //权限不够时返回false
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkAndRequest(Activity activity) {
        //权限不够时返回false
        if (checkOnly(activity)) {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, STORAGE_REQUEST_CODE);
            return false;
        }
        return true;
    }
}
