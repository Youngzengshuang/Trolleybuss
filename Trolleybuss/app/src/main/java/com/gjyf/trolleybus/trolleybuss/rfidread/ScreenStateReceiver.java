package com.gjyf.trolleybus.trolleybuss.rfidread;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.hdhe.uhf.reader.UhfReader;

public class ScreenStateReceiver extends BroadcastReceiver {

    private UhfReader reader;

    @Override
    public void onReceive(Context context, Intent intent) {
        reader = UhfReader.getInstance();
    }

}
