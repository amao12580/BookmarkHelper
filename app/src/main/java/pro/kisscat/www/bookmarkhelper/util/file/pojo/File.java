package pro.kisscat.www.bookmarkhelper.util.file.pojo;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import pro.kisscat.www.bookmarkhelper.common.shared.BuildConfig;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/10
 * Time:15:44
 */

public class File implements Comparable<File> {
    private static final long phase = 1024L;
    private static final double byteLimit = phase;
    private static final double KBLimit = phase * phase;
    private static final double MBLimit = phase * phase * phase;
    private static final double GBLimit = phase * phase * phase * phase;


    @Getter
    @Setter
    String nameWithSuffix;
    @Getter
    @Setter
    Date changeTime;
    @Getter
    @Setter
    String prefix;
    @Getter
    @Setter
    Double size;
    @Getter
    @Setter
    Long originSize;
    @Getter
    @Setter
    Measure measure;

    public File(String[] property) {
        int length = property.length;
//        同一设备，同一条ls -l命令，返回数组长度，出现了不一致，wait for fix
//        if (length != 11) {
//            throw new IllegalArgumentException("property length is invalid,property length is :" + property.length + ",data:" + Arrays.toString(property));
//        }
        if (length <= 5) {
            throw new IllegalArgumentException("property length is invalid,property length is :" + property.length + ",data:" + Arrays.toString(property));
        }
        String size = property[length - 4];
        if (size != null && !size.isEmpty()) {
            processSize(Long.valueOf(size));
        }
        String dateStr = property[length - 3];
        String timeStr = property[length - 2];
        if (dateStr != null && !dateStr.isEmpty() && timeStr != null && !timeStr.isEmpty()) {
            String dateTimeStr = dateStr + " " + timeStr;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                this.changeTime = simpleDateFormat.parse(dateTimeStr);
            } catch (ParseException e) {
                throw new IllegalArgumentException("property is invalid,date:" + dateTimeStr);
            }
        }
        this.nameWithSuffix = property[length - 1];
    }

    private void processSize(long size) {
        double tmp = size;
        String prefix = null;
        Measure sizeMeasure;
        if (tmp < byteLimit) {
            sizeMeasure = Measure.BYTE;
        } else if (tmp < KBLimit) {
            tmp = tmp / byteLimit;
            sizeMeasure = Measure.KB;
        } else if (tmp < MBLimit) {
            tmp = tmp / KBLimit;
            sizeMeasure = Measure.MB;
        } else if (tmp < GBLimit) {
            tmp = tmp / MBLimit;
            sizeMeasure = Measure.GB;
        } else {
            prefix = ">";
            tmp = tmp / MBLimit;
            sizeMeasure = Measure.GB;
        }
        this.prefix = prefix;
        this.originSize = size;
        this.size = tmp;
        this.measure = sizeMeasure;
    }

    public File(java.io.File file) {
        long length = file.length();
        String fileName = file.getName();
        processSize(length);
        this.nameWithSuffix = fileName;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (prefix != null) {
            stringBuilder.append(prefix);
        }
        stringBuilder.append(size);
        stringBuilder.append(measure.toString());
        return stringBuilder.toString();
    }

    /**
     * 文件大小是否超过10KB
     */
    public boolean isOver10KB() {
        return !BuildConfig.DEBUG && size > 10 && measure.toString().equals(Measure.KB.toString());
    }

    /**
     * 按文件修改时间进行倒序排序
     */
    @Override
    public int compareTo(@NonNull File other) {
        if (this.getChangeTime().after(other.getChangeTime())) {
            return 1;
        } else if (this.getChangeTime().before(other.getChangeTime())) {
            return -1;
        } else {
            //修改时间一致时，按大小倒序
            if (this.getOriginSize() > other.getOriginSize()) {
                return 1;
            } else if (this.getOriginSize() < other.getOriginSize()) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
