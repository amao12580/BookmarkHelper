package pro.kisscat.www.bookmarkhelper.converter.support;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.BaiduBrowser;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.Default.impl.Flyme5Browser;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.Default.impl.MiuiBrowser;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.FirefoxBrowser;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.LiebaoBrowser;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.OupengBrowser;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.QQBrowser;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.Qihoo360Browser;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.SogouBrowser;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.XBrowser;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.chrome.impl.ChromeBetaBrowser;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.chrome.impl.ChromeBrowser;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.chrome.impl.ChromeCanaryBrowser;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.chrome.impl.ChromeDevBrowser;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.chrome.impl.YandexBrowser;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.uc.impl.UCBrowser;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.uc.impl.UCInternationalBrowser;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.via.ViaBrowserAble;
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
            ViaBrowserAble viaBrowserAble = ViaBrowserAble.fetchViaBrowser();
            supportRule.add(new Rule(supportRule.size() + 1, context, new ChromeBrowser(), viaBrowserAble));
            supportRule.add(new Rule(supportRule.size() + 1, context, new ChromeCanaryBrowser(), viaBrowserAble));
            supportRule.add(new Rule(supportRule.size() + 1, context, new ChromeBetaBrowser(), viaBrowserAble));
            supportRule.add(new Rule(supportRule.size() + 1, context, new ChromeDevBrowser(), viaBrowserAble));
            supportRule.add(new Rule(supportRule.size() + 1, context, new Flyme5Browser(), viaBrowserAble));
            supportRule.add(new Rule(supportRule.size() + 1, context, new UCBrowser(), viaBrowserAble));
            supportRule.add(new Rule(supportRule.size() + 1, context, new UCInternationalBrowser(), viaBrowserAble));
            supportRule.add(new Rule(supportRule.size() + 1, context, new QQBrowser(), viaBrowserAble));
            supportRule.add(new Rule(supportRule.size() + 1, context, new XBrowser(), viaBrowserAble));
            supportRule.add(new Rule(supportRule.size() + 1, context, new Qihoo360Browser(), viaBrowserAble));
            supportRule.add(new Rule(supportRule.size() + 1, context, new SogouBrowser(), viaBrowserAble));
            supportRule.add(new Rule(supportRule.size() + 1, context, new BaiduBrowser(), viaBrowserAble));
            supportRule.add(new Rule(supportRule.size() + 1, context, new OupengBrowser(), viaBrowserAble));
            supportRule.add(new Rule(supportRule.size() + 1, context, new LiebaoBrowser(), viaBrowserAble));
            supportRule.add(new Rule(supportRule.size() + 1, context, new FirefoxBrowser(), viaBrowserAble));
            supportRule.add(new Rule(supportRule.size() + 1, context, new MiuiBrowser(), viaBrowserAble));
            supportRule.add(new Rule(supportRule.size() + 1, context, new YandexBrowser(), viaBrowserAble));
        }
    }
}
