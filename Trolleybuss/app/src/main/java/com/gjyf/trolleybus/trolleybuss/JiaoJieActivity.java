package com.gjyf.trolleybus.trolleybuss;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gjyf.app.citylight.MyConstants;
import com.gjyf.trolleybus.trolleybuss.adapter.JiaoJieInFoAdapter;
import com.gjyf.trolleybus.trolleybuss.adapter.TaskHistoryInFoAdapter;
import com.gjyf.trolleybus.trolleybuss.adapter.TaskInFoAdapter;
import com.gjyf.trolleybus.trolleybuss.bean.JIaoJIeBean;
import com.gjyf.trolleybus.trolleybuss.bean.TaskInfo;
import com.gjyf.trolleybus.trolleybuss.myview.MyProgressDialog;
import com.gjyf.trolleybus.trolleybuss.myview.MyTaskHistoryDialog;
import com.gjyf.trolleybus.trolleybuss.utils.HttpAPIUtils;
import com.gjyf.trolleybus.trolleybuss.utils.MyData;
import com.gjyf.trolleybus.trolleybuss.utils.SharedPreferencesUtils;
import com.gjyf.trolleybus.trolleybuss.utils.ToastUtil;
import com.lidroid.xutils.http.RequestParams;

import java.util.ArrayList;

/**
 * 交接记录
 */
public class JiaoJieActivity extends BaseActivity implements View.OnClickListener {
    private TextView usernametext, timetext;
    private TextView tvJiaoJieHistory;
    private TextView tvJiaoJieDeal;
    private LinearLayout listview_linearlayout, inputview_linearlayout;
    private LinearLayout inputview_linearlayout1, inputview_linearlayout2;
    private MyProgressDialog dialog;
    private MyTaskHistoryDialog todialog;
    //交接班记录查询
    private TextView empty;
    private ListView listview;

