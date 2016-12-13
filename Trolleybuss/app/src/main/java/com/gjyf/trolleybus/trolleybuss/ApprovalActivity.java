package com.gjyf.trolleybus.trolleybuss;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gjyf.app.citylight.MyConstants;
import com.gjyf.trolleybus.trolleybuss.adapter.ApprovalAdapter;
import com.gjyf.trolleybus.trolleybuss.bean.ApprovalOutGoing;
import com.gjyf.trolleybus.trolleybuss.myview.MyProgressDialog;
import com.gjyf.trolleybus.trolleybuss.utils.HttpAPIUtils;
import com.gjyf.trolleybus.trolleybuss.utils.SharedPreferencesUtils;
import com.lidroid.xutils.http.RequestParams;

import java.util.ArrayList;

/**
 * 出库单信息获取
 */


public class ApprovalActivity extends BaseActivity {
    private TextView empty;
    private ListView listView;
    private ApprovalAdapter approvalAdapter;

    private MyProgressDialog dialog;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 400:
                    dialog.dismiss();
                    String st = msg.obj.toString();
                    if (!TextUtils.isEmpty(st)) {
                        Log.v("yangzss", st);
                        ArrayList<ApprovalOutGoing> approvalOutGoings = (ArrayList<ApprovalOutGoing>) JSON.parseArray(st, ApprovalOutGoing.class);
                        approvalAdapter = new ApprovalAdapter(ApprovalActivity.this, approvalOutGoings);
                        listView.setAdapter(approvalAdapter);

                    }
                    break;
                case 404:
                    dialog.dismiss();
                    empty.setText("数据加载失败，点击界面重新加载！");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval);

        initview();
        initlistener();
    }

    private void initview() {
        dialog = MyProgressDialog.createDialog(this);
        listView = (ListView) findViewById(R.id.listview);
        empty = (TextView) findViewById(R.id.empty);
        listView.setEmptyView(empty);
        ImageView back = (ImageView) findViewById(R.id.back);
        TextView top_text = (TextView) findViewById(R.id.main_top_text);
        top_text.setText("出库审批");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApprovalActivity.this.finish();
            }
        });

        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestParams params = new RequestParams();
                params.addBodyParameter("uid", SharedPreferencesUtils.getString(ApprovalActivity.this, "uid"));
                HttpAPIUtils.HttpPost(handler, MyConstants.OUTGOING_GET, params);
                dialog.show();
            }
        });
    }

    private void initlistener() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("uid", SharedPreferencesUtils.getString(ApprovalActivity.this, "uid"));
        HttpAPIUtils.HttpPost(handler, MyConstants.OUTGOING_GET, params);
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RequestParams params = new RequestParams();
        params.addBodyParameter("uid", SharedPreferencesUtils.getString(ApprovalActivity.this, "uid"));
        HttpAPIUtils.HttpPost(handler, MyConstants.OUTGOING_GET, params);
        dialog.show();
    }
}
