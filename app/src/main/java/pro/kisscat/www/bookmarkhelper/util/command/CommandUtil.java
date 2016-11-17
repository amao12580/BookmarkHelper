package pro.kisscat.www.bookmarkhelper.util.command;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/16
 * Time:8:54
 */

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

public class CommandUtil {
    private static final String COMMAND_SU = "su";
    private static final String COMMAND_EXIT = "exit\n";
    private static final String COMMAND_LINE_END = "\n";

    @Getter
    public CommandResult commandResult = null;

    public void executeCommand(String[] commands) {
        executeCommand(COMMAND_SU, commands);
    }

    private void executeCommand(String excuteUser, String[] commands) {
        // 先清空
        List<String> stdoutList = new ArrayList<>();
        List<String> erroroutList = new ArrayList<>();
        int result = -1;
        if (commands == null || commands.length == 0) {
            commandResult = new CommandResult(result);
            return;
        }
        String commandStr = Arrays.toString(commands);
        LogHelper.v("commands is:" + commandStr);
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(excuteUser);
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
            new IOThread(process.getInputStream(), stdoutList).start();
            new IOThread(process.getErrorStream(), erroroutList).start();
            result = process.waitFor();
            LogHelper.v("result is:" + result + ",successMsg is:" + JsonUtil.toJson(stdoutList) + ",errorMsg is:" + JsonUtil.toJson(erroroutList));
        } catch (IOException e) {
            LogHelper.e(MetaData.LOG_E_DEFAULT, "IOException:Root cmd 执行失败,commands:" + commandStr + ",exception:" + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            LogHelper.e(MetaData.LOG_E_DEFAULT, "InterruptedException:Root cmd 执行失败,commands:" + commandStr + ",exception:" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e1) {
                LogHelper.e(MetaData.LOG_E_DEFAULT, "IOException:Root 资源释放失败,commands:" + commandStr + ",exception:" + e1.getMessage());
                e1.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        commandResult = new CommandResult(result, stdoutList, erroroutList);
        LogHelper.v("commandResult is :" + JsonUtil.toJson(commandResult));
    }
}
