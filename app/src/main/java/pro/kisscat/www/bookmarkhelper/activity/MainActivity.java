package pro.kisscat.www.bookmarkhelper.activity;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/12
 * Time:15:49
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;
import pro.kisscat.www.bookmarkhelper.converter.support.BasicBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.ConverterMaster;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.rule.Rule;
import pro.kisscat.www.bookmarkhelper.exception.ConverterException;
import pro.kisscat.www.bookmarkhelper.exception.InitException;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.network.NetworkUtil;
import pro.kisscat.www.bookmarkhelper.util.permission.PermissionUtil;
import pro.kisscat.www.bookmarkhelper.util.root.RootUtil;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView lv;
    Adapter adapter;
    List<Rule> rules;
    boolean isRoot;
    boolean isGetRootAccess;
    boolean checkPermission;
    List<Map<String, Object>> items = new ArrayList<>();

    private static Integer default_color = null;
    private static Integer choosed_color = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            try {
                LogHelper.init();
                ConverterMaster.init(this);
                initColor(this);
            } catch (InitException e) {
                showToastMessage(this, e.getMessage());
                finish();
            }
        }
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.listViewRules);
        lv.setBackgroundColor(default_color);
        rules = ConverterMaster.getSupportRule();
        LogHelper.v(MetaData.LOG_V_DEFAULT, "rule-before:" + JsonUtil.toJson(rules));
        for (Rule rule : rules) {
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
        lv.setOnItemClickListener(this);
    }

    private void initColor(Context context) {
        if (default_color == null) {
            default_color = ContextCompat.getColor(context, R.color.colorDefault);
        }
        if (choosed_color == null) {
            choosed_color = ContextCompat.getColor(context, R.color.colorItemChoosed);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionUtil.getSTORAGE_REQUEST_CODE());
//        }
//        //doNext(requestCode,grantResults);
        if (PermissionUtil.checkOnly(this)) {
            showToastMessage(this, "未能获取Root权限，无法提供服务.");
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        lv.setClickable(false);
        int choosed = parent.getPositionForView(view);
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (i != choosed) {
                parent.getChildAt(i).setBackgroundColor(default_color);
            } else {
                view.setBackgroundColor(choosed_color);
            }
        }
        try {
            Rule rule = ConverterMaster.getSupportRule().get((int) id);
            if (!rule.isCanUse()) {
                if (!rule.getSource().isInstalled(this, rule.getSource())) {
                    showDialogMessage(rule.getSource().getName() + " " + lv.getResources().getString(R.string.appUninstall));
                    return;
                }
                if (!rule.getTarget().isInstalled(this, rule.getTarget())) {
                    showDialogMessage(rule.getTarget().getName() + " " + lv.getResources().getString(R.string.appUninstall));
                    return;
                }
                showDialogMessage(lv.getResources().getString(R.string.notSupport));
            }
            processConverter(rule);
        } finally {
            lv.setClickable(true);
        }
    }

    private void processConverter(Rule rule) {
        long start = System.currentTimeMillis();
        long end;
        int ret;
        try {
            checkPermission = PermissionUtil.check(this);
            isRoot = RootUtil.upgradeRootPermission(getPackageCodePath());
//        isGetRootAccess = RootUtil.checkRootAuth();
//        showToastMessage(this, "isRoot:" + isRoot + ",isGetRootAccess:" + isGetRootAccess + ",checkPermission:" + checkPermission);
            if (!isRoot) {
                showSimpleDialog("无法获取Root权限，不能使用.");
                return;
            } else {
                showToastMessage(this, "成功获取了Root权限.");
            }
            ret = ConverterMaster.excute(lv.getContext(), rule);
            end = System.currentTimeMillis();
        } catch (ConverterException e) {
            showDialogMessage(e.getMessage());
            return;
        }
        if (ret > 0) {
            long ms = end - start;
            String s;
            if (ms > 1000) {
                s = ((end - start) / 1000) + "s";
            } else {
                s = ms + "ms";
            }

            showDialogMessage(ret + "条书签合并完成，重启" + rule.getTarget().getName() + "后见效." + s + ".");
        } else {
            showDialogMessage("所有书签已存在，不需要合并.");
        }
    }

    private void showDialogMessage(String message) {
        showSimpleDialog(message);
    }

    private static String dialogTitle = null;

    private void showSimpleDialog(String message) {
        if (dialogTitle == null) {
            dialogTitle = lv.getResources().getString(R.string.dialogTitle);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(dialogTitle);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(lv.getResources().getString(R.string.dialogClose),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        dialoginterface.dismiss();  //关闭对话框
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Toast toast;

    /**
     * Android 解决Toast的延时显示问题
     * 关键在于重用toast，这样就不用每次都创建一个新的toast
     */
    private void showToastMessage(Context context, String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public void onclickAboutMe(View view) {
        openUrlInWebview(MetaData.ABOUTMEURL, lv.getResources().getString(R.string.aboutMeTitle), R.drawable.ic_aboutme);
    }

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
            openUrlInWebview(MetaData.RatingURL + getPackageName(), lv.getResources().getString(R.string.app_name), R.mipmap.ic_launcher);
        }
    }

    public void onclickDonate(View view) {
        openUrlInWebview(MetaData.DONATEURL, lv.getResources().getString(R.string.donateTitle), R.drawable.ic_donate);
    }

    public void openUrlInWebview(String url, String title, int logo) {
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
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
