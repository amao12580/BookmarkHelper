package pro.kisscat.www.bookmarkhelper.util.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;
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
    private List<String> list;
    private InputStream inputStream;

    IOThread(InputStream inputStream, List<String> list) {
        this.inputStream = inputStream;
        this.list = list;
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.setDaemon(true);//将其设置为守护线程
        thread.start();
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
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            LogHelper.e(MetaData.LOG_E_DEFAULT, "IOThread.run," + e.getMessage());
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    //释放资源
                    inputStream.close();
                    inputStream = null;
                } catch (IOException e) {
                    LogHelper.e(MetaData.LOG_E_DEFAULT, "IOThread.run.finally.inputStream," + e.getMessage());
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    //释放资源
                    br.close();
                } catch (IOException e) {
                    LogHelper.e(MetaData.LOG_E_DEFAULT, "IOThread.run.finally.br," + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