    //交接班记录填写
    private EditText remark;
    private Button inputview_bt;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 400:
                    String request = (String) msg.obj;
                    if (!TextUtils.isEmpty(request)) {
                        if (request.equals("1")) {
                            ToastUtil.show("提交成功！");
                            RequestParams params = new RequestParams();
                            params.addBodyParameter("flag", "2");
                            HttpAPIUtils.HttpPost(handler, MyConstants.JIAOJIE, params);
                            inputview_linearlayout1.setVisibility(View.GONE);
                            inputview_linearlayout2.setVisibility(View.VISIBLE);
                        } else if (request.equals("0")) {
                            ToastUtil.show("提交失败！");
                            inputview_linearlayout1.setVisibility(View.VISIBLE);
                            inputview_linearlayout2.setVisibility(View.GONE);
                        } else {
                            ArrayList<JIaoJIeBean> list = (ArrayList<JIaoJIeBean>) JSON.parseArray(request, JIaoJIeBean.class);
                            if (list != null) {
                                JiaoJieInFoAdapter adapter = new JiaoJieInFoAdapter(JiaoJieActivity.this, list, handler);
                                listview.setAdapter(adapter);
                            }
                        }
                    }
                    dialog.dismiss();
                    break;
                case 404:
                    dialog.dismiss();
                    ToastUtil.show("超时，请检查网络或者服务器！");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jiao_jie);
        initview();
        initlistener();
    }

    private void initlistener() {
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                View viewt = LayoutInflater.from(JiaoJieActivity.this).inflate(
                        R.layout.jiaojie_show_info, null);
                ImageView close = (ImageView) viewt.findViewById(R.id.close);
                TextView textView = (TextView) viewt.findViewById(R.id.text);
                JIaoJIeBean bean = (JIaoJIeBean) adapterView.getItemAtPosition(i);
                textView.setText(bean.getMemo());
                MyTaskHistoryDialog.Builder builder = new MyTaskHistoryDialog.Builder(JiaoJieActivity.this);
                builder.setContentView(viewt);
                todialog = builder.create();
                todialog.show();
                close.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        todialog.dismiss();
                    }
                });
            }
        });

        inputview_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String remarkstr = remark.getText().toString().trim();
                if (!TextUtils.isEmpty(remarkstr)) {
                    SharedPreferencesUtils.putLong(JiaoJieActivity.this, SharedPreferencesUtils.getString(JiaoJieActivity.this, "uid"), System.currentTimeMillis());
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("shiftUserId", SharedPreferencesUtils.getString(JiaoJieActivity.this, "uid"));
                    params.addBodyParameter("flag", "1");
                    params.addBodyParameter("memo", remarkstr);
                    HttpAPIUtils.HttpPost(handler, MyConstants.JIAOJIE, params);
                } else {
                    ToastUtil.show("交接记录不能为空！");
                }

            }
        });
    }

    private void initview() {
        inputview_linearlayout1 = (LinearLayout) findViewById(R.id.inputview_linearlayout1);
        inputview_linearlayout2 = (LinearLayout) findViewById(R.id.inputview_linearlayout2);
        dialog = MyProgressDialog.createDialog(JiaoJieActivity.this);
        listview_linearlayout = (LinearLayout) findViewById(R.id.listview_linearlayout);
        inputview_linearlayout = (LinearLayout) findViewById(R.id.inputview_linearlayout);

        //交接记录填写
        remark = (EditText) findViewById(R.id.remark_et);
        inputview_bt = (Button) findViewById(R.id.btn_confirm);

        //交接记录查询
        listview = (ListView) findViewById(R.id.listview);
        empty = (TextView) findViewById(R.id.empty);
        listview.setEmptyView(empty);

        tvJiaoJieHistory = (TextView) findViewById(R.id.tv_history);
        tvJiaoJieDeal = (TextView) findViewById(R.id.tv_deal);
        tvJiaoJieDeal.setOnClickListener(this);
        tvJiaoJieHistory.setOnClickListener(this);

        //按钮样式处理
        tvJiaoJieDeal.setBackgroundResource(R.drawable.bg_pro_left_long_press);
        tvJiaoJieDeal.setTextColor(getResources().getColor(R.color.white));
        tvJiaoJieHistory.setBackgroundResource(R.drawable.bg_pro_right_long);
        tvJiaoJieHistory.setTextColor(getResources().getColor(R.color.text_black));


        usernametext = (TextView) findViewById(R.id.username);
        timetext = (TextView) findViewById(R.id.time);
        usernametext.setText(SharedPreferencesUtils.getString(JiaoJieActivity.this, "personname"));
        timetext.setText(MyData.getDateEN());
        ImageView back = (ImageView) findViewById(R.id.back);
        TextView top_text = (TextView) findViewById(R.id.main_top_text);
        top_text.setText("交接信息");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JiaoJieActivity.this.finish();
            }
        });
        RequestParams params = new RequestParams();
        params.addBodyParameter("flag", "2");
        HttpAPIUtils.HttpPost(handler, MyConstants.JIAOJIE, params);
        if (CheckData()) {
            inputview_linearlayout1.setVisibility(View.VISIBLE);
            inputview_linearlayout2.setVisibility(View.GONE);
        } else {
            inputview_linearlayout1.setVisibility(View.GONE);
            inputview_linearlayout2.setVisibility(View.VISIBLE);
        }
    }

    public boolean CheckData() {
        Long nowdata = System.currentTimeMillis();
        Long putdata = SharedPreferencesUtils.getLong(this, SharedPreferencesUtils.getString(JiaoJieActivity.this, "uid"));
        if (putdata != -1) {
            if (nowdata - putdata < 8 * 60 * 60 * 1000) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_deal: //
                tvJiaoJieDeal.setBackgroundResource(R.drawable.bg_pro_left_long_press);
                tvJiaoJieDeal.setTextColor(getResources().getColor(R.color.white));
                tvJiaoJieHistory.setBackgroundResource(R.drawable.bg_pro_right_long);
                tvJiaoJieHistory.setTextColor(getResources().getColor(R.color.text_black));
                listview_linearlayout.setVisibility(View.VISIBLE);
                inputview_linearlayout.setVisibility(View.INVISIBLE);
                RequestParams params = new RequestParams();
                params.addBodyParameter("flag", "2");
                HttpAPIUtils.HttpPost(handler, MyConstants.JIAOJIE, params);
                break;

            case R.id.tv_history: //
                tvJiaoJieDeal.setBackgroundResource(R.drawable.bg_pro_left_long);
                tvJiaoJieDeal.setTextColor(getResources().getColor(R.color.text_black));
                tvJiaoJieHistory.setBackgroundResource(R.drawable.bg_pro_right_long_press);
                tvJiaoJieHistory.setTextColor(getResources().getColor(R.color.white));
                listview_linearlayout.setVisibility(View.INVISIBLE);
                inputview_linearlayout.setVisibility(View.VISIBLE);
                if (CheckData()) {
                    inputview_linearlayout1.setVisibility(View.VISIBLE);
                    inputview_linearlayout2.setVisibility(View.GONE);
                } else {
                    inputview_linearlayout1.setVisibility(View.GONE);
                    inputview_linearlayout2.setVisibility(View.VISIBLE);
                }
                break;

            default:
                break;
        }
    }
}
