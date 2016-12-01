package pro.kisscat.www.bookmarkhelper.init.executor;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.util.context.ContextUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.phone.PhoneUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/24
 * Time:16:11
 */

public class InitAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context context;

    public InitAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        Toast.makeText(context, context.getResources().getString(R.string.keepRootAppRunning), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(Void v) {
        LogHelper.write();
    }

    @Override
    protected Void doInBackground(Void... v) {
        ContextUtil.init(context);
        PhoneUtil.record();
        publishProgress();
        return null;
    }
}
