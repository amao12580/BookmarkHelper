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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pro.kisscat.www.bookmarkhelper.R;

public class TestActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    ListView lv;
    Adapter adapter;
    ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        lv = (ListView) findViewById(R.id.lv);
        PackageManager pm = getPackageManager();
        //得到PackageManager对象
        List<PackageInfo> packs = pm.getInstalledPackages(0);
        //得到系统 安装的所有程序包的PackageInfo对象

        for (PackageInfo pi : packs) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("icon", pi.applicationInfo.loadIcon(pm));
            //图标
            map.put("appName", pi.applicationInfo.loadLabel(pm));
            //应用名
            map.put("packageName", pi.packageName);
            //包名
            items.add(map);
            //循环读取存到HashMap,再增加到ArrayList.一个HashMap就是一项
        }

        adapter = new Adapter(this, items, R.layout.piitem, new String[]{
                "icon", "appName", "packageName"}, new int[]{R.id.icon,
                R.id.appName, R.id.packageName});
        //参数:Context,ArrayList(item的集合),item的layout,包含ArrayList中Hashmap的key的数组,key所对应的值相对应的控件id
        lv.setAdapter(adapter);

    }
}
