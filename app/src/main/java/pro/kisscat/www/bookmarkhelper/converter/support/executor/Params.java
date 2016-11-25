package pro.kisscat.www.bookmarkhelper.converter.support.executor;

import android.os.Handler;

import lombok.Getter;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.rule.Rule;

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
    private Rule rule;
    @Getter
    private Handler handler;

    public Params(Rule rule, Handler converterTaskMessage) {
        this.rule = rule;
        this.handler = converterTaskMessage;
    }
}
