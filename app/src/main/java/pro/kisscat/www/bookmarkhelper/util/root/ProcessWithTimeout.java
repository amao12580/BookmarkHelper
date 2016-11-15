package pro.kisscat.www.bookmarkhelper.util.root;

import lombok.Getter;
import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/15
 * Time:14:34
 */

class ProcessWithTimeout extends Thread {
    private Process process;
    @Getter
    private Integer exitCode = null;

    boolean isTimeout(Integer exitCode) {
        return exitCode == null;
    }

    ProcessWithTimeout(Process process) {
        this.process = process;
    }

    @Override
    public void run() {
        System.out.println("****************** 8");
        try {
            long s = System.currentTimeMillis();
            exitCode = process.waitFor();
            System.out.println("****************** 9 exitCode:" + exitCode + ",time:" + (System.currentTimeMillis() - s));
        } catch (Exception ex) {
            System.out.println("****************** 10");
            LogHelper.e(MetaData.LOG_E_DEFAULT, "ProcessWithTimeout,run." + ex.getMessage());
        }
    }
}
