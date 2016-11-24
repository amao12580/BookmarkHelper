package pro.kisscat.www.bookmarkhelper.util.progressBar;

import android.view.View;

import com.daimajia.numberprogressbar.NumberProgressBar;

import lombok.Getter;

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
    @Getter
    private NumberProgressBar progressBar = null;

    public ProgressBarUtil(NumberProgressBar progressBar) {
        this.progressBar = progressBar;
        this.max = progressBar.getMax();
        this.now = progressBar.getProgress();
    }

    public void next() {
        int remainder = max - now;
        if (remainder > 0) {
            int increment = remainder / 10;
            now += increment;
            progressBar.setProgress(now);
        }
    }

    public void stop() {
        progressBar.setProgress(0);
        progressBar.setVisibility(View.GONE);
    }
}
