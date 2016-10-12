package pro.kisscat.www.bookmarkhelper.converter.support.pojo.rule.impl;

import android.content.Context;

import lombok.Getter;
import lombok.Setter;
import pro.kisscat.www.bookmarkhelper.converter.support.BasicBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.rule.Rule;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/11
 * Time:10:26
 */

public class ExcuteRule extends Rule {
    @Setter
    @Getter
    private int stage;//0=未开始，1=进行中，2=成功完成，3=失败

    @Setter
    @Getter
    private int progress;//进度值

    @Setter
    @Getter
    private String message;//toast message  成功或失败需要提示的消息内容

    private ExcuteRule(int id, Context context, BasicBroswer source, BasicBroswer target) {
        super(id, context, source, target);
    }

    private ExcuteRule(int id, Context context, Rule rule) {
        super(id, context, rule.getSource(), rule.getTarget());
    }
}
