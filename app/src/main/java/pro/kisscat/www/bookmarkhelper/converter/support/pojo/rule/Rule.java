package pro.kisscat.www.bookmarkhelper.converter.support.pojo.rule;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/10
 * Time:10:11
 */

import android.content.Context;

import lombok.Getter;
import lombok.Setter;
import pro.kisscat.www.bookmarkhelper.converter.support.Broswer;

/**
 * 从source转换到target
 */
public class Rule {
    @Setter
    @Getter
    private int id;
    @Setter
    @Getter
    private Broswer source;
    @Setter
    @Getter
    private Broswer target;
    @Setter
    @Getter
    private boolean canUse;

    public Rule(int id, Context context, Broswer source, Broswer target) {
        boolean sourceInstalled = false, targetInstalled = false;
        if (source.isInstalled()) {
            sourceInstalled = true;
            source.readBookmarkSum(context);
        } else {
            source.fillDefaultShow(context);
        }
        if (target.isInstalled()) {
            targetInstalled = true;
            target.readBookmarkSum(context);
        } else {
            target.fillDefaultShow(context);
        }
        if (sourceInstalled && targetInstalled) {
            canUse = true;
        }
        this.id = id;
        this.source = source;
        this.target = target;
    }
}
