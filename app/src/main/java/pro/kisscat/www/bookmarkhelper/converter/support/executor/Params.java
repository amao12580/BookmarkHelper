package pro.kisscat.www.bookmarkhelper.converter.support.executor;

import android.os.Handler;

import lombok.Getter;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.rule.impl.ExecuteRule;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/24
 * Time:11:02
 */

public class Params {
    @Getter
    private ExecuteRule rule;
    @Getter
    private Handler handler;

    public Params(ExecuteRule executeRule, Handler converterTaskMessage) {
        this.rule = executeRule;
        this.handler = converterTaskMessage;
    }
}
