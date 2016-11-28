package pro.kisscat.www.bookmarkhelper.util.root;

import java.util.List;

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
        return executeCmd("chmod 777 " + ContextUtil.getPackageCodePath());
    }

    public static boolean executeCmd(String cmd) {
        return checkSuccess(executeCmd(new String[]{cmd}));
    }

    private static boolean checkSuccess(CommandResult commandResult) {
        if (commandResult == null) {
            return false;
        }
        if (commandResult.getResult() < 0) {
            return false;
        }
        List<String> errors = commandResult.getErrorMsg();
        return errors == null || errors.isEmpty() || errors.size() == 1 && (errors.get(0).startsWith("WARNING:"));
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
