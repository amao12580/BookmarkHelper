package pro.kisscat.www.bookmarkhelper.util.root;

import pro.kisscat.www.bookmarkhelper.util.command.CommandUtil;
import pro.kisscat.www.bookmarkhelper.util.command.pojo.CommandResult;
import pro.kisscat.www.bookmarkhelper.util.context.ContextUtil;

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
    public static boolean upgradeRootPermission() {
        return executeCmd("chmod 777 " + ContextUtil.getPackageCodePath(), true);
    }

    public static boolean executeCmd(String cmd) {
        return executeCmd(new String[]{cmd}, false).isSuccess();
    }

    public static boolean executeCmd(String cmd, boolean isLongCommand) {
        return executeCmd(new String[]{cmd}, isLongCommand).isSuccess();
    }

    public static synchronized CommandResult executeCmd(String[] commands, boolean isLongCommand) {
        CommandUtil util = new CommandUtil();
        util.executeCommand(commands, isLongCommand);
        CommandResult commandResult = util.getCommandResult();
        if (commandResult == null) {
            commandResult = new CommandResult();
        }
        return commandResult;
    }
}
