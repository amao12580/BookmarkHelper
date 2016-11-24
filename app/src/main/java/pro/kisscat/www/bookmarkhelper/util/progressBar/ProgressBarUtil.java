package pro.kisscat.www.bookmarkhelper.util.progressBar;

import android.view.View;

import com.daimajia.numberprogressbar.NumberProgressBar;

import lombok.Getter;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/24
 * Time:9:40
 */

public class ProgressBarUtil {
    private int min = 0;
    private int max = 0;
    private int now = 0;
    private int n = 10;
    @Getter
    private NumberProgressBar progressBar = null;

    public ProgressBarUtil(NumberProgressBar progressBar) {
        this.progressBar = progressBar;
        this.max = progressBar.getMax();
        this.now = progressBar.getProgress();
    }

    public void next() {
        int remainder = max - now;
        if (remainder <= 1) {
            now = max;
            stop();
            return;
        }
        if (remainder > min) {
            if (remainder < n) {
                n = n / 2;
            }
            now += remainder / n;
            if (now > max) {
                now = max;
            }
            progressBar.setProgress(now);
        }
    }

    public void stop() {
        progressBar.setProgress(max);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            LogHelper.e("ProgressBarUtil stop exception:" + e.getMessage());
            e.printStackTrace();
        }
        progressBar.setProgress(min);
        progressBar.setVisibility(View.GONE);
    }
}
