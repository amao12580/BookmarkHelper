package pro.kisscat.www.bookmarkhelper.activity;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/13
 * Time:11:33
 */


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pro.kisscat.www.bookmarkhelper.R;

public class Test2Activity extends Activity implements OnScrollListener,
        OnItemClickListener {

    private static final String TAG = "gjj";

    /*----ListView MVC实现----*/
    // model
    List<String> data;
    // view
    ListView lv;
    // controller
    MyAdapter adapter;
    View header;
    View footer;
    Button scrollInfo;
    Thread currentThread;
    int size = 1;

    // 初始化组件
    private void initWidget() {
        lv = (ListView) findViewById(R.id.list);
        scrollInfo = (Button) findViewById(R.id.scroll_info);
        header = getLayoutInflater().inflate(R.layout.simple_text, null);
        ((TextView) header.findViewById(R.id.text1)).setText("这是一个头部");
        footer = getLayoutInflater().inflate(R.layout.simple_text, null);
        ((TextView) footer.findViewById(R.id.text1)).setText("加载中...");
    }

    // 初始化绑定数据
    private void initData() {
        if (lv == null)
            return;
        // 第一步：获取数据源（model）
        data = new ArrayList<String>();
        appendData();

        // 第二步：new一个适配器（controller）
        // 参数1：Context
        // 参数2：listview的item布局
        // 参数3：数据填充在item布局下的那个控件id
        // 参数4：填充的数据
        // adapter = new ArrayAdapter<String>(this, R.layout.simple_text,
        // R.id.text1, data);
        adapter = new MyAdapter();
        // 第三步：给listview设置适配器（view）
        // addHeaderView和addFooterView一定要有一个在setAdapter之前调用
        lv.addHeaderView(header, "header", true);
        lv.setAdapter(adapter);
        // 这里的参数null是数据，false说明是不能被选中的
        // lv.addFooterView(footer, null, false);
        // 设置尾部无分割线，头部不想要分割线同理
        lv.setFooterDividersEnabled(false);
    }

    // 添加数据
    private void appendData() {
        if (data == null)
            return;
        for (int i = 0; i < 10; i++) {
            data.add("" + size++);
        }
    }

    // 模拟加载数据
    class DataLoadThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(2000);
                appendData();
                // 因为Android控件只能通过主线程（ui线程）更新，所以用此方法
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 加载完毕，移除尾部控件
                        lv.removeFooterView(footer);
                        // 当数据改变时调用此方法通知view更新
                        adapter.notifyDataSetChanged();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 自定义基础适配器
    class MyAdapter extends BaseAdapter {

        // listview显示的个数，如果有数据源有10条，而返回5，那么lv永远只能显示5条
        // 所以最好就返回数据源的条数就好了
        @Override
        public int getCount() {
            return data.size();
        }

        // 获取item绑定的数据时调用
        @Override
        public Object getItem(int position) {
            Log.i(TAG, "position:" + position);
            return data.get(position);
        }

        // itemId
        @Override
        public long getItemId(int position) {
            return position;
        }

        // lv显示几个item就会调用几次此方法，然后返回一个view对象显示
        // position：位置
        // convertView：如果lv不能显示全部的数据，那么滚动后会把从显示到不显示的View传进来复用
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.simple_text, null);
            } else {
                view = convertView;
            }
            TextView tv = (TextView) view.findViewById(R.id.text1);
            tv.setText(data.get(position));
            // 隔行变色，可以随心所欲
            if ((position & 1) == 1) {
                tv.setBackgroundResource(android.R.color.holo_green_light);
            } else {
                tv.setBackgroundResource(android.R.color.holo_red_light);
            }
            return view;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            // 手指接触屏幕滑动
            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                // 手指离开屏幕做惯性滑动
            case OnScrollListener.SCROLL_STATE_FLING:
                // 当滑动要最后一行时加载数据
                if (view.getLastVisiblePosition() >= view.getCount() - 2) {
                    // 可以通过网络加载数据等。
                    // 判断是否还是在加载中
                    if (currentThread == null || !currentThread.isAlive()) {
                        // 添加listview尾部控件加载中
                        lv.addFooterView(footer, null, false);
                        // 启动线程加载数据
                        currentThread = new DataLoadThread();
                        currentThread.start();
                    }
                }
                break;
            // 不滑动
            case OnScrollListener.SCROLL_STATE_IDLE:
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // firstVisibleItem:第一个显示的item位置
        // visibleItemCount：当前显示的item个数
        // totalItemCount：listview的item总个数
        scrollInfo.setText("first:" + firstVisibleItem + "  visible:"
                + visibleItemCount + "  total:" + totalItemCount);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Toast.makeText(
                this,
                "position:" + position + "  item:"
                        + parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test2);
        initWidget();
        initData();
        lv.setOnScrollListener(this);
        lv.setOnItemClickListener(this);
    }
}

