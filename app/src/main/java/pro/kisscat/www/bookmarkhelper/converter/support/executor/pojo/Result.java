package pro.kisscat.www.bookmarkhelper.converter.support.executor.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/24
 * Time:11:03
 */

public class Result {
    @Setter
    @Getter
    private boolean isComplete;
    @Setter
    @Getter
    private int successCount = 0;
    @Setter
    @Getter
    private String errorMsg;
    @Setter
    @Getter
    private String warnMsg;
    @Setter
    @Getter
    private String successMsg;

    public Result() {//空的构造方法不能删除，fastjson需要用到

    }

    public Result(String warnMsg) {
        this.warnMsg = warnMsg;
    }

    public Result(int successCount, String errorMsg, String successMsg) {
        this.successCount = successCount;
        this.errorMsg = errorMsg;
        this.successMsg = successMsg;
    }
}
