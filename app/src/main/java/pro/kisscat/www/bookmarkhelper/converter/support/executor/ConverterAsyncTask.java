package pro.kisscat.www.bookmarkhelper.converter.support.executor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import pro.kisscat.www.bookmarkhelper.converter.support.pojo.rule.impl.ExecuteRule;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.progressBar.ProgressBarUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/24
 * Time:10:59
 */

public class ConverterAsyncTask extends AsyncTask<Params, Integer, Result> {
    private static final String TAG = "ConverterAsyncTask";
    private ProgressBarUtil progressBarUtil;

    public ConverterAsyncTask(ProgressBarUtil progressBarUtil) {
        this.progressBarUtil = progressBarUtil;
    }

    //onPreExecute方法用于在执行后台任务前做一些UI操作
    @Override
    protected void onPreExecute() {
        LogHelper.v(TAG, "onPreExecute() called");
//            textView.setText("loading...");
    }

    //onProgressUpdate方法用于更新进度信息
    @Override
    protected void onProgressUpdate(Integer... progresses) {
        LogHelper.v(TAG, "onProgressUpdate(Progress... progresses) called");
        LogHelper.v(TAG, "progresses[0]:" + progresses[0]);
        progressBarUtil.next();
//            progressBar.setProgress(progresses[0]);
//            textView.setText("loading..." + progresses[0] + "%");
    }

    //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
    @Override
    protected void onPostExecute(Result result) {
        LogHelper.v(TAG, "onPostExecute(Result result) called");
        LogHelper.v("result:" + JsonUtil.toJson(result));
        super.onPostExecute(result);
//        result = excuteResult;
        progressBarUtil.stop();
//            if (isQR) {
//                handler.sendEmptyMessage(0);
//            }
    }

    //doInBackground方法内部执行后台任务,不可在此方法内修改UI
    @Override
    protected Result doInBackground(Params... params) {
        LogHelper.v(TAG, "doInBackground(Params... params) called");
        Params param = params[0];
        Handler handler = param.getHandler();
        ExecuteRule rule = param.getRule();
        handlePreExecuteMessage(rule, handler);
        LogHelper.v("params.rule:" + JsonUtil.toJson(rule));
//            decodeImage(params[0]);
        int count = 0;
        int total = 100;
        while (total > 0) {
            //调用publishProgress公布进度,最后onProgressUpdate方法将被执行
            publishProgress((int) ((count / (float) total) * 100));
            //为了演示进度,休眠500毫秒
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
            total--;
        }
        Message message = new Message();
        message.what = 1;
        Bundle bundle = new Bundle();
        Result result = new Result(true, 10, "abc", "", "def");
        bundle.putString("result", JsonUtil.toJson(result));
        message.setData(bundle);
        handler.sendMessage(message);
        return result;
    }

    //onCancelled方法用于在取消执行中的任务时更改UI
    @Override
    protected void onCancelled() {
        LogHelper.v(TAG, "onCancelled() called");
//            textView.setText("cancelled");
////            progressBar.setProgress(0);

//            execute.setEnabled(true);
//            cancel.setEnabled(false);
    }


    private void handlePreExecuteMessage(ExecuteRule rule, Handler handler) {
        String sourceMessage = rule.getSource().getPreExecuteConverterMessage();
        if (sourceMessage != null) {
            Message message = new Message();
            message.what = 0;
            Bundle bundle = new Bundle();
            Result result = new Result(false, 0, null, sourceMessage, null);
            bundle.putString("result", JsonUtil.toJson(result));
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }
}
