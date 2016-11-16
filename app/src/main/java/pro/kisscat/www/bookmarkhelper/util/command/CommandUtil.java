package pro.kisscat.www.bookmarkhelper.util.command;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/16
 * Time:8:54
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    // 保存进程的输入流信息
    private List<String> stdoutList = new ArrayList<>();
    // 保存进程的错误流信息
    private List<String> erroroutList = new ArrayList<>();

    @Getter
    public CommandResult commandResult = null;

    public void executeCommand(String[] commands) {
        executeCommand(COMMAND_SU, commands);
    }

    private void executeCommand(String excuteUser, String[] commands) {
        // 先清空
        stdoutList.clear();
        erroroutList.clear();
        int result = -1;
        if (commands == null || commands.length == 0) {
            commandResult = new CommandResult(result);
            return;
        }
        String commandStr = Arrays.toString(commands);
        LogHelper.v("commands is:" + commandStr);
        Process process = null;
        DataOutputStream os = null;
        List<String> successMsg = null;
        List<String> errorMsg = null;
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
            // 创建2个线程，分别读取输入流缓冲区和错误流缓冲区
            new IOThread(process.getInputStream(), stdoutList).start();
            new IOThread(process.getErrorStream(), erroroutList).start();
            result = process.waitFor();
            LogHelper.v("result is:" + result);
            successMsg = stdoutList;
            LogHelper.v("successMsg is:" + JsonUtil.toJson(successMsg));
            errorMsg = erroroutList;
            LogHelper.v("errorMsg is:" + JsonUtil.toJson(successMsg));
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
        commandResult = new CommandResult(result, successMsg == null ? null : successMsg, errorMsg == null ? null : errorMsg);
        LogHelper.v("commandResult is :" + JsonUtil.toJson(commandResult));
    }
}

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
        BufferedReader br = null;
        try {
            String character = "GB2312";
            br = new BufferedReader(new InputStreamReader(inputStream, character));
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    //释放资源
                    inputStream.close();
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
