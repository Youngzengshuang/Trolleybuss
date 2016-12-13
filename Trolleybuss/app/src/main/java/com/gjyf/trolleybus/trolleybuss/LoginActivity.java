package com.gjyf.trolleybus.trolleybuss;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.gjyf.app.citylight.MyApplication;
import com.gjyf.app.citylight.MyConstants;
import com.gjyf.trolleybus.trolleybuss.bean.EPCBean;
import com.gjyf.trolleybus.trolleybuss.myview.ClearEditText;
import com.gjyf.trolleybus.trolleybuss.myview.MyProgressDialog;
import com.gjyf.trolleybus.trolleybuss.utils.EPCUtils;
import com.gjyf.trolleybus.trolleybuss.utils.HttpAPIUtils;
import com.gjyf.trolleybus.trolleybuss.utils.NetWorkUtils;
import com.gjyf.trolleybus.trolleybuss.utils.SharedPreferencesUtils;
import com.gjyf.trolleybus.trolleybuss.utils.ToastUtil;
import com.gjyf.trolleybus.trolleybuss.utils.UpdateUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * 登陆
 */
public class LoginActivity extends BaseActivity {
    private Button btn_login;
    private ClearEditText etloginname, etloginpwd;
    private ImageView set;
    private MyProgressDialog dialog;
    private boolean isremember = false;
    private CheckBox rememberpwd; //记住密码
    private String username, userpwd;
    private NetWorkUtils netWorkUtils;
    private HttpUtils httpUtils;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 400:
                    dialog.dismiss();
                    String userinfo = msg.obj.toString().trim();
                    if (!TextUtils.isEmpty(userinfo)) {

                        if (userinfo.equals("0")) {
                            ToastUtil.show("用户名或者密码错误！");
                        } else {
                            String[] strings = userinfo.split(",");
                            try {
                                SharedPreferencesUtils.putString(LoginActivity.this, "username", username);
                                if (isremember) {
                                    SharedPreferencesUtils.putString(LoginActivity.this, "userpwd", userpwd);
                                } else {
                                    SharedPreferencesUtils.putString(LoginActivity.this, "userpwd", null);
                                }
                                SharedPreferencesUtils.putString(LoginActivity.this, "jobtitle", strings[0]);
                                SharedPreferencesUtils.putString(LoginActivity.this, "uid", strings[1]);
                                SharedPreferencesUtils.putString(LoginActivity.this, "personname", strings[2]);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            } catch (Exception e) {
                                ToastUtil.show("数据错误，请重试！");
                            }

                        }
                    }

                    break;
                case 404:
                    ToastUtil.show("超时，请检查网络或者服务器！");
                    dialog.dismiss();

                case 500:
                    String street = (String) msg.obj;
                    if (!TextUtils.isEmpty(street)) {
                        SharedPreferencesUtils.putString(LoginActivity.this, "streetname", street);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MyConstants.SetServiceInfo(LoginActivity.this);
        netWorkUtils = new NetWorkUtils(this);
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String szImei = TelephonyMgr.getDeviceId();
        SharedPreferencesUtils.putString(this, "szImei", szImei);
        initview();
        initlistener();
        UpdateUtils updateUtils = new UpdateUtils(getApplication());//若需要反馈是否为最新版本则传入true，不需要则传false
        updateUtils.update(true);

    }


    //控件
    private void initview() {

        set = (ImageView) findViewById(R.id.setidhost);
        httpUtils = MyApplication.getHttpUtils();
        dialog = MyProgressDialog.createDialog(this);
        btn_login = (Button) findViewById(R.id.btn_login);
        etloginname = (ClearEditText) findViewById(R.id.et_username);
        etloginpwd = (ClearEditText) findViewById(R.id.et_pwd);
        rememberpwd = (CheckBox) findViewById(R.id.rememberpwd);

        HttpAPIUtils.HttpGet(handler, MyConstants.GET_STREET);

        username = SharedPreferencesUtils.getString(this, "username");
        userpwd = SharedPreferencesUtils.getString(this, "userpwd");

        isremember = SharedPreferencesUtils.getBoolean(this, "isremember");
        if (!TextUtils.isEmpty(username)) {
            etloginname.setText(username);
        }
        if (!TextUtils.isEmpty(userpwd)) {
            etloginpwd.setText(userpwd);
        }
        if (isremember) {
            rememberpwd.setChecked(true);
            isremember = true;
        } else {
            rememberpwd.setChecked(false);
            isremember = false;
        }
    }

    private void initlistener() {

        //是否保存密码
        rememberpwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferencesUtils.putBoolean(LoginActivity.this, "isremember", b);
                isremember = b;

            }
        });

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        //登录按钮
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = etloginname.getText().toString().trim();
                userpwd = etloginpwd.getText().toString().trim();

                String ip = SharedPreferencesUtils.getString(LoginActivity.this, "ip");
                String host = SharedPreferencesUtils.getString(LoginActivity.this, "host");
                if (!TextUtils.isEmpty(ip) && !TextUtils.isEmpty(host)) {
                    if (netWorkUtils.checkNetworkState()) {
                        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(userpwd)) {
                            RequestParams params = new RequestParams();
                            params.addBodyParameter("uname", username);
                            params.addBodyParameter("upass", userpwd);
                            Log.v("yzs", MyConstants.LOGINURL);
                            HttpAPIUtils.HttpPost(handler, MyConstants.LOGINURL, params);
                            dialog.show();
                        } else {
                            ToastUtil.show("用户名密码不能为空！");
                        }
                    } else {
                        ToastUtil.show("请检查网络是否通畅！  ");
                    }
                } else {
                    ToastUtil.show("请完善服务器信息！");
                }


            }
        });
    }

    @Override
    protected void onResume() {
        MyConstants.SetServiceInfo(LoginActivity.this);
        super.onResume();
    }

    @Override
    protected void onRestart() {
        MyConstants.SetServiceInfo(LoginActivity.this);
        super.onRestart();
    }
}
