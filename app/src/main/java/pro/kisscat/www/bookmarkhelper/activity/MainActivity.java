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
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
import pro.kisscat.www.bookmarkhelper.converter.support.BasicBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.ConverterMaster;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.rule.impl.ExecuteRule;
import pro.kisscat.www.bookmarkhelper.exception.ConverterException;
import pro.kisscat.www.bookmarkhelper.exception.CrashHandler;
import pro.kisscat.www.bookmarkhelper.exception.InitException;
import pro.kisscat.www.bookmarkhelper.util.appList.AppListUtil;
import pro.kisscat.www.bookmarkhelper.util.context.ContextUtil;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.network.NetworkUtil;
import pro.kisscat.www.bookmarkhelper.util.permission.PermissionUtil;
import pro.kisscat.www.bookmarkhelper.util.phone.PhoneUtil;
import pro.kisscat.www.bookmarkhelper.util.root.RootUtil;
import pro.kisscat.www.bookmarkhelper.util.storage.InternalStorageUtil;
import pro.kisscat.www.bookmarkhelper.util.toast.ToastUtil;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView lv;
    private Adapter adapter;
    private List<ExecuteRule> rules;
    private volatile boolean isItemRuning = false;
    private boolean isRoot;
    private boolean isRecordRule = false;
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
        if (savedInstanceState == null) {
            try {
                CrashHandler handler = CrashHandler.getInstance();
                handler.init(getApplicationContext());
                Thread.setDefaultUncaughtExceptionHandler(handler);
                LogHelper.init();
                ConverterMaster.init(this);
            } catch (InitException e) {
                showToastMessage(this, e.getMessage());
                finish();
            }
        }
        if (rules == null) {
            rules = ConverterMaster.cover2Execute(this, ConverterMaster.getSupportRule());
            if (!isRecordRule) {
                LogHelper.v("executeRules:" + JsonUtil.toJson(rules), false);
            }
        }

        lv = (ListView) findViewById(R.id.listViewRules);
        for (ExecuteRule rule : rules) {
            Map<String, Object> map = new HashMap<>();
            BasicBroswer sourceBorswer = rule.getSource();
            map.put("sourceBroswerIcon", sourceBorswer.getIcon());
            map.put("sourceBroswerAppNameText", sourceBorswer.getName());
            map.put("converterDirecterImage", ContextCompat.getDrawable(this, R.drawable.ic_arrow));
            map.put("converterDirecterText", "~Biu !");
            BasicBroswer targetBorswer = rule.getTarget();
            map.put("targetBroswerIcon", targetBorswer.getIcon());
            map.put("targetBroswerAppNameText", targetBorswer.getName());
            items.add(map);
        }
        adapter = new Adapter(this, items, R.layout.listview_item, new String[]{"sourceBroswerIcon", "sourceBroswerAppNameText", "converterDirecterImage", "converterDirecterText", "targetBroswerIcon", "targetBroswerAppNameText"},
                new int[]{R.id.sourceBroswerIcon, R.id.sourceBroswerAppNameText, R.id.converterDirecterImage, R.id.converterDirecterText, R.id.targetBroswerIcon, R.id.targetBroswerAppNameText});
        lv.setAdapter(adapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        PermissionUtil.checkAndRequest(this);
        lv.setOnItemClickListener(this);
        ToastUtil.showMessage(this, this.getResources().getString(R.string.keepRootAppRunning));
        lv.post(new Runnable() {
            @Override
            public void run() {
                PhoneUtil.record();
                LogHelper.write();
            }
        });
    }

    private long lastClickItemTime = 0;
    private long currentClickItemTime = 0;
    private String someTaskIsRunning;


    @Override
    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        adapter.setSelectItem(position, view);
        adapter.notifyDataSetInvalidated();
        try {
            currentClickItemTime = System.currentTimeMillis();
            long diff = currentClickItemTime - lastClickItemTime;
            if (diff <= 1000) {
                return;
            }
            lastClickItemTime = currentClickItemTime;
            setCurrentClickItemEnabled(false);
            final ExecuteRule rule = rules.get((int) id);
            if (!rule.isCanUse()) {
                if (!rule.getSource().isInstalled(this, rule.getSource())) {
                    AppListUtil.reInit(this);
                    if (!rule.getSource().isInstalled(this, rule.getSource())) {
                        showDialogMessage(ContextUtil.buildAppNotInstalledMessage(this, rule.getSource().getName()));
                        return;
                    }
                }
                if (!rule.getTarget().isInstalled(this, rule.getTarget())) {
                    AppListUtil.reInit(this);
                    if (!rule.getTarget().isInstalled(this, rule.getTarget())) {
                        showDialogMessage(ContextUtil.buildAppNotInstalledMessage(this, rule.getTarget().getName()));
                        return;
                    }
                }
                showDialogMessage(ContextUtil.buildRuleNotSupportedNowMessage(this, rule));
                return;
            }
            ToastUtil.showMessage(this, this.getResources().getString(R.string.keepRootAppRunning));
            view.post(new Runnable() {
                public void run() {
                    processConverter(rule);
                }
            });
        } catch (InitException e) {
            showDialogMessage(e.getMessage());
            e.printStackTrace();
        }
    }

    private void setCurrentClickItemEnabled(boolean isEnable) {
        adapter.setCurrentClickItemEnabled(isEnable);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    private void processConverter(ExecuteRule rule) {
        if (isItemRuning) {
            if (someTaskIsRunning == null) {
                someTaskIsRunning = lv.getResources().getString(R.string.someTaskIsRuning);
            }
            showToastMessage(someTaskIsRunning);
            return;
        }
        isItemRuning = true;
        long start = System.currentTimeMillis();
        long end = -1;
        int ret = -1;
        try {
            isRoot = RootUtil.upgradeRootPermission(this);
            if (!isRoot) {
                String erroUpgrade = "无法获取Root权限，不能使用.";
                showSimpleDialog(erroUpgrade);
                LogHelper.v(erroUpgrade);
                return;
            } else {
                LogHelper.v("成功获取了Root权限.");
            }
            if (!InternalStorageUtil.remountDataDir()) {
                showDialogMessage(this.getResources().getString(R.string.SystemNotReadOrWriteable));
                return;
            }

            if (!InternalStorageUtil.remountSDCardDir()) {
                showDialogMessage(this.getResources().getString(R.string.SDCardNotReadOrWriteable));
                return;
            }
            start = System.currentTimeMillis();
            rule.setStage(1);
            ret = ConverterMaster.execute(lv.getContext(), rule);
            end = System.currentTimeMillis();
        } catch (ConverterException e) {
            showDialogMessage(e.getMessage());
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
                showDialogMessage(rule.getSource().getName() + "：" + ret + "条书签合并完成，重启" + rule.getTarget().getName() + "后见效" + (s == null ? "." : ("，耗时：" + s + ".")));
            } else if (ret == 0) {
                rule.setStage(2);
                showDialogMessage(rule.getSource().getName() + "：" + "所有书签已存在，不需要合并.");
            } else {
                rule.setStage(3);
            }
            isItemRuning = false;
            LogHelper.write();
            setCurrentClickItemEnabled(true);
        }
    }

    private void showDialogMessage(String message) {
        showSimpleDialog(message);
    }

    private final static BaseAnimatorSet bas_in = new FlipVerticalSwingEnter();
    private final static BaseAnimatorSet bas_out = new FadeExit();
    private static String dialog_default_title;
    private static String dialog_default_button_ok;

    /**
     * http://www.jianshu.com/p/f4d3a20d281c
     */
    private void showSimpleDialog(String message) {
        if (dialog_default_button_ok == null) {
            dialog_default_button_ok = lv.getResources().getString(R.string.dialogButtonOk);
        }
        if (dialog_default_title == null) {
            dialog_default_title = lv.getResources().getString(R.string.dialogTitle);
        }
        if (dialog_button_ok_color == null) {
            dialog_button_ok_color = ContextCompat.getColor(this, R.color.colorDialogButtonOk);
        }
        final NormalDialog dialog = new NormalDialog(this);
        dialog.isTitleShow(true)
                .title(dialog_default_title)
                .content(message)
                .btnNum(1)
                .btnText(dialog_default_button_ok)
                .btnTextColor(dialog_button_ok_color)
                .showAnim(bas_in)
                .dismissAnim(bas_out)
                .show();
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                setCurrentClickItemEnabled(true);
            }
        });
    }

    private void showToastMessage(Activity activity, String message) {
        ToastUtil.showMessage(activity, message);
    }

    private void showToastMessage(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        showToastMessage(this, message);
    }

    private static String aboutMeUrl = null;

    public void onclickAboutMe(View view) {
        if (aboutMeUrl == null) {
            aboutMeUrl = lv.getResources().getString(R.string.aboutMeURL);
        }
        openUrlInWebview(aboutMeUrl, lv.getResources().getString(R.string.aboutMeTitle), R.drawable.ic_aboutme);
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
            showToastMessage(this, lv.getResources().getString(R.string.marketAppNoInstalled));
            if (ratingURL == null) {
                ratingURL = lv.getResources().getString(R.string.ratingURL);
            }
            openUrlInWebview(ratingURL + getPackageName(), lv.getResources().getString(R.string.app_name), R.mipmap.ic_launcher);
        }
    }

    private static String donateURL = null;

    public void onclickDonate(View view) {
        if (donateURL == null) {
            donateURL = lv.getResources().getString(R.string.donateURL);
        }
        openUrlInWebview(donateURL, lv.getResources().getString(R.string.donateTitle), R.drawable.ic_donate);
    }

    private void openUrlInWebview(String url, String title, int logo) {
        if (!NetworkUtil.isNetworkConnected(this)) {
            showToastMessage(this, lv.getResources().getString(R.string.networkError));
            Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
            startActivity(intent);
            return;
        }
        Intent intent = new Intent(MainActivity.this, Html5Activity.class);
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
