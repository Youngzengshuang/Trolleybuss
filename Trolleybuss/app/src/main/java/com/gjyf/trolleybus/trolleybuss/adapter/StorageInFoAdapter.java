package com.gjyf.trolleybus.trolleybuss.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gjyf.trolleybus.trolleybuss.R;
import com.gjyf.trolleybus.trolleybuss.bean.StorageInfo;
import com.gjyf.trolleybus.trolleybuss.myview.CirclePercentView;

import java.util.List;

/**
 * @author 库房材料adapter
 */
public class StorageInFoAdapter extends BaseAdapter {
    private Context context;
    private List<StorageInfo> list;

    public StorageInFoAdapter(Context context, List<StorageInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHold viewHold = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.storage_item, null);
            viewHold = new ViewHold();
            viewHold.kufangname = (TextView) convertView.findViewById(R.id.kufangname);
            viewHold.kufangbianhao = (TextView) convertView.findViewById(R.id.kufangbianhao);
            viewHold.kucuntext = (TextView) convertView.findViewById(R.id.kucuntext);
            viewHold.kucunline = (View) convertView.findViewById(R.id.kucunline);
            viewHold.kucunnum = (TextView) convertView.findViewById(R.id.kucunnum);
            viewHold.mCirclePercentView = (CirclePercentView) convertView.findViewById(R.id.circleview);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }
        StorageInfo info = list.get(position);

        viewHold.kufangname.setText("" + info.getName());
        if (TextUtils.isEmpty(info.getType())) {
            info.setNamenum("----");
        }
        viewHold.kufangbianhao.setText("" + info.getType());
        viewHold.kufangname.setText("" + info.getName());
        viewHold.kucunnum.setText("" + info.getNum());
        if (!info.getNum().equals("0")) {
            int cirnum = (int) ((Double.parseDouble(info.getNum()) / Double.parseDouble(info.getSumnum())) * 100);
            int GREEN = Color.parseColor("#45D37F");
            int RED = Color.parseColor("#EB6000");
            if (cirnum > 50) {
                viewHold.kucunnum.setText(info.getSumnum());
                viewHold.kucuntext.setText(info.getNum());
                if (cirnum > 100) {
                    cirnum = 100;
                }
                viewHold.mCirclePercentView.setPercent(cirnum);
                viewHold.mCirclePercentView.SetColor(GREEN);
                viewHold.kucuntext.setTextColor(GREEN);
                viewHold.kucunnum.setTextColor(GREEN);
                viewHold.kucunline.setBackgroundColor(GREEN);
            } else {
                viewHold.kucunnum.setText(info.getSumnum());
                viewHold.kucuntext.setText(info.getNum());
                viewHold.mCirclePercentView.setPercent(cirnum);
                viewHold.mCirclePercentView.SetColor(RED);
                viewHold.kucuntext.setTextColor(RED);
                viewHold.kucunnum.setTextColor(RED);
                viewHold.kucunline.setBackgroundColor(RED);
            }
        } else {
            viewHold.kucunnum.setText(info.getSumnum());
            viewHold.kucuntext.setText(info.getNum());
            viewHold.mCirclePercentView.setPercent(2);
            viewHold.mCirclePercentView.SetColor(Color.RED);
            viewHold.kucuntext.setTextColor(Color.RED);
            viewHold.kucunnum.setTextColor(Color.RED);
            viewHold.kucunline.setBackgroundColor(Color.RED);
        }

        return convertView;
    }

    class ViewHold {
        private TextView kufangname, kufangbianhao;
        private TextView kucuntext, kucunnum;
        private View kucunline;
        private CirclePercentView mCirclePercentView;
    }

}
