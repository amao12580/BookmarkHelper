package pro.kisscat.www.bookmarkhelper.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.util.List;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.activity.fragment.ConverterFragment;
import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;
import pro.kisscat.www.bookmarkhelper.converter.support.Broswer;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.rule.Rule;
import pro.kisscat.www.bookmarkhelper.util.json.JsonUtil;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/8
 * Time:15:06
 */

public class MainActivity extends AppCompatActivity {

    private ConverterFragment converterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogHelper.v("onCreate begin.");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            Broswer.init(this);
            List<Rule> supports = Broswer.getSupportRule();
            LogHelper.v(MetaData.LOG_V_BIZ, "supports:" + JsonUtil.toJson(supports.size()));
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            for (Rule rule : supports) {
            LogHelper.v(MetaData.LOG_V_BIZ, "rule-before:" + JsonUtil.toJson(supports));
            converterFragment = new ConverterFragment();
            Bundle bundle = new Bundle();
            bundle.putString(MetaData.RULE_DEFINED, JsonUtil.toJson(supports));
            converterFragment.setArguments(bundle);
            fragmentTransaction.add(R.id.fragment_container, converterFragment);
//            }
            fragmentTransaction.commit();
        }
        LogHelper.v("onCreate end.");
    }

    public void onAttachFragment(Fragment fragment) {
        //当前的界面的保存状态，只是从新让新的Fragment指向了原本未被销毁的fragment，它就是onAttach方法对应的Fragment对象
        if (converterFragment == null && fragment instanceof ConverterFragment) {
            converterFragment = (ConverterFragment) fragment;
        }
    }

    private static int count = 8000;

    public void onConverterFragmentClickItem(View view) {
        Toast.makeText(this,
                "hit " + count,
                Toast.LENGTH_SHORT).show();
        count++;
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
