package com.gjyf.trolleybus.trolleybuss.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gjyf.app.citylight.MyConstants;
import com.gjyf.trolleybus.trolleybuss.R;
import com.gjyf.trolleybus.trolleybuss.RepairActivity;
import com.gjyf.trolleybus.trolleybuss.bean.JIaoJIeBean;
import com.gjyf.trolleybus.trolleybuss.bean.TaskInfo;
import com.gjyf.trolleybus.trolleybuss.myview.MyProgressDialog;
import com.gjyf.trolleybus.trolleybuss.utils.HttpAPIUtils;
import com.gjyf.trolleybus.trolleybuss.utils.MyData;
import com.gjyf.trolleybus.trolleybuss.utils.SharedPreferencesUtils;
import com.gjyf.trolleybus.trolleybuss.utils.ToastUtil;
import com.lidroid.xutils.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author 交接信息adapter
 */
public class JiaoJieInFoAdapter extends BaseAdapter {
    private Context context;
    private List<JIaoJIeBean> list;
    private Handler handler;

    public JiaoJieInFoAdapter(Context context, List<JIaoJIeBean> list, Handler handler) {
        this.context = context;
        this.list = list;
        this.handler = handler;
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
                    R.layout.jiaojie_item, null);
            viewHold = new ViewHold();
            viewHold.bt = (Button) convertView.findViewById(R.id.button);
            viewHold.remark = (TextView) convertView.findViewById(R.id.remark);
            viewHold.name = (TextView) convertView.findViewById(R.id.name);
            viewHold.time = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }

        final JIaoJIeBean info = (JIaoJIeBean) getItem(position);
        viewHold.name.setText("人员编号:"+info.getShiftUserId());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date(Long.parseLong(info.getShiftDate())));
        viewHold.time.setText("申请日期:" + date);
        viewHold.remark.setText(info.getMemo());
        String flag = info.getShiftStatus();
        if (flag.equals("0")) {
            if (!SharedPreferencesUtils.getString(context, "uid").equals(info.getShiftUserId())) {
                viewHold.bt.setVisibility(View.VISIBLE);
                viewHold.bt.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RequestParams params = new RequestParams();
                        params.addBodyParameter("successionUserId", SharedPreferencesUtils.getString(context, "uid"));
                        params.addBodyParameter("shiftChgId", info.getShiftChgId());
                        params.addBodyParameter("flag", "0");
                        HttpAPIUtils.HttpPost(handler, MyConstants.JIAOJIE, params);
                    }
                });
            } else {
                viewHold.bt.setVisibility(View.INVISIBLE);
            }
        } else {
            viewHold.bt.setVisibility(View.INVISIBLE);
        }


        return convertView;
    }

    class ViewHold {
        private Button bt;
        private TextView name, remark, time;
    }

}
