package pro.kisscat.www.bookmarkhelper.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import java.util.List;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.converter.support.Broswer;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.Rule;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/8
 * Time:15:06
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("monitor", "onCreate begin.");
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);


        //获取所有支持的转换规则
        Broswer.init(this);
        List<Rule> supports = Broswer.getSupportRule();
        Log.v("biz", "supports:" + JsonUtil.toJson(supports));
        Log.v("monitor", "onCreate end.");
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        getSharedPreferences()
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.put
//        super.onSaveInstanceState(outState);
//    }

//    public void sendMessage(View view) {
//        Intent intent = new Intent(this, DisplayMessageActivity.class);
//        EditText editText = (EditText) findViewById(R.id.edit_message);
//        String message = editText.getText().toString();
//        intent.putExtra(MetaData.EXTRA_MESSAGE, message);
//        startActivity(intent);
//    }
}
