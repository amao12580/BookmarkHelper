package pro.kisscat.www.bookmarkhelper.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;
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
        String rule = getArguments().getString(MetaData.RULE_DEFINED);
        LogHelper.v("rule-after:" + rule);
        //定义一个数组
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            data.add("smyh" + i);
        }
        //将数组加到ArrayAdapter当中
//        simple_list_item_1
        adapter = new ArrayAdapter<>(getActivity(),R.layout.fragment_converter,R.id.fragment_converter_text, data);
        //绑定适配器时，必须通过ListFragment.setListAdapter()接口，而不是ListView.setAdapter()或其它方法
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
