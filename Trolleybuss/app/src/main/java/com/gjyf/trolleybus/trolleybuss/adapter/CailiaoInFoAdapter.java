package com.gjyf.trolleybus.trolleybuss.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gjyf.trolleybus.trolleybuss.R;
import com.gjyf.trolleybus.trolleybuss.bean.CaiLiaoBean;

import java.util.List;

/**
 * @author 任务处理adapter
 */
public class CailiaoInFoAdapter extends BaseAdapter {
    private Context context;
    private List<CaiLiaoBean> list;

    public CailiaoInFoAdapter(Context context, List<CaiLiaoBean> list) {
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
                    R.layout.cailiao_item, null);
            viewHold = new ViewHold();
            viewHold.name = (TextView) convertView.findViewById(R.id.name);
            viewHold.num = (TextView) convertView.findViewById(R.id.num);

            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }

        try {
            CaiLiaoBean caiLiaoBean = list.get(position);
            viewHold.name.setText(caiLiaoBean.getName().trim());
            viewHold.num.setText(caiLiaoBean.getNum());
        } catch (Exception e) {

        }

        return convertView;
    }

    class ViewHold {
        private TextView name, num;
    }

}
