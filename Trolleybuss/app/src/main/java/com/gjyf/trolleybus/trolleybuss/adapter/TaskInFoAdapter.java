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

import com.gjyf.app.citylight.MyApplication;
import com.gjyf.trolleybus.trolleybuss.R;
import com.gjyf.trolleybus.trolleybuss.RepairActivity;
import com.gjyf.trolleybus.trolleybuss.bean.TaskInfo;
import com.gjyf.trolleybus.trolleybuss.myview.MyProgressDialog;

import java.util.List;

/**
 * @author 任务处理adapter
 */
public class TaskInFoAdapter extends BaseAdapter {
    private Context context;
    private MyProgressDialog dialog;
    private List<TaskInfo> list;

    public TaskInFoAdapter(Context context, List<TaskInfo> list, MyProgressDialog dialog) {
        this.context = context;
        this.list = list;
        this.dialog = dialog;
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
                    R.layout.task_item, null);
            viewHold = new ViewHold();
            viewHold.showinfo = (Button) convertView.findViewById(R.id.button1);
            viewHold.id = (TextView) convertView.findViewById(R.id.taskid);
            viewHold.name = (TextView) convertView.findViewById(R.id.taskname);
            viewHold.time = (TextView) convertView.findViewById(R.id.time);
            viewHold.showinfo.setText("处理");
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }

        final TaskInfo info = (TaskInfo) getItem(position);
        viewHold.id.setText(info.getAppNo());
        viewHold.name.setText(info.getExcpDesc());
        viewHold.time.setText(info.getDistrictTown() + "," + info.getStreetNo() + "," + info.getDefailedAdderss());

        viewHold.showinfo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.show();
                Intent intent = new Intent(context, RepairActivity.class);
                intent.putExtra("taskinfo", info);
                context.startActivity(intent);

            }
        });

        return convertView;
    }

    class ViewHold {
        private Button showinfo;
        private TextView id, name, level, time;
    }

}
