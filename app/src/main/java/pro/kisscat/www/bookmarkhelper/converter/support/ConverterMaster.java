package pro.kisscat.www.bookmarkhelper.converter.support;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.ChromeBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.Flyme5Broswer;
import pro.kisscat.www.bookmarkhelper.converter.support.impl.ViaBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.rule.Rule;
import pro.kisscat.www.bookmarkhelper.exception.ConverterException;
import pro.kisscat.www.bookmarkhelper.util.appList.AppListUtil;
import pro.kisscat.www.bookmarkhelper.util.context.ContextUtil;

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
            supportRule.add(new Rule(supportRule.size() + 1, context, new ChromeBroswer(), new ViaBroswer()));
            supportRule.add(new Rule(supportRule.size() + 1, context, new Flyme5Broswer(), new ViaBroswer()));
        }
    }

    public static int excute(Context context, Rule rule) {
        BasicBroswer source = rule.getSource();
        List<Bookmark> sourceBookmarks = source.readBookmark(context);
        if (sourceBookmarks == null) {
            throw new ConverterException(ContextUtil.buildReadBookmarksErrorMessage(context, source.getName()));
        }
        if (sourceBookmarks.isEmpty()) {
            throw new ConverterException(ContextUtil.buildReadBookmarksEmptyMessage(context, source.getName()));
        }
        BasicBroswer target = rule.getTarget();
        int ret = target.appendBookmark(context, sourceBookmarks);
        if (ret < 0) {
            throw new ConverterException(ContextUtil.buildAppendBookmarksErrorMessage(context, target.getName()));
        }
        return ret;
    }
}
