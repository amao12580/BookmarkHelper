package pro.kisscat.www.bookmarkhelper.util.file;

import java.io.File;

import lombok.Getter;
import lombok.Setter;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/9
 * Time:12:44
 */

public class FileUtil {
    private static final long phase = 1024L;
    private static final double byteLimit = phase;
    private static final double KBLimit = phase * phase;
    private static final double MBLimit = phase * phase * phase;
    private static final double GBLimit = phase * phase * phase * phase;

    public static FileShow formatFileSize(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        long length = file.length();
        LogHelper.v("file name:" + file.getName() + ",length:" + length);
        double tmp = length;
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
        return new FileShow(prefix, length, tmp, sizeMeasure);
    }

    private enum Measure {
        BYTE, KB, MB, GB;

        @Override
        public String toString() {
            if (this == BYTE) {
                return "byte";
            } else {
                return super.toString();
            }
        }
    }

    public static class FileShow {
        @Getter
        @Setter
        String prefix;
        @Getter
        @Setter
        double size;
        @Getter
        @Setter
        long originSize;
        @Getter
        @Setter
        Measure measure;

        FileShow(String prefix, long originSize, double size, Measure measure) {
            this.prefix = prefix;
            this.originSize = originSize;
            this.size = size;
            this.measure = measure;
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
            return size > 10 && measure.toString().equals(Measure.KB.toString());
        }
    }
}
