package com.gjyf.trolleybus.trolleybuss;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.hdhe.uhf.reader.UhfReader;
import com.gjyf.app.citylight.MyConstants;
import com.gjyf.trolleybus.trolleybuss.myview.MyEtDialog;
import com.gjyf.trolleybus.trolleybuss.myview.MyEtIPDialog;
import com.gjyf.trolleybus.trolleybuss.utils.SharedPreferencesUtils;

/**
 * 系统设置
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private TextView serviceip, servicehost;
    private LinearLayout llserviceip, llservicehost;
    private ToggleButton ta1, ta2;
    public MyEtDialog.Builder mBuilder;
    public MyEtIPDialog.Builder mIPBuilder;
    private Button buttonMin;
    private Button buttonPlus;
    private Button buttonSet;
    private EditText editValues;
    private int value = 26;//初始值为最大，2600为26dbm(value范围16dbm~26dbm)
    private UhfReader reader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initview();
        initlistener();
    }

    private void initview() {
        reader = UhfReader.getInstance();
        buttonMin = (Button) findViewById(R.id.button_min);
        buttonPlus = (Button) findViewById(R.id.button_plus);
        buttonSet = (Button) findViewById(R.id.button_set);
        editValues = (EditText) findViewById(R.id.editText_power);

        buttonMin.setOnClickListener(this);
        buttonPlus.setOnClickListener(this);
        buttonSet.setOnClickListener(this);
        value = getSharedValue();
        editValues.setText("" + value);
        serviceip = (TextView) findViewById(R.id.tv_service_ip);
        servicehost = (TextView) findViewById(R.id.tv_service_host);
        llserviceip = (LinearLayout) findViewById(R.id.ll_service_ip);
        llservicehost = (LinearLayout) findViewById(R.id.ll_service_host);

        String ip = SharedPreferencesUtils.getString(SettingActivity.this, "ip");
        String host = SharedPreferencesUtils.getString(SettingActivity.this, "host");

        if (!TextUtils.isEmpty(ip)) {
            serviceip.setText(ip);
            MyConstants
                    .SetServiceInfo(SettingActivity.this);
        }
        if (!TextUtils.isEmpty(host)) {
            servicehost.setText(host);
            MyConstants
                    .SetServiceInfo(SettingActivity.this);
        }

        mBuilder = new MyEtDialog.Builder(this);
        mIPBuilder = new MyEtIPDialog.Builder(this);
        ImageView back = (ImageView) findViewById(R.id.back);
        TextView top_text = (TextView) findViewById(R.id.main_top_text);
        top_text.setText("系统设置");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingActivity.this.finish();
            }
        });
    }

    private void initlistener() {


        //服务器ip设置
        llserviceip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIPBuilder.setTitle("服务器IP设置");
                mIPBuilder.setText(serviceip.getText().toString());
                mIPBuilder.setNegativeButton("确认",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String strContent = mIPBuilder.getEditContent();
                                serviceip.setText(strContent);
                                SharedPreferencesUtils.putString(
                                        SettingActivity.this, "ip",
                                        strContent);
                                MyConstants
                                        .SetServiceInfo(SettingActivity.this);
                                dialog.dismiss();
                            }
                        });
                mIPBuilder.setPositiveButton("取消",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
                mIPBuilder.create().show();
            }
        });
        //服务器端口设置
        llservicehost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBuilder.setTitle("服务器端口设置");
                mBuilder.sethint("请输入信息");
                mBuilder.setText(servicehost.getText().toString().trim());
                mBuilder.setNegativeButton("确认",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String strContent = mBuilder.getEditContent();
                                servicehost.setText(strContent);
                                SharedPreferencesUtils.putString(
                                        SettingActivity.this, "host",
                                        strContent);
                                MyConstants
                                        .SetServiceInfo(SettingActivity.this);
                                dialog.dismiss();
                            }
                        });
                mBuilder.setPositiveButton("取消",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
                mBuilder.create().show();
            }
        });
    }

    //获取存储Value
    private int getSharedValue() {
        SharedPreferences shared = getSharedPreferences("power", 0);
        return shared.getInt("value", 26);
    }

    //保存Value
    private void saveSharedValue(int value) {
        SharedPreferences shared = getSharedPreferences("power", 0);
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt("value", value);
        editor.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_min://减
                if (value > 16) {
                    value = value - 1;
                }
                editValues.setText(value + "");
                break;
            case R.id.button_plus://加
                if (value < 26) {
                    value = value + 1;
                }
                editValues.setText(value + "");
                break;
            case R.id.button_set://设置
                if (reader.setOutputPower(value)) {
                    saveSharedValue(value);
                    Toast.makeText(getApplicationContext(), "设置成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "设置失败！", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }

    }

}
