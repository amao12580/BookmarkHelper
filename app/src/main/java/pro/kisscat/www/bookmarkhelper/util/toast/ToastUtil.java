package pro.kisscat.www.bookmarkhelper.util.toast;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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
    private static Handler handler = new Handler(Looper.getMainLooper());
    private static Toast toast = null;
    private static final Object synObj = new Object();

    public static void showMessage(final Context context, final String msg) {
        showMessage(context, msg, Toast.LENGTH_SHORT);
    }

//    public static void showMessage(final Activity activity, final String msg) {
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("hit");
//                if (toast != null) {
//                    toast.cancel();
//                    toast.setText(msg);
//                    toast.setDuration(Toast.LENGTH_SHORT);
//                } else {
//                    toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
//                }
//                toast.show();
//            }
//        });
//    }


    public static void showMessage(final Context context, final int msg) {
        showMessage(context, msg, Toast.LENGTH_SHORT);
    }

    private static void showMessage(final Context context, final String msg, final int len) {
//        https:github.com/zhitaocai/ToastCompat_Deprecated
//        ToastCompat.makeText(context, msg, len).show();

        new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (synObj) {
                            if (toast != null) {
                                toast.cancel();
                                toast.setText(msg);
                                toast.setDuration(len);
                            } else {
                                toast = Toast.makeText(context, msg, len);
                            }
                            toast.show();
                        }
                    }
                });
            }
        }).start();
    }

    private static void showMessage(final Context context, final int msg, final int len) {
        new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (synObj) {
                            if (toast != null) {
                                toast.cancel();
                                toast.setText(msg);
                                toast.setDuration(len);
                            } else {
                                toast = Toast.makeText(context, msg, len);
                            }
                            toast.show();
                        }
                    }
                });
            }
        }).start();
    }
}
