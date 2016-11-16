package pro.kisscat.www.bookmarkhelper.util.root;

import pro.kisscat.www.bookmarkhelper.util.command.CommandResult;
import pro.kisscat.www.bookmarkhelper.util.command.CommandUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/13
 * Time:18:16
 */

public final class RootUtil {
    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean upgradeRootPermission(String pkgCodePath) {
        return executeCmd("chmod 777 " + pkgCodePath);
    }

    public static boolean executeCmd(String cmd) {
        return executeCmd(new String[]{cmd}).isSuccess();
    }

    public static synchronized CommandResult executeCmd(String[] commands) {
        CommandUtil util = new CommandUtil();
        util.executeCommand(commands);
        CommandResult commandResult = util.getCommandResult();
        if (commandResult == null) {
            commandResult = new CommandResult();
        }
        return commandResult;
    }
}
