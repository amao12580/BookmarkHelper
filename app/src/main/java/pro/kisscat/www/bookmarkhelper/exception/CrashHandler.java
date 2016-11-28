package pro.kisscat.www.bookmarkhelper.exception;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

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


    public void uncaughtException(Thread thread, Throwable throwable) {
        // 在此可以把用户手机的一些信息以及异常信息捕获并上传,
        String fatalErrorMessage = MetaData.LOG_E_FATAL + ":" + AppListUtil.thisAppInfo + " is crash.exception message:" + throwable.getMessage();
        System.out.println(fatalErrorMessage);
        LogHelper.e(MetaData.LOG_E_FATAL, fatalErrorMessage);
        LogHelper.e(MetaData.LOG_E_FATAL, printException(throwable));
        LogHelper.write();
        throwable.printStackTrace();
        Toast.makeText(context, "Bug:我们已经妥善保护好现场，请将日志文件发给作者.", Toast.LENGTH_LONG).show();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //干掉当前的程序
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private String printException(Throwable throwable) {
        String msg = null;
        Writer result = null;
        PrintWriter printWriter = null;
        try {
            result = new StringWriter();
            printWriter = new PrintWriter(result);
            throwable.printStackTrace(printWriter);
            msg = result.toString();
        } finally {
            if (printWriter != null) {
                printWriter.flush();
                printWriter.close();
            }
            if (result != null) {
                try {
                    result.flush();
                    result.close();
                } catch (IOException e) {
                    LogHelper.e("printException finally IOException:" + e.getMessage());
                    LogHelper.write();
                    e.printStackTrace();
                }
            }
        }
        return msg == null ? "" : msg;
    }
}
