package com.gjyf.trolleybus.trolleybuss.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {
	private static Context context;
	private static Handler handler;
	private static Toast tost;

	public static final void init(final Context context) {
		ToastUtil.context = context;
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				if (tost != null) {
					tost.setText(msg.obj.toString());
					tost.setGravity(Gravity.CENTER, 0, 0);
					tost.setDuration(Toast.LENGTH_SHORT);
					tost.show();
				} else {

					tost = Toast.makeText(context, msg.obj.toString(),
							Toast.LENGTH_SHORT);
					tost.setGravity(Gravity.CENTER, 0, 0);
					tost.show();
				}

			}
		};
	}

	public static final void show(String text) {
		handler.obtainMessage(0, text).sendToTarget();
	}

	public static final void show(int resId) {
		handler.obtainMessage(0, context.getString(resId)).sendToTarget();
	}

}
