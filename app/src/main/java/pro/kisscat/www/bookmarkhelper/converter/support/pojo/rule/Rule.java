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
import pro.kisscat.www.bookmarkhelper.converter.support.BasicBroswer;

/**
 * 从source转换到target
 */
public class Rule {
    @Setter
    @Getter
    private int id;
    @Setter
    @Getter
    private BasicBroswer source;
    @Setter
    @Getter
    private BasicBroswer target;
    @Setter
    @Getter
    private boolean canUse;

    public Rule() {
    }

//    public Rule(int id, Broswer source, Broswer target, boolean canUse) {
//        this.id = id;
//        this.source = source;
//        this.target = target;
//        this.canUse = canUse;
//    }


    public Rule(int id, Context context, BasicBroswer source, BasicBroswer target) {
        boolean sourceInstalled = false, targetInstalled = false;
        if (source.isInstalled()) {
            sourceInstalled = true;
            source.readBookmarkSum(context);
        } else {
            source.fillDefaultIcon(context);
        }
        if(source.getIcon()==null){
            source.fillDefaultIcon(context);
        }
        source.fillName(context);
        if (target.isInstalled()) {
            targetInstalled = true;
            target.readBookmarkSum(context);
        } else {
            target.fillDefaultIcon(context);
        }
        if(target.getIcon()==null){
            target.fillDefaultIcon(context);
        }
        if (sourceInstalled && targetInstalled) {
            canUse = true;
        }
        target.fillName(context);
        this.id = id;
        this.source = source;
        this.target = target;
    }
}
