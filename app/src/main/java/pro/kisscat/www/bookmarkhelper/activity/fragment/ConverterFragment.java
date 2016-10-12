package pro.kisscat.www.bookmarkhelper.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;
import pro.kisscat.www.bookmarkhelper.converter.support.BasicBroswer;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.rule.Rule;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;


/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/11
 * Time:10:36
 */

public class ConverterFragment extends ListFragment {

    private ArrayAdapter<List<View>> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        String ruleStr = getArguments().getString(MetaData.RULE_DEFINED);
        LogHelper.v("rule-after:" + ruleStr);
        //定义一个数组
        List<String> data = new ArrayList<>();
        List<List<View>> views = new ArrayList<>();
//        List<Rule> rules = JsonUtil.toList(ruleStr, Rule.class);
        List<Rule> rules = JSON.parseArray(ruleStr, Rule.class);
        Context context = getActivity();
        for (Rule rule : rules) {
            List<View> viewArray = new ArrayList<>();


            BasicBroswer sourceBorswer = rule.getSource();
            ImageView sourceImageView = new ImageView(context);
            sourceImageView.setImageDrawable(sourceBorswer.getIcon());
            sourceImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            TextView sourceTextView = new TextView(context);
//            textView.setTextSize(40);
            sourceTextView.setText(sourceBorswer.getName());


            TextView centerDescTextView = new TextView(context);
//            textView.setTextSize(40);
            centerDescTextView.setText(" TO ");


            BasicBroswer targetBorswer = rule.getTarget();
            ImageView targetImageView = new ImageView(context);
            targetImageView.setImageDrawable(targetBorswer.getIcon());
            targetImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            TextView targettextView = new TextView(context);
//            targettextView.setTextSize(40);
            targettextView.setText(targetBorswer.getName());

            viewArray.add(sourceImageView);
            viewArray.add(sourceTextView);
            viewArray.add(centerDescTextView);
            viewArray.add(targetImageView);
            viewArray.add(targettextView);
            views.add(viewArray);

//            ImageView iv = new ImageView(this);
//            iv.setImageResource(R.drawable.beerbottle);
//            RelativeLayout rl = (RelativeLayout) findViewById(R.id.RelativeLayout01);
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.WRAP_CONTENT,
//                    RelativeLayout.LayoutParams.WRAP_CONTENT);
//            lp.addRule(RelativeLayout.BELOW, R.id.ButtonRecalculate);
//            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//            rl.addView(iv, lp);

//            TextView textView = new TextView(this);
//            textView.setTextSize(40);
//            textView.setText(message);
//            setContentView(textView);


//            data.add(rule.getSource().getName() + " TO " + rule.getTarget().getName());
        }
//        adapter = new ArrayAdapter<>(context, R.layout.fragment_converter, R.id.fragment_converter_text, data);
        adapter = new ArrayAdapter<>(context, R.layout.fragment_converter, views);
        //绑定适配器时，必须通过ListFragment.setListAdapter()接口，而不是ListView.setAdapter()或其它方法
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
