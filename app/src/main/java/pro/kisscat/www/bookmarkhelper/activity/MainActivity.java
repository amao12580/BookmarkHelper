package pro.kisscat.www.bookmarkhelper.activity;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/12
 * Time:15:49
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.FadeExit.FadeExit;
import com.flyco.animation.FlipEnter.FlipVerticalSwingEnter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.activity.QRCode.QRCodeActivity;
import pro.kisscat.www.bookmarkhelper.converter.support.BasicBrowser;
import pro.kisscat.www.bookmarkhelper.converter.support.ConverterMaster;
import pro.kisscat.www.bookmarkhelper.converter.support.executor.ConverterAsyncTask;
import pro.kisscat.www.bookmarkhelper.entry.converter.Params;
import pro.kisscat.www.bookmarkhelper.entry.rule.Rule;
import pro.kisscat.www.bookmarkhelper.exception.CrashHandler;
import pro.kisscat.www.bookmarkhelper.exception.InitException;
import pro.kisscat.www.bookmarkhelper.init.executor.InitAsyncTask;
import pro.kisscat.www.bookmarkhelper.pojo.executor.Result;
import pro.kisscat.www.bookmarkhelper.util.appList.AppListUtil;
import pro.kisscat.www.bookmarkhelper.util.clipboard.ClipboardUtil;
import pro.kisscat.www.bookmarkhelper.util.context.ContextUtil;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.network.NetworkUtil;
import pro.kisscat.www.bookmarkhelper.util.permission.PermissionUtil;
import pro.kisscat.www.bookmarkhelper.util.progressBar.ProgressBarUtil;
import pro.kisscat.www.bookmarkhelper.util.sign.SignUtil;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView lv;
    private Adapter adapter;
    private List<Rule> rules;
    private Context context;
    private List<Map<String, Object>> items = new ArrayList<>();

    private static Integer dialog_button_ok_color = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        lv = (ListView) findViewById(R.id.listViewRules);
        if (savedInstanceState == null) {
            try {
                CrashHandler handler = CrashHandler.getInstance();
                handler.init(context);
                Thread.setDefaultUncaughtExceptionHandler(handler);
                LogHelper.init();
                ConverterMaster.init(this);
            } catch (InitException e) {
                showToastMessage(context, e.getMessage());
                finish();
            }
        }
        if (rules == null) {
            rules = ConverterMaster.getSupportRule();
        }
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow);
        for (Rule rule : rules) {
            Map<String, Object> map = new HashMap<>();
            BasicBrowser sourceBorswer = rule.getSource();
            map.put("sourceBrowserIcon", sourceBorswer.getIcon());
            map.put("sourceBrowserAppNameText", sourceBorswer.getName());
            map.put("converterDirecterImage", drawable);
            map.put("converterDirecterText", "~Biu !");
            BasicBrowser targetBorswer = rule.getTarget();
            map.put("targetBrowserIcon", targetBorswer.getIcon());
            map.put("targetBrowserAppNameText", targetBorswer.getName());
            items.add(map);
        }
        adapter = new Adapter(this, items, R.layout.listview_item, new String[]{"sourceBrowserIcon", "sourceBrowserAppNameText", "converterDirecterImage", "converterDirecterText", "targetBrowserIcon", "targetBrowserAppNameText"},
                new int[]{R.id.sourceBrowserIcon, R.id.sourceBrowserAppNameText, R.id.converterDirecterImage, R.id.converterDirecterText, R.id.targetBrowserIcon, R.id.targetBrowserAppNameText});
        lv.setAdapter(adapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        PermissionUtil.checkAndRequest(this);
        lv.setOnItemClickListener(this);
        new InitAsyncTask(context).execute(new pro.kisscat.www.bookmarkhelper.entry.init.Params(initTaskMessage));
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private long lastClickItemTime = 0;
    private long currentClickItemTime = 0;


    @Override
    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!SignUtil.isAppCanUse) {
            LogHelper.e(context.getResources().getString(R.string.sourceValidationFailed));
            return;
        }
        adapter.setSelectItem(position, view);
        adapter.notifyDataSetInvalidated();
        try {
            currentClickItemTime = System.currentTimeMillis();
            long diff = currentClickItemTime - lastClickItemTime;
            if (diff <= 1000) {
                Toast.makeText(view.getContext(), view.getResources().getString(R.string.opeartionTooFast), Toast.LENGTH_SHORT).show();
                return;
            }
            lastClickItemTime = currentClickItemTime;
            setCurrentClickItemEnabled(false);
            Rule rule = rules.get((int) id);
            if (!rule.isCanUse()) {
                if (!rule.getSource().isInstalled(this, rule.getSource())) {
                    AppListUtil.reInit(this);
                    if (!rule.getSource().isInstalled(this, rule.getSource())) {
                        showDialogMessage(view.getContext(), ContextUtil.buildAppNotInstalledMessage(rule.getSource().getName(), rule.getSource().getPackageName()));
                        return;
                    }
                }
                if (!rule.getTarget().isInstalled(this, rule.getTarget())) {
                    AppListUtil.reInit(this);
                    if (!rule.getTarget().isInstalled(this, rule.getTarget())) {
                        showDialogMessage(view.getContext(), ContextUtil.buildAppNotInstalledMessage(rule.getTarget().getName(), rule.getSource().getPackageName()));
                        return;
                    }
                }
                showDialogMessage(view.getContext(), ContextUtil.buildRuleNotSupportedNowMessage());
                return;
            }
            NumberProgressBar progressBar = (NumberProgressBar) view.findViewById(R.id.executeProgressesBar);
            int progress = progressBar.getProgress();
            if (progress == 0) {
                Toast.makeText(view.getContext(), view.getResources().getString(R.string.keepRootAppRunning), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setClickable(false);
                progressBar.setProgress(0);
                ProgressBarUtil progressBarUtil = new ProgressBarUtil(progressBar);
                progressBarUtil.next();
                currentConverterAsyncTask = new ConverterAsyncTask(progressBarUtil);
                currentConverterAsyncTask.execute(new Params(rule, converterTaskMessage));
            } else {
                Toast.makeText(view.getContext(), view.getResources().getString(R.string.someTaskIsRuning), Toast.LENGTH_SHORT).show();
            }
        } catch (InitException e) {
            showDialogMessage(view.getContext(), e.getMessage());
            e.printStackTrace();
        }
    }

    private ConverterAsyncTask currentConverterAsyncTask;

    @SuppressLint("HandlerLeak")
    private Handler initTaskMessage = new Handler() {
        public void handleMessage(Message msg) {
            Bundle data = msg.peekData();
            if (data == null) {
                finish();
            }
            if (!data.containsKey("message") || data.get("message") == null) {
                finish();
            }
            String resultStr = data.get("message").toString();
            if (msg.what == 10) {
                if (currentConverterAsyncTask != null) {
                    currentConverterAsyncTask.setNeedFC(true);
                }
                showSimpleDialog(lv.getContext(), resultStr, true);
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler converterTaskMessage = new Handler() {
        public void handleMessage(Message msg) {
            Bundle data = msg.peekData();
            if (data == null) {
                return;
            }
            if (!data.containsKey("result") || data.get("result") == null) {
                return;
            }
            String resultStr = data.get("result").toString();
            Result result = JsonUtil.fromJson(resultStr, Result.class);
            if (result == null) {
                return;
            }
            LogHelper.v("converterTaskMessage：" + JsonUtil.toJson(result));
            if (msg.what == 0) {//toast
                if (result.getErrorMsg() != null) {
                    Toast.makeText(context, result.getErrorMsg(), Toast.LENGTH_LONG).show();
                }
                if (result.getWarnMsg() != null) {
                    Toast.makeText(context, result.getWarnMsg(), Toast.LENGTH_SHORT).show();
                }
                if (result.getSuccessMsg() != null) {
                    Toast.makeText(context, result.getSuccessMsg(), Toast.LENGTH_SHORT).show();
                }
            } else if (msg.what == 1) {//dialog
                if (result.getErrorMsg() != null) {
                    showDialogMessage(lv.getContext(), result.getErrorMsg());
                }
                if (result.getWarnMsg() != null) {
                    showDialogMessage(lv.getContext(), result.getWarnMsg());
                }
                if (result.getSuccessMsg() != null) {
                    showDialogMessage(lv.getContext(), result.getSuccessMsg());
                }
            } else {
                LogHelper.v("skip msg.what：" + msg.what);
            }
        }
    };

    private void setCurrentClickItemEnabled(boolean isEnable) {
        adapter.setCurrentClickItemEnabled(isEnable);
    }

    private void showDialogMessage(Context context, String message) {
        showSimpleDialog(context, message);
    }

    private final static BaseAnimatorSet bas_in = new FlipVerticalSwingEnter();
    private final static BaseAnimatorSet bas_out = new FadeExit();
    private static String dialog_default_title;
    private static String dialog_default_button_ok;

    /**
     * http://www.jianshu.com/p/f4d3a20d281c
     */
    private NormalDialog showSimpleDialog(Context context, String message) {
        return showSimpleDialog(context, message, false);
    }

    private NormalDialog showSimpleDialog(Context context, String message, final boolean needFC) {
        if (dialog_default_button_ok == null) {
            dialog_default_button_ok = context.getResources().getString(R.string.dialogButtonOk);
        }
        if (dialog_default_title == null) {
            dialog_default_title = context.getResources().getString(R.string.dialogTitle);
        }
        if (dialog_button_ok_color == null) {
            dialog_button_ok_color = ContextCompat.getColor(context, R.color.colorDialogButtonOk);
        }
        final NormalDialog dialog = new NormalDialog(context);
        dialog.isTitleShow(true)
                .title(dialog_default_title)
                .content(message)
                .btnNum(1)
                .btnText(dialog_default_button_ok)
                .btnTextColor(dialog_button_ok_color)
                .showAnim(bas_in)
                .dismissAnim(bas_out)
                .show();
        if (needFC) {
            dialog.autoDismiss(true);
            dialog.autoDismissDelay(1200);
        }
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (needFC) {
                    finish();
                } else {
                    setCurrentClickItemEnabled(true);
                }
            }
        });
        return dialog;
    }

    private void showToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    private static String aboutMeUrl = null;

    public void onclickAboutMe(View view) {
        if (aboutMeUrl == null) {
            aboutMeUrl = lv.getResources().getString(R.string.aboutMeURL);
        }
        ClipboardUtil.replace(context, aboutMeUrl);
        openUrlInWebview(aboutMeUrl, lv.getResources().getString(R.string.aboutMeTitle), R.drawable.ic_aboutme);
        Toast.makeText(context, context.getResources().getString(R.string.networkSlowestNotice), Toast.LENGTH_LONG).show();
    }

    private static String ratingURL = null;

    public void onclickRating(View view) {
        //这里开始执行一个应用市场跳转逻辑，默认this为Context上下文对象
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + getPackageName())); //跳转到应用市场，非Google Play市场一般情况也实现了这个接口
        //存在手机里没安装应用市场的情况，跳转会包异常，做一个接收判断
        if (intent.resolveActivity(getPackageManager()) != null) { //可以接收
            startActivity(intent);
        } else {
            //您的系统中没有安装应用市场，用WebView打开
            Toast.makeText(context, context.getResources().getString(R.string.marketAppNoInstalled), Toast.LENGTH_LONG).show();
            if (ratingURL == null) {
                ratingURL = context.getResources().getString(R.string.ratingURL);
            }
            openUrlInWebview(ratingURL + getPackageName(), lv.getResources().getString(R.string.app_name), R.mipmap.ic_launcher);
        }
    }

    public void onclickDonate(View view) {
        openDonate();
    }

    private void openDonate() {
        if (!checkNetworkAndToSetting()) {
            return;
        }
        Intent intent = new Intent(MainActivity.this, QRCodeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", context.getResources().getString(R.string.donateTitle));
        bundle.putInt("logo", R.drawable.ic_donate);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    private void openUrlInWebview(String url, String title, int logo) {
        openUrlInWebview(url, title, logo, false);
    }

    private boolean checkNetworkAndToSetting() {
        if (!NetworkUtil.isNetworkConnected(this)) {
            Toast.makeText(context, context.getResources().getString(R.string.networkError), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
            startActivity(intent);
            return false;
        }
        return true;
    }

    private void openUrlInWebview(String url, String title, int logo, boolean withQRCode) {
        if (!checkNetworkAndToSetting()) {
            return;
        }
        Intent intent;
        if (withQRCode) {
            intent = new Intent(MainActivity.this, QRCodeActivity.class);
        } else {
            intent = new Intent(MainActivity.this, Html5Activity.class);
        }
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("title", title);
        bundle.putInt("logo", logo);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    //上次按下返回键的系统时间
    private long lastBackTime = 0;
    //当前按下返回键的系统时间
    private long currentBackTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //捕获返回键按下的事件
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //获取当前系统时间的毫秒数
            currentBackTime = System.currentTimeMillis();
            //比较上次按下返回键和当前按下返回键的时间差，如果大于2秒，则提示再按一次退出
            if (currentBackTime - lastBackTime > 2 * 1000) {
                Toast.makeText(this, lv.getResources().getString(R.string.clickBackToExit), Toast.LENGTH_SHORT).show();
                lastBackTime = currentBackTime;
            } else { //如果两次按下的时间差小于2秒，则退出程序
                LogHelper.write();
                finish();
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_POWER || keyCode == KeyEvent.KEYCODE_CAMERA || keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_SEARCH) {
            LogHelper.write();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        LogHelper.write();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        LogHelper.write();
        super.onStop();
    }

    @OnShowRationale({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    void showRationaleForStorage(final PermissionRequest request) {
        showToastMessage(this, "需要存储权限来记录运行时日志");
        request.proceed();
    }

    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    void showDeniedForStorage() {
        showToastMessage(this, "你临时拒绝了存储权限");
    }

    @OnNeverAskAgain({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    void showNeverAskForStorage() {
        showToastMessage(this, "你永久拒绝了存储权限");
    }

    /**
     * 权限回调方法，用户点击允许或者拒绝之后都会调用此方法
     * requestCode 定义的权限编码--->请求码
     * permisssions 权限名称
     * grantResults 允许/拒绝
     **/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtil.checkOnly(this)) {
            showToastMessage(this, "未能获取权限，无法提供服务.");
            finish();
        }
    }
}
