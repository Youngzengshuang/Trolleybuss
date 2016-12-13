package com.gjyf.trolleybus.trolleybuss;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gjyf.app.citylight.MyConstants;
import com.gjyf.trolleybus.trolleybuss.bean.ApprovalOutGoing;
import com.gjyf.trolleybus.trolleybuss.myview.MyProgressDialog;
import com.gjyf.trolleybus.trolleybuss.utils.HttpAPIUtils;
import com.gjyf.trolleybus.trolleybuss.utils.SharedPreferencesUtils;
import com.gjyf.trolleybus.trolleybuss.utils.ToastUtil;
import com.lidroid.xutils.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 审批信息详情
 */
public class ApprovalInfoShowActivity extends BaseActivity {
    private TextView executeDate, ioTaskNo, madeDepNo, madeName, ioTaskDate, materialJson;
    private Button yes, no;
    private EditText editText;
    private MyProgressDialog dialog;
    ApprovalOutGoing approvalOutGoing;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 400:
                    String request = (String) msg.obj;
                    if (!TextUtils.isEmpty(request)) {
                        if (request.equals("1")) {
                            dialog.dismiss();
                            ApprovalInfoShowActivity.this.finish();
                            ToastUtil.show("提交成功！");
                        }
                        if (request.equals("0")) {
                            dialog.dismiss();
                            ToastUtil.show("提交失败！");
                        }
                    }
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
        setContentView(R.layout.activity_approval_info_show);

        initview();
        initlistener();
    }

    private void initlistener() {
        //审批通过
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(editText.getText().toString().trim()) && approvalOutGoing != null) {
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("uid", SharedPreferencesUtils.getString(ApprovalInfoShowActivity.this, "uid"));
                    params.addBodyParameter("ioTaskId", approvalOutGoing.getIoTaskId());
                    params.addBodyParameter("ioTaskNo", approvalOutGoing.getIoTaskNo());
                    params.addBodyParameter("auditResult", "0");
                    params.addBodyParameter("auditAdvice", editText.getText().toString().trim());
                    HttpAPIUtils.HttpPost(handler, MyConstants.OUTGOING_APPROVAL, params);
                    dialog.show();
                } else {
                    ToastUtil.show("审批意见不能为空！");
                }

            }
        });
        //审批不通过
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(editText.getText().toString().trim()) && approvalOutGoing != null) {
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("uid", SharedPreferencesUtils.getString(ApprovalInfoShowActivity.this, "uid"));
                    params.addBodyParameter("ioTaskId", approvalOutGoing.getIoTaskId());
                    params.addBodyParameter("ioTaskNo", approvalOutGoing.getIoTaskNo());
                    params.addBodyParameter("auditResult", "1");
                    params.addBodyParameter("auditAdvice", editText.getText().toString().trim());
                    HttpAPIUtils.HttpPost(handler, MyConstants.OUTGOING_APPROVAL, params);
                    dialog.show();
                } else {
                    ToastUtil.show("审批意见不能为空！");
                }
            }
        });
    }

    private void initview() {
        dialog = MyProgressDialog.createDialog(this);
        yes = (Button) findViewById(R.id.yes);
        no = (Button) findViewById(R.id.no);
        editText = (EditText) findViewById(R.id.remark_et);
        executeDate = (TextView) findViewById(R.id.executeDate);
        ioTaskNo = (TextView) findViewById(R.id.ioTaskNo);
        madeDepNo = (TextView) findViewById(R.id.madeDepNo);
        madeName = (TextView) findViewById(R.id.madeName);
        ioTaskDate = (TextView) findViewById(R.id.ioTaskDate);
        materialJson = (TextView) findViewById(R.id.materialJson);

        Intent intent = getIntent();
        if (intent != null) {
            approvalOutGoing = (ApprovalOutGoing) intent.getSerializableExtra("approinfo");
            if (approvalOutGoing != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String date = format.format(new Date(Long.parseLong(approvalOutGoing.getIoTaskDate())));
                executeDate.setText(date);
                ioTaskDate.setText(format.format(new Date(Long.parseLong(approvalOutGoing.getIoTaskDate()))));
                ioTaskNo.setText(approvalOutGoing.getIoTaskNo());
                madeName.setText(approvalOutGoing.getMadeName());
                madeDepNo.setText(approvalOutGoing.getMadeDepNo());
                if (!TextUtils.isEmpty(approvalOutGoing.getMaterialJson())) {
                    String[] josn = approvalOutGoing.getMaterialJson().split(":");
                    String jsonstring = "";
                    for (int i = 0; i < josn.length; i++) {
                        jsonstring += josn[i] + "\n";
                    }
                    materialJson.setText(jsonstring);
                }

            }
        }


        ImageView back = (ImageView) findViewById(R.id.back);
        TextView top_text = (TextView) findViewById(R.id.main_top_text);
        top_text.setText("出库详情");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApprovalInfoShowActivity.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
