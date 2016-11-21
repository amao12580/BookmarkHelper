package pro.kisscat.www.bookmarkhelper.util.toast;

import android.content.Context;
import android.widget.Toast;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/18
 * Time:16:50
 */

public class ToastUtil {
    private static Toast toast;

    /**
     * Android 解决Toast的延时显示问题
     * 关键在于重用toast，这样就不用每次都创建一个新的toast
     */
    public static void showToastMessage(Context context, String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}
