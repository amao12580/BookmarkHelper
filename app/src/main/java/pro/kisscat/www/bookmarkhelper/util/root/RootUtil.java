package pro.kisscat.www.bookmarkhelper.util.root;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/13
 * Time:18:16
 */

public final class RootUtil {
    private static final String COMMAND_SU = "su";
    private static final String COMMAND_EXIT = "exit\n";
    private static final String COMMAND_LINE_END = "\n";

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean upgradeRootPermission(String pkgCodePath) {
        return executeCmd("chmod 777 " + pkgCodePath);
    }


    /**
     * 判断应用是否获取root权限
     */
    public static boolean checkRootAuth() {
        boolean flag = executeCmd("");
        LogHelper.v("checkRootAuth result:" + flag);
        return flag;
    }

    public static boolean executeCmd(String cmd) {
        return executeCmd(new String[]{cmd}).isSuccess();
    }

    public static synchronized CommandResult executeCmd(String[] commands) {
        int result = -1;
        boolean isNeedResultMsg = true;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result);
        }
        String commandStr = Arrays.toString(commands);
        LogHelper.v("commands is:" + commandStr);
        Process process = null;
        DataOutputStream os = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        List<String> successMsg = null;
        List<String> errorMsg = null;
        try {
            process = Runtime.getRuntime().exec(COMMAND_SU);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null || command.isEmpty()) {
                    continue;
                }
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();

            result = process.waitFor();
            LogHelper.v("process.waitFor is:" + result);
            if (isNeedResultMsg) {
                successMsg = new LinkedList<>();
                errorMsg = new LinkedList<>();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.add(s);
                }
                LogHelper.v("successMsg is:" + successMsg);
                while ((s = errorResult.readLine()) != null) {
                    errorMsg.add(s);
                }
                LogHelper.v("errorMsg is:" + errorMsg);
            }
        } catch (Exception e) {
            LogHelper.e(MetaData.LOG_E_DEFAULT, "Root cmd 执行失败,commands:" + commandStr + ",exception:" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                LogHelper.e(MetaData.LOG_E_DEFAULT, "Root 资源释放失败,commands:" + commandStr + ",exception:" + e.getMessage());
                e.printStackTrace();
            }

            if (process != null) {
                process.destroy();
            }
        }
        CommandResult commandResult = new CommandResult(result, successMsg == null ? null : successMsg, errorMsg == null ? null : errorMsg);
        LogHelper.v("commandResult is :" + JsonUtil.toJson(commandResult));
        return commandResult;
    }

    public static class CommandResult {

        /**
         * result of command
         **/
        private int result = -1;
        /**
         * success message of command result
         **/
        @Getter
        private List<String> successMsg;
        /**
         * error message of command result
         **/
        private List<String> errorMsg;

        CommandResult(int result) {
            this.result = result;
        }

        CommandResult(int result, List<String> successMsg, List<String> errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }

        public boolean isSuccess() {
            return result >= 0 && (errorMsg == null || errorMsg.isEmpty());
        }
    }
}
