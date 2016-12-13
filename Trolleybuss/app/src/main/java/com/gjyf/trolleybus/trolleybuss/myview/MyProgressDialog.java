package com.gjyf.trolleybus.trolleybuss.myview;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

import com.gjyf.trolleybus.trolleybuss.R;


public class MyProgressDialog extends Dialog {

    private Context mContext;
    private static MyProgressDialog myProgressDialog;

    public MyProgressDialog(Context context) {
        super(context);
        mContext = context;
    }

    public MyProgressDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public static MyProgressDialog createDialog(Context context) {
        myProgressDialog = new MyProgressDialog(context, R.style.dialog);
        myProgressDialog.setContentView(R.layout.login_loading);
        myProgressDialog.setCancelable(false);
        myProgressDialog.setOnKeyListener(onKeyListener);
//		myProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        return myProgressDialog;
    }

    private static OnKeyListener onKeyListener = new OnKeyListener() {

        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                dialog.dismiss();
            }
            return false;
        }
    };

}
