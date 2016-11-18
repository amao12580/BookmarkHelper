package pro.kisscat.www.bookmarkhelper.util.command.pojo;

import java.util.List;

import lombok.Getter;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/16
 * Time:8:58
 */

public class CommandResult {

    /**
     * result of command
     **/
    @Getter
    private int result = -1;
    /**
     * success message of command result
     **/
    @Getter
    private List<String> successMsg;
    /**
     * error message of command result
     **/
    @Getter
    private List<String> errorMsg;

    public CommandResult() {
    }

    public CommandResult(int result) {
        this.result = result;
    }

    public CommandResult(int result, List<String> successMsg, List<String> errorMsg) {
        this.result = result;
        this.successMsg = successMsg;
        this.errorMsg = errorMsg;
    }

    public boolean isSuccess() {
        return result >= 0 && (errorMsg == null || errorMsg.isEmpty());
    }
}
