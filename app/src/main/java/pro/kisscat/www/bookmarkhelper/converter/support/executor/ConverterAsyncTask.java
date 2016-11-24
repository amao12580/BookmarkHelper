package pro.kisscat.www.bookmarkhelper.converter.support.executor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.List;

import pro.kisscat.www.bookmarkhelper.converter.support.BasicBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.executor.pojo.Result;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Bookmark;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.rule.Rule;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.rule.impl.ExecuteRule;
import pro.kisscat.www.bookmarkhelper.exception.ConverterException;
import pro.kisscat.www.bookmarkhelper.util.context.ContextUtil;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.progressBar.ProgressBarUtil;
import pro.kisscat.www.bookmarkhelper.util.root.RootUtil;
import pro.kisscat.www.bookmarkhelper.util.storage.ExternalStorageUtil;
import pro.kisscat.www.bookmarkhelper.util.storage.InternalStorageUtil;

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
    private Handler handler;

    public ConverterAsyncTask(ProgressBarUtil progressBarUtil) {
        this.progressBarUtil = progressBarUtil;
    }

    //onProgressUpdate方法用于更新进度信息
    @Override
    protected void onProgressUpdate(Integer... progresses) {
        LogHelper.v(TAG, "onProgressUpdate(Progress... progresses) called");
        LogHelper.v(TAG, "progresses[0]:" + progresses[0]);
        progressBarUtil.next();
    }

    //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
    @Override
    protected void onPostExecute(Result result) {
        LogHelper.v(TAG, "onPostExecute(Result result) called");
        LogHelper.v("result:" + JsonUtil.toJson(result));
        super.onPostExecute(result);
        result.setComplete(true);
        Message message = new Message();
        message.what = 1;
        Bundle bundle = new Bundle();
        bundle.putString("result", JsonUtil.toJson(result));
        message.setData(bundle);
        handler.sendMessage(message);
        progressBarUtil.stop();
        LogHelper.write();
    }

    //doInBackground方法内部执行后台任务,不可在此方法内修改UI
    @Override
    protected Result doInBackground(Params... params) {
        LogHelper.v(TAG, "doInBackground(Params... params) called");
        Params param = params[0];
        handler = param.getHandler();
        ExecuteRule rule = param.getRule();
        handlePreExecuteMessage(rule);
        LogHelper.v("params.rule:" + JsonUtil.toJson(rule));
        return processConverter(rule);
    }

    //onCancelled方法用于在取消执行中的任务时更改UI
//    @Override
//    protected void onCancelled() {
//        LogHelper.v(TAG, "onCancelled() called");
//    }


    private void handlePreExecuteMessage(ExecuteRule rule) {
        String sourceMessage = rule.getSource().getPreExecuteConverterMessage();
        if (sourceMessage != null) {
            Message message = new Message();
            message.what = 0;
            Bundle bundle = new Bundle();
            Result result = new Result(sourceMessage);
            bundle.putString("result", JsonUtil.toJson(result));
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }

    private void handleExecuteRuningMessage() {
        Message message = new Message();
        message.what = 0;
        Bundle bundle = new Bundle();
        Result result = new Result("正在执行，约需要5秒钟，请等待.");
        bundle.putString("result", JsonUtil.toJson(result));
        message.setData(bundle);
        handler.sendMessage(message);
    }

    private Result processConverter(ExecuteRule rule) {
        Result result = new Result();
        long start = System.currentTimeMillis();
        long end = -1;
        int ret = -1;
        try {
            boolean isRoot = RootUtil.upgradeRootPermission();
            if (!isRoot) {
                String erroUpgrade = "无法获取Root权限，不能使用.";
                result.setErrorMsg(erroUpgrade);
                LogHelper.v(erroUpgrade);
                return result;
            } else {
                publishProgress(1);
                handleExecuteRuningMessage();
                LogHelper.v("成功获取了Root权限.");
            }
            if (!InternalStorageUtil.remountDataDir()) {
                result.setErrorMsg(ContextUtil.getSystemNotReadOrWriteable());
                return result;
            }
            publishProgress(1);
            if (!ExternalStorageUtil.remountSDCardDir()) {
                result.setErrorMsg(ContextUtil.getSDCardNotReadOrWriteable());
                return result;
            }
            publishProgress(1);
            start = System.currentTimeMillis();
            rule.setStage(1);
            ret = execute(rule);
            end = System.currentTimeMillis();
        } catch (ConverterException e) {
            result.setErrorMsg(e.getMessage());
            return result;
        } finally {
            if (ret > 0) {
                rule.setStage(2);
                String s = null;
                if (end > 0) {
                    long ms = end - start;
                    if (ms > 1000) {
                        s = ((end - start) / 1000) + "s";
                    } else {
                        s = ms + "ms";
                    }
                }
                result.setSuccessCount(ret);
                result.setSuccessMsg(rule.getSource().getName() + "：" + ret + "条书签合并完成，重启" + rule.getTarget().getName() + "后见效" + (s == null ? "." : ("，耗时：" + s + ".")));
            } else if (ret == 0) {
                rule.setStage(2);
                result.setSuccessCount(ret);
                result.setSuccessMsg(rule.getSource().getName() + "：" + "所有书签已存在，不需要合并.");
            } else {
                rule.setStage(3);
            }
            LogHelper.write();
        }
        publishProgress(1);
        return result;
    }

    private int execute(Rule rule) {
        BasicBroswer source = rule.getSource();
        List<Bookmark> sourceBookmarks = source.readBookmark();
        publishProgress(1);
        if (sourceBookmarks == null) {
            throw new ConverterException(ContextUtil.buildReadBookmarksErrorMessage(source.getName()));
        }
        if (sourceBookmarks.isEmpty()) {
            throw new ConverterException(ContextUtil.buildReadBookmarksEmptyMessage(source.getName()));
        }
        BasicBroswer target = rule.getTarget();
        int ret = target.appendBookmark(sourceBookmarks);
        publishProgress(1);
        if (ret < 0) {
            throw new ConverterException(ContextUtil.buildAppendBookmarksErrorMessage(target.getName()));
        }
        return ret;
    }
}
