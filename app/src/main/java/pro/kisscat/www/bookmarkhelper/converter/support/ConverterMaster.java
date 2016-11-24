package pro.kisscat.www.bookmarkhelper.converter.support;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import pro.kisscat.www.bookmarkhelper.converter.support.executor.ConverterExecutor;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.BaiduBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.ChromeBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.FirefoxBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.Flyme5Broswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.LiebaoBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.OupengBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.QQBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.Qihoo360Broswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.SogouBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.XBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.uc.impl.UCBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.uc.impl.UCIntlBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.via.ViaBroswerable;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.rule.Rule;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.rule.impl.ExecuteRule;
import pro.kisscat.www.bookmarkhelper.util.appList.AppListUtil;
import pro.kisscat.www.bookmarkhelper.util.progressBar.ProgressBarUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/12
 * Time:11:12
 */

public class ConverterMaster {
    @Getter
    private transient static List<Rule> supportRule;

    public static void init(Context context) {
        AppListUtil.init(context);
        if (supportRule == null) {
            supportRule = new LinkedList<>();
            ViaBroswerable viaBroswerable = ViaBroswerable.fetchViaBroswer();
            supportRule.add(new Rule(supportRule.size() + 1, context, new ChromeBroswer(), viaBroswerable));
            supportRule.add(new Rule(supportRule.size() + 1, context, new Flyme5Broswer(), viaBroswerable));
            supportRule.add(new Rule(supportRule.size() + 1, context, new UCBroswer(), viaBroswerable));
            supportRule.add(new Rule(supportRule.size() + 1, context, new UCIntlBroswer(), viaBroswerable));
            supportRule.add(new Rule(supportRule.size() + 1, context, new QQBroswer(), viaBroswerable));
            supportRule.add(new Rule(supportRule.size() + 1, context, new XBroswer(), viaBroswerable));
            supportRule.add(new Rule(supportRule.size() + 1, context, new Qihoo360Broswer(), viaBroswerable));
            supportRule.add(new Rule(supportRule.size() + 1, context, new SogouBroswer(), viaBroswerable));
            supportRule.add(new Rule(supportRule.size() + 1, context, new BaiduBroswer(), viaBroswerable));
            supportRule.add(new Rule(supportRule.size() + 1, context, new OupengBroswer(), viaBroswerable));
            supportRule.add(new Rule(supportRule.size() + 1, context, new LiebaoBroswer(), viaBroswerable));
            supportRule.add(new Rule(supportRule.size() + 1, context, new FirefoxBroswer(), viaBroswerable));
        }
    }

    public static int execute(Context context, ExecuteRule rule, ProgressBarUtil progressBarUtil) {
        return new ConverterExecutor(context, rule,progressBarUtil).execute();
    }

    public static List<ExecuteRule> cover2Execute(Context context, List<Rule> rules) {
        List<ExecuteRule> result = new LinkedList<>();
        for (Rule item : rules) {
            result.add(new ExecuteRule(context, item));
        }
        return result;
    }
}
