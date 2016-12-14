package pro.kisscat.www.bookmarkhelper.entry.command;

import java.util.List;

import lombok.Getter;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

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
        if (result < 0) {
            return false;
        }
        if (errorMsg == null || errorMsg.isEmpty()) {
            return true;
        }
        LogHelper.v("errorMsg size:" + errorMsg.size());
        LogHelper.v("errorMsg:" + JsonUtil.toJson(errorMsg.size()));
        return checkEveryError(errorMsg);
    }

    private boolean checkEveryError(List<String> errors) {
        for (String item : errors) {
            if (item == null || item.isEmpty()) {
                continue;
            }
            if (!item.toUpperCase().startsWith("WARNING:")) {
                return false;
            }
        }
        return true;
    }
}
