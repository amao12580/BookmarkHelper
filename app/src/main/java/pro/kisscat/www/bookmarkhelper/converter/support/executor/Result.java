package pro.kisscat.www.bookmarkhelper.converter.support.executor;

import lombok.Getter;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/24
 * Time:11:03
 */

public class Result {
    @Getter
    private boolean isComplete = false;
    @Getter
    private int successCount = 0;
    @Getter
    private String errorMsg;
    @Getter
    private String warnMsg;
    @Getter
    private String successMsg;

    Result(boolean isComplete, int successCount, String errorMsg, String warnMsg, String successMsg) {
        this.isComplete = isComplete;
        this.successCount = successCount;
        this.errorMsg = errorMsg;
        this.warnMsg = warnMsg;
        this.successMsg = successMsg;
    }
}
