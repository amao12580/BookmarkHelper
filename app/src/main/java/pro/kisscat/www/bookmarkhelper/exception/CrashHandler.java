package pro.kisscat.www.bookmarkhelper.exception;

import android.content.Context;

import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;
import pro.kisscat.www.bookmarkhelper.util.appList.AppListUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/8
 * Time:14:26
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    // 需求是 整个应用程序 只有一个 MyCrash-Handler
    private static CrashHandler INSTANCE;
    private Context context;

    //1.私有化构造方法
    private CrashHandler() {

    }

    public static synchronized CrashHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CrashHandler();
        return INSTANCE;
    }

    public void init(Context context) {
        this.context = context;
    }


    public void uncaughtException(Thread arg0, Throwable throwable) {
        // 在此可以把用户手机的一些信息以及异常信息捕获并上传,
        String fatalErrorMessage = AppListUtil.thisAppInfo + " is crash.exception message:" + throwable.getMessage();
        System.out.println(fatalErrorMessage);
        LogHelper.e(MetaData.LOG_E_FATAL, fatalErrorMessage);
        LogHelper.write();
        //干掉当前的程序
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
