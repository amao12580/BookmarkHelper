package pro.kisscat.www.bookmarkhelper.util.storage;

import android.content.Context;

import java.util.UUID;

import pro.kisscat.www.bookmarkhelper.exception.ConverterException;
import pro.kisscat.www.bookmarkhelper.util.Path;
import pro.kisscat.www.bookmarkhelper.util.command.pojo.CommandResult;
import pro.kisscat.www.bookmarkhelper.util.context.ContextUtil;
import pro.kisscat.www.bookmarkhelper.util.file.pojo.FileType;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.root.RootUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/22
 * Time:11:25
 */

abstract class BasicStorageUtil implements Storageable {
    protected static boolean mkdir(Context context, String dirPath, String mark, boolean needthrowWhenError) {
        /**
         * -f  强制覆盖，不询问yes/no（-i的默认的，即默认为交互模式，询问是否覆盖）
         * -r  递归复制，包含目录
         * -a  做一个备份，这里可以不用这个参数，我们可以先备份整个test目录
         * -p  保持新文件的属性不变
         */
        String cmd = "mkdir -p " + dirPath;
        boolean result = RootUtil.executeCmd(cmd);
        if (!result && needthrowWhenError) {
            throw new ConverterException(ContextUtil.buildFileMkdirErrorMessage(context, mark));
        }
        return result;
    }

    private static boolean createNewFile(String file) {
        String cmd = "touch " + file;
        return RootUtil.executeCmd(cmd);
    }

    private static boolean readFile(String file) {
        String cmd = "cat " + file;
        return RootUtil.executeCmd(cmd);
    }

    /**
     * 检查读写权限
     */
    static boolean checkReadWriteable(Context context, String dirPath) {
        dirPath += "tmp" + Path.FILE_SPLIT;
        boolean mkdirRet = mkdir(context, dirPath, null, false);
        if (!mkdirRet) {
            LogHelper.v("mkdirRet false.");
            return false;
        }
        String fileName = UUID.randomUUID().toString();
        dirPath += fileName;
        LogHelper.v("targetNewFile path:" + dirPath);
        boolean createNewFileRet = createNewFile(dirPath);
        if (!createNewFileRet) {
            LogHelper.v("createNewFileRet false.");
            return false;
        }
        boolean readFileRet = readFile(dirPath);
        if (!readFileRet) {
            LogHelper.v("readFileRet false.");
            return false;
        }
        return true;
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
        return isExist(filePath, FileType.FILE);
    }

    /**
     * 目录是否存在？
     * <p>
     * 与JDK File.exist()的区别是，利用root权限，不受android权限框架限制，可以读取任意目录的存在性
     * <p>
     * 借用root权限
     * <p>
     */
    public static boolean isExistDir(String dirPath) {
        return isExist(dirPath, FileType.DIR);
    }

    private static boolean isExist(String path, FileType fileType) {
        String[] commands = new String[4];
        int existFlag = 10;
        int notExistFlag = 20;
        commands[0] = "if test -" + (fileType.equals(FileType.FILE) ? "f" : "d") + " " + path + " ";
        commands[1] = "then echo " + existFlag;
        commands[2] = "else echo " + notExistFlag;
        commands[3] = "fi";
        CommandResult commandResult = RootUtil.executeCmd(commands);
        return commandResult != null && commandResult.isSuccess() && commandResult.getSuccessMsg() != null &&
                existFlag == Integer.valueOf(commandResult.getSuccessMsg().isEmpty() ? "0" : commandResult.getSuccessMsg().get(0));
    }

    @Override
    public abstract String getRootPath();
}
