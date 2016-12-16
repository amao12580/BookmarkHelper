package pro.kisscat.www.bookmarkhelper.init.executor;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import pro.kisscat.www.bookmarkhelper.BuildConfig;
import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.entry.init.Params;
import pro.kisscat.www.bookmarkhelper.util.context.ContextUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.phone.PhoneUtil;
import pro.kisscat.www.bookmarkhelper.util.sign.SignUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/24
 * Time:16:11
 */

public class InitAsyncTask extends AsyncTask<Params, Void, Void> {
    private Context context;
    private Handler handler;

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
    protected Void doInBackground(Params... params) {
        Params param = params[0];
        handler = param.getHandler();
        if (!SignUtil.check(context)) {
            if (BuildConfig.DEBUG) {
                LogHelper.e("监测到不是正版软件，强制退出！");
            }
            Message message = new Message();
            message.what = 10;
            Bundle bundle = new Bundle();
            bundle.putString("message", context.getResources().getString(R.string.sourceValidationFailed));
            message.setData(bundle);
            handler.sendMessage(message);
            return null;
        }
        ContextUtil.init(context);
        PhoneUtil.record();
        publishProgress();
        return null;
    }
}
