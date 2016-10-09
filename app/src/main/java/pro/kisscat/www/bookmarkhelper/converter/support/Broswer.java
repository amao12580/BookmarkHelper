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

/**
 *      @Data   ：注解在类上；提供类所有属性的 getting 和 setting 方法，此外还提供了equals、canEqual、hashCode、toString 方法
 *      @Setter：注解在属性上；为属性提供 setting 方法
 *      @Getter：注解在属性上；为属性提供 getting 方法
 *      @Log4j ：注解在类上；为类提供一个 属性名为log 的 log4j 日志对象
 *      @NoArgsConstructor：注解在类上；为类提供一个无参的构造方法
 *      @AllArgsConstructor：注解在类上；为类提供一个全参的构造方法
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
