package com.gjyf.trolleybus.trolleybuss;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.gjyf.app.citylight.MyConstants;
import com.gjyf.trolleybus.trolleybuss.myview.MyProgressDialog;

public class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

    }

    @Override
    protected void onRestart() {
        MyConstants.SetServiceInfo(this);
        super.onRestart();
    }

    @Override
    protected void onResume() {
        MyConstants.SetServiceInfo(this);
        super.onResume();
    }
}
