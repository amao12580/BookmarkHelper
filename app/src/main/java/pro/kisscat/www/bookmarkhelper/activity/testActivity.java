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
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;
import pro.kisscat.www.bookmarkhelper.util.root.RootUtil;

public class TestActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView lv;
    Adapter adapter;
    List<Rule> rules;
    boolean isRoot;
    boolean isGetRootAccess;
    List<Map<String, Object>> items = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        if (savedInstanceState == null) {
            isRoot = RootUtil.upgradeRootPermission(getPackageCodePath());
            if (!isRoot) {
                showToastMessage(this, "设备未Root，无法使用.");
                finish();
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
        adapter = new Adapter(this, items, R.layout.piitem, new String[]{"sourceBroswerIcon", "sourceBroswerAppNameText", "converterDirecterText", "targetBroswerIcon", "targetBroswerAppNameText"},
                new int[]{R.id.sourceBroswerIcon, R.id.sourceBroswerAppNameText, R.id.converterDirecterText, R.id.targetBroswerIcon, R.id.targetBroswerAppNameText});
        lv.setAdapter(adapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Rule rule = ConverterMaster.getSupportRule().get((int) id);
        if (!rule.isCanUse()) {
            if (!rule.getSource().isInstalled()) {
                showToastMessage(rule.getSource().getName() + lv.getResources().getString(R.string.appUninstall));
                return;
            }
            if (!rule.getTarget().isInstalled()) {
                showToastMessage(rule.getTarget().getName() + lv.getResources().getString(R.string.appUninstall));
                return;
            }
        }
        showToastMessage("hit:" + id);
        processConverter(rule);
    }

    private void processConverter(Rule rule) {

    }

    private void showToastMessage(String message) {
        showToastMessage(lv.getContext(), message);
    }

    private void showToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
