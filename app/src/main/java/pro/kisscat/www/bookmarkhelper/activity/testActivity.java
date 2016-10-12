package pro.kisscat.www.bookmarkhelper.activity;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/12
 * Time:15:49
 */

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

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

public class TestActivity extends Activity {
    ListView lv;
    Adapter adapter;
    List<Map<String, Object>> items = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        lv = (ListView) findViewById(R.id.lv);
        ConverterMaster.init(this);
        List<Rule> rules = ConverterMaster.getSupportRule();
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
    }
}
