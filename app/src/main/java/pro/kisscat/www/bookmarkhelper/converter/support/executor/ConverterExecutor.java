package pro.kisscat.www.bookmarkhelper.converter.support.executor;

import android.content.Context;
import android.os.AsyncTask;

import lombok.Getter;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.rule.impl.ExecuteRule;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.progressBar.ProgressBarUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/11/23
 * Time:11:41
 */

public class ConverterExecutor {
    private static final String TAG = "ConverterExecutor";
    private Params params;
    private ProgressBarUtil progressBarUtil;
    private Result result;

    private class Params {
        @Getter
        private Context context;
        @Getter
        private ExecuteRule rule;

        Params(Context context, ExecuteRule executeRule) {
            this.context = context;
            this.rule = executeRule;
        }
    }

    private class Result {
        @Getter
        private int successCount = 0;
        @Getter
        private String errorMsg;
        @Getter
        private String successMsg;

        Result(int successCount, String errorMsg, String successMsg) {
            this.successCount = successCount;
            this.errorMsg = errorMsg;
            this.successMsg = successMsg;
        }
    }

    private class ConverterAsyncTask extends AsyncTask<Params, Integer, Result> {
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
        protected void onPostExecute(Result excuteResult) {
            LogHelper.v(TAG, "onPostExecute(Result result) called");
            LogHelper.v("result:" + JsonUtil.toJson(excuteResult));
            super.onPostExecute(result);
            result = excuteResult;
            progressBarUtil.stop();
//            if (isQR) {
//                handler.sendEmptyMessage(0);
//            }
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected Result doInBackground(Params... params) {
            LogHelper.v(TAG, "doInBackground(Params... params) called");
            LogHelper.v("params.rule:" + JsonUtil.toJson(params[0].getRule()));
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
            return new Result(10, "abc", "def");
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
    }

    public ConverterExecutor(Context context, ExecuteRule executeRule, ProgressBarUtil progressBarUtil) {
        this.params = new Params(context, executeRule);
        this.progressBarUtil = progressBarUtil;
    }

    public int execute() {
        ConverterAsyncTask converterAsyncTask = new ConverterAsyncTask();
        converterAsyncTask.execute(params);
        LogHelper.v(TAG, "excuteResult:" + JsonUtil.toJson(result));
        return 0;
//        handler.sendEmptyMessage(0);
//        handler.sendMessage(new Message());
//        long s = System.currentTimeMillis();
//        BasicBroswer source = rule.getSource();
//        LogHelper.v("正在执行的转换规则是:" + rule.getSource().getName() + "------------>" + rule.getTarget().getName());
//        List<Bookmark> sourceBookmarks = source.readBookmark(context);
//        if (sourceBookmarks == null) {
//            throw new ConverterException(ContextUtil.buildReadBookmarksErrorMessage(context, source.getName()));
//        }
//        if (sourceBookmarks.isEmpty()) {
//            throw new ConverterException(ContextUtil.buildReadBookmarksEmptyMessage(context, source.getName()));
//        }
//        BasicBroswer target = rule.getTarget();
//        int ret = target.appendBookmark(context, sourceBookmarks);
//        if (ret < 0) {
//            throw new ConverterException(ContextUtil.buildAppendBookmarksErrorMessage(context, target.getName()));
//        }
//        LogHelper.v("转换规则:" + rule.getSource().getName() + "------------>" + rule.getTarget().getName() + "执行完成，耗时：" + (System.currentTimeMillis() - s) + "ms.");
//        return ret;
    }
}
