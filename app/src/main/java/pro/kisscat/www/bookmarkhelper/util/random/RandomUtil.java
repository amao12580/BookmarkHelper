package pro.kisscat.www.bookmarkhelper.util.random;

import java.util.Random;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/23
 * Time:9:40
 */

public class RandomUtil {
    private static final Random random = new Random();

    public static int nextInt(int bound) {
        return random.nextInt(bound);
    }
}
