package pro.kisscat.www.bookmarkhelper.converter.support;

import android.content.Context;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/9
 * Time:15:31
 */

public abstract class Broswer {
    @Getter
    private Context context;
    @Setter
    @Getter
    private int bookmarkSum;
    @Getter
    private static List<Broswer> installedBroswer;
    @Getter
    private static List<Rule> supportRule;

    protected Broswer(Context context) {
        this.context = context;
    }

    /**
     * 从source转换到target
     */
    @Data
    public class Rule {
        private Broswer source;
        private Broswer target;
    }

    @Data
    public abstract class Bookmark {
        private String title;
        private String url;
    }

    @Data
    public class Show {
        private String name;
        private String icon;
    }

    /**
     * activity获取浏览器的展示信息
     */
    public abstract Show getBroswerShowInfo();

}
