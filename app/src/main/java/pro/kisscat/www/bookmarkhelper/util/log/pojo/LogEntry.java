package pro.kisscat.www.bookmarkhelper.util.log.pojo;

import java.util.Date;

import lombok.Data;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/8
 * Time:15:02
 */
@Data
public final class LogEntry {
    String level;
    String tag;
    String text;
    Date time = new Date();

    public LogEntry(String level, String tag, String text) {
        this.level = level;
        this.tag = tag;
        this.text = text;
    }
}
