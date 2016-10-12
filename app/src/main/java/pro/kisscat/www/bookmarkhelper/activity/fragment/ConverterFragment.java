package pro.kisscat.www.bookmarkhelper.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;
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

    private ArrayAdapter<String> adapter;

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
//        List<Rule> rules = JsonUtil.toList(ruleStr, Rule.class);
        List<Rule> rules = JSON.parseArray(ruleStr, Rule.class);
        for (Rule rule : rules) {
            data.add(rule.getSource().getName() + " TO " + rule.getTarget().getName());
        }
        //将数组加到ArrayAdapter当中
        adapter = new ArrayAdapter<>(getActivity(), R.layout.fragment_converter, R.id.fragment_converter_text, data);
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
