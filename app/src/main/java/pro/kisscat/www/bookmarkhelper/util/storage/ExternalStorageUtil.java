package pro.kisscat.www.bookmarkhelper.util.storage;


import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import pro.kisscat.www.bookmarkhelper.exception.ConverterException;
import pro.kisscat.www.bookmarkhelper.util.context.ContextUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.root.RootUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/9
 * Time:15:06
 */

public final class ExternalStorageUtil implements BasicStorageUtil {
    /**
     * 获取外置SD卡路径
     */
    @Override
    public String getRootPath() {
        List<String> lResult = new ArrayList<>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("extSdCard")) {
                    String[] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory()) {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lResult.isEmpty() ? null : lResult.get(0);
    }

    /**
     * 拷贝文件
     * <p>
     * 借用root权限
     * <p>
     * 可以读写系统文件
     * <p>
     * root
     */
    public static File copyFile(Context context, String source, String target, String mark) {
        /**
         * -f  强制覆盖，不询问yes/no（-i的默认的，即默认为交互模式，询问是否覆盖）
         * -r  递归复制，包含目录
         * -a  做一个备份，这里可以不用这个参数，我们可以先备份整个test目录
         * -p  保持新文件的属性不变
         */
        String cmd = "cp -fr " + source + " " + target;
        boolean result = RootUtil.executeCmd(cmd);
        if (!result) {
            throw new ConverterException(ContextUtil.buildFileCPErrorMessage(context, mark));
        }
        File file = new File(target);
        if (!file.exists()) {
            throw new ConverterException(ContextUtil.buildFileCPErrorMessage(context, mark));
        }
        boolean flag = file.setReadable(true);
        LogHelper.v("file is readable:" + flag);
        return file;
    }
}
