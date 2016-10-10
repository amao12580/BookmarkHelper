package pro.kisscat.www.bookmarkhelper.converter.support;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.ChromeBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.Flyme5Broswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.ViaBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Rule;
import pro.kisscat.www.bookmarkhelper.pojo.App;
import pro.kisscat.www.bookmarkhelper.util.appList.AppListUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/9
 * Time:15:31
 */

/**
 * @Data ：注解在类上；提供类所有属性的 getting 和 setting 方法，此外还提供了equals、canEqual、hashCode、toString 方法
 * @Setter：注解在属性上；为属性提供 setting 方法
 * @Getter：注解在属性上；为属性提供 getting 方法
 * @Log4j ：注解在类上；为类提供一个 属性名为log 的 log4j 日志对象
 * @NoArgsConstructor：注解在类上；为类提供一个无参的构造方法
 * @AllArgsConstructor：注解在类上；为类提供一个全参的构造方法
 */

public abstract class Broswer extends App {
    @Setter
    @Getter
    protected int bookmarkSum;
    @Setter
    protected boolean installed;
    @Getter
    private static List<Rule> supportRule;

    public static void init(Context context) {
        AppListUtil.getInstalledAll(context);
        if (supportRule == null) {
            supportRule = new LinkedList<>();
            supportRule.add(new Rule(context, new Flyme5Broswer(), new ViaBroswer()));
            supportRule.add(new Rule(context, new ChromeBroswer(), new ViaBroswer()));
        }
    }

    public boolean isInstalled() {
        setInstalled(AppListUtil.isInstalled(getPackageName()));
        return installed;
    }

    public abstract void readBookmarkSum(Context context);

    public abstract void fillDefaultShow(Context context);

    public abstract String getPackageName();
}
