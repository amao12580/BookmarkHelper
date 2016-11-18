package pro.kisscat.www.bookmarkhelper.util.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/17
 * Time:10:55
 */

class IOThread implements Runnable {
    private volatile static boolean needExitNow = false;
    private List<String> list;
    private InputStream inputStream;

    IOThread(InputStream inputStream, List<String> list) {
        this.inputStream = inputStream;
        this.list = list;
    }

    public Thread start() {
        Thread thread = new Thread(this);
        thread.setDaemon(true);//将其设置为守护线程
        thread.start();
        return thread;
    }

    public void run() {
        if (inputStream == null) {
            return;
        }
        BufferedReader br = null;
        try {
            String character = "GB2312";
            br = new BufferedReader(new InputStreamReader(inputStream, character));
            String line;
            while (!needExitNow && inputStream != null && (line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            LogHelper.e("IOThread.run.catch," + e.getMessage());
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    //释放资源
                    br.close();
                } catch (IOException e) {
                    LogHelper.e("IOThread.run.finally.br," + e.getMessage());
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    //释放资源
                    inputStream.close();
                    inputStream = null;
                } catch (IOException e) {
                    LogHelper.e("IOThread.run.finally.inputStream," + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    synchronized static void needExitNow() {
        if (!needExitNow) {
            needExitNow = true;
        }
    }
}
