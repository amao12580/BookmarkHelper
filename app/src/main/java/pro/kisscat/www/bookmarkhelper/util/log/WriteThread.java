package pro.kisscat.www.bookmarkhelper.util.log;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/8
 * Time:14:51
 */
public class WriteThread extends Thread {

    public static boolean isWriteThreadRuning = false;//写日志线程是否已经在运行了

    @Override
    public void run() {
        isWriteThreadRuning = true;
        LogHelper.flush();
        isWriteThreadRuning = false;//队列中的日志都写完了，关闭线程（也可以常开 要测试下）
    }
}
