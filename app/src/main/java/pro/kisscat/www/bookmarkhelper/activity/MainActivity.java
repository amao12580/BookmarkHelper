package pro.kisscat.www.bookmarkhelper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;
import pro.kisscat.www.bookmarkhelper.converter.support.Broswer;

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
        Log.d("monitor", "onCreate begin.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取所有支持的转换规则
        List<Broswer.Rule> supports = Broswer.getSupportRule();
        //获取所有已安装的浏览器，以及书签数据概览
        List<Broswer> installed = Broswer.getInstalledBroswer();
        Log.d("monitor", "onCreate end.");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        getSharedPreferences()
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        outState.put
        super.onSaveInstanceState(outState);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(MetaData.EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
