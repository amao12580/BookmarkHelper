package pro.kisscat.www.bookmarkhelper.util.storage;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import pro.kisscat.www.bookmarkhelper.exception.ConverterException;
import pro.kisscat.www.bookmarkhelper.util.context.ContextUtil;
import pro.kisscat.www.bookmarkhelper.util.root.RootUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/9
 * Time:15:01
 */

public final class InternalStorageUtil implements BasicStorageUtil {
    /**
     * 获取内置SD卡路径
     */
    @Override
    public String getRootPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

    /**
     * 删除文件
     * <p>
     * 借用root权限
     * <p>
     */
    public static boolean deleteFile(Context context, String filePath, String mark) {
        String cmd = "rm -rf " + filePath;
        boolean result = RootUtil.executeCmd(cmd);
        if (!result) {
            throw new ConverterException(ContextUtil.buildFileDeleteErrorMessage(context, mark));
        }
        return result;
    }

    /**
     * 文件是否存在？
     * <p>
     * 与JDK File.exist()的区别是，利用root权限，不受android权限框架限制，可以读取任意文件的存在性
     * <p>
     * 借用root权限
     * <p>
     */
    public static boolean isExistFile(String filePath) {
        String[] commands = new String[4];
        int existFlag = 10;
        int notExistFlag = 20;
        commands[0] = "if test -e " + filePath + " ";
        commands[1] = "then echo " + existFlag;
        commands[2] = "else echo " + notExistFlag;
        commands[3] = "fi";
        RootUtil.CommandResult commandResult = RootUtil.executeCmd(commands);
        return commandResult != null && commandResult.isSuccess() && existFlag == Integer.valueOf(commandResult.getSuccessMsg());
    }
}
