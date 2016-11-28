package pro.kisscat.www.bookmarkhelper.converter.support;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.BaiduBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.FirefoxBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.Flyme5Broswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.LiebaoBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.OupengBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.QQBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.Qihoo360Broswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.SogouBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.XBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.chrome.impl.ChromeBetaBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.chrome.impl.ChromeBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.chrome.impl.ChromeCanaryBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.chrome.impl.ChromeDevBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.uc.impl.UCBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.uc.impl.UCIntlBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.via.ViaBroswerable;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.rule.Rule;
import pro.kisscat.www.bookmarkhelper.util.appList.AppListUtil;

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
            supportRule.add(new Rule(supportRule.size() + 1, context, new ChromeCanaryBroswer(), viaBroswerable));
            supportRule.add(new Rule(supportRule.size() + 1, context, new ChromeBetaBroswer(), viaBroswerable));
//            supportRule.add(new Rule(supportRule.size() + 1, context, new ChromeDevBroswer(), viaBroswerable));
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
}
