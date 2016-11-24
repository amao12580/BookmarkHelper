package pro.kisscat.www.bookmarkhelper.activity;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/12
 * Time:15:50
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import pro.kisscat.www.bookmarkhelper.R;

class Adapter extends SimpleAdapter {
    private int[] mTo;
    private String[] mFrom;
    private ViewBinder mViewBinder;
    private List<? extends Map<String, ?>> mData;
    private int mResource;
    private LayoutInflater mInflater;

    Adapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mData = data;
        mResource = resource;
        mFrom = from;
        mTo = to;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Getter
    private int selectItem = -1;
    @Getter
    private View currentClickItem;

    void setSelectItem(int position, View view) {
        this.currentClickItem = view;
        this.selectItem = position;
    }

    void setCurrentClickItemEnabled(boolean isEnable) {
        if (currentClickItem.isEnabled() == isEnable) {
            return;
        }
        currentClickItem.setEnabled(isEnable);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = createViewFromResource(position, convertView, parent, mResource);
        LinearLayout executeProgressesLayout = (LinearLayout) view.findViewById(R.id.executeProgressesLayout);
        if (position == selectItem) {
            view.setBackgroundResource(R.drawable.listview_item_selected_bg);
            executeProgressesLayout.setVisibility(View.VISIBLE);
        } else {
            view.setBackgroundResource(R.drawable.listview_item_default_bg);
            executeProgressesLayout.setVisibility(View.GONE);
        }
        return view;
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
        View v;
        if (convertView == null) {
            v = mInflater.inflate(resource, parent, false);
            final int[] to = mTo;
            final int count = to.length;
            final View[] holder = new View[count];
            for (int i = 0; i < count; i++) {
                holder[i] = v.findViewById(to[i]);
            }
            v.setTag(holder);
        } else {
            v = convertView;
        }
        bindView(position, v);
        return v;
    }

    private void bindView(int position, View view) {
        final Map dataSet = mData.get(position);
        if (dataSet == null) {
            return;
        }
        final ViewBinder binder = mViewBinder;
        final View[] holder = (View[]) view.getTag();
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;
        for (int i = 0; i < count; i++) {
            final View v = holder[i];
            if (v != null) {
                final Object data = dataSet.get(from[i]);
                String text = data == null ? "" : data.toString();
                if (text == null) {
                    text = "";
                }
                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, data, text);
                }
                if (!bound) {
                    //自定义适配器，关键在这里，根据传过来的控件类型以及值的数据类型，执行相应的方法
                    //可以根据自己需要自行添加if语句。另CheckBox等继承自TextView的控件也会被识别成TextView， 这就需要判断值的类型了
                    if (v instanceof TextView) {
//                        LogHelper.v("text:"+v.toString());
                        //如果是TextView控件
                        setViewText((TextView) v, text);
                        //调用SimpleAdapter自带的方法，设置文本
                    } else if (v instanceof ImageView) {//如果是ImageView控件
//                        LogHelper.v("image:"+v.toString());
                        setViewImage((ImageView) v, (Drawable) data);
                        //调用下面自己写的方法，设置图片
                    } else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a " + " view that can be bounds by this SimpleAdapter");
                    }
                }
            }
        }
    }

    private void setViewImage(ImageView v, Drawable value) {
        v.setImageDrawable(value);
    }
}