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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            checkPermission = PermissionUtil.check(this);
            isRoot = RootUtil.upgradeRootPermission(getPackageCodePath());
            isGetRootAccess = RootUtil.checkRootAuth();
            showToastMessage(this, "isRoot:" + isRoot + ",isGetRootAccess:" + isGetRootAccess + ",checkPermission:" + checkPermission);
            if (!isRoot) {
                showToastMessage(this, "设备未Root，无法使用.");
                finish();
            } else {
                showToastMessage(this, "成功获取了Root权限.");
            }
            LogHelper.init();
            ConverterMaster.init(this);
        }
        lv = (ListView) findViewById(R.id.lv);
        rules = ConverterMaster.getSupportRule();
        LogHelper.v(MetaData.LOG_V_BIZ, "rule-before:" + JsonUtil.toJson(rules));
        for (Rule rule : rules) {
            Map<String, Object> map = new HashMap<>();
            BasicBroswer sourceBorswer = rule.getSource();
            map.put("sourceBroswerIcon", sourceBorswer.getIcon());
            map.put("sourceBroswerAppNameText", sourceBorswer.getName());
            map.put("converterDirecterText", " TO ");
            BasicBroswer targetBorswer = rule.getTarget();
            map.put("targetBroswerIcon", targetBorswer.getIcon());
            map.put("targetBroswerAppNameText", targetBorswer.getName());
            items.add(map);
        }
        adapter = new Adapter(this, items, R.layout.listview_item, new String[]{"sourceBroswerIcon", "sourceBroswerAppNameText", "converterDirecterText", "targetBroswerIcon", "targetBroswerAppNameText"},
                new int[]{R.id.sourceBroswerIcon, R.id.sourceBroswerAppNameText, R.id.converterDirecterText, R.id.targetBroswerIcon, R.id.targetBroswerAppNameText});
        lv.setAdapter(adapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setOnItemClickListener(this);
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
            showToastMessage(this, "权限不足，exit.");
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Rule rule = ConverterMaster.getSupportRule().get((int) id);
        if (!rule.isCanUse()) {
            if (!rule.getSource().isInstalled()) {
                showToastMessage(rule.getSource().getName() + " " + lv.getResources().getString(R.string.appUninstall));
                return;
            }
            if (!rule.getTarget().isInstalled()) {
                showToastMessage(rule.getTarget().getName() + " " + lv.getResources().getString(R.string.appUninstall));
                return;
            }
        }
        showToastMessage("hit:" + id);
        processConverter(rule);
    }

    private void processConverter(Rule rule) {
        int ret;
        try {
            ret = ConverterMaster.excute(lv.getContext(), rule);
        } catch (ConverterException e) {
            showToastMessage(e.getMessage());
            return;
        }
        if (ret > 0) {
            showToastMessage("成功合并了" + ret + "条书签，请重启" + rule.getTarget().getName() + "后查看效果.");
        } else {
            showToastMessage("检测到所有书签数据已存在，不需要合并.");
        }
    }

    private void showToastMessage(String message) {
        showToastMessage(lv.getContext(), message);
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
}
