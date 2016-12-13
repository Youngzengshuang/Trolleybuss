package com.gjyf.trolleybus.trolleybuss.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gjyf.trolleybus.trolleybuss.ApprovalInfoShowActivity;
import com.gjyf.trolleybus.trolleybuss.R;
import com.gjyf.trolleybus.trolleybuss.RepairActivity;
import com.gjyf.trolleybus.trolleybuss.bean.ApprovalOutGoing;
import com.gjyf.trolleybus.trolleybuss.bean.TaskInfo;
import com.gjyf.trolleybus.trolleybuss.myview.MyProgressDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author 材料列表adapter
 */
public class ApprovalAdapter extends BaseAdapter {
    private Context context;
    private MyProgressDialog dialog;
    private List<ApprovalOutGoing> list;

    public ApprovalAdapter(Context context, List<ApprovalOutGoing> list) {
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
                    R.layout.appro_item, null);
            viewHold = new ViewHold();
            viewHold.showinfo = (Button) convertView.findViewById(R.id.button1);
            viewHold.id = (TextView) convertView.findViewById(R.id.taskid);
            viewHold.name = (TextView) convertView.findViewById(R.id.name);
            viewHold.time = (TextView) convertView.findViewById(R.id.time);

            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }

        final ApprovalOutGoing info = (ApprovalOutGoing) getItem(position);
        viewHold.id.setText(info.getIoTaskNo());
        viewHold.name.setText("申请人：" + info.getMadeName());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date(Long.parseLong(info.getIoTaskDate())));
        viewHold.time.setText("申请日期:" + date);

        viewHold.showinfo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ApprovalInfoShowActivity.class);
                intent.putExtra("approinfo", info);
                context.startActivity(intent);

            }
        });

        return convertView;
    }

    class ViewHold {
        private Button showinfo;
        private TextView id, name, time;
    }

}
