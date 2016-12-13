package com.gjyf.trolleybus.trolleybuss.myview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.gjyf.trolleybus.trolleybuss.R;
import com.gjyf.trolleybus.trolleybuss.utils.PhotoUtils;


public class LargePictureActivity extends Activity {


    private ZoomImageView ivLarge;

    private Bitmap mBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_picture);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
//                    1);
//        } else {
        ivLarge = (ZoomImageView) findViewById(R.id.iv_large_picture);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        if (getIntent().getExtras().getString("path") == null) {
            byte[] bitmaps = getIntent().getByteArrayExtra("bitmap");
            Bitmap bmpout = BitmapFactory.decodeByteArray(bitmaps, 0,
                    bitmaps.length);
            ivLarge.setImageBitmap(bmpout);
        } else {
            String strpath = getIntent().getExtras().getString("path");
            if (TextUtils.isEmpty(strpath)) {
            } else {
                Bitmap bitmap = PhotoUtils.createBitmap(strpath, 1920, 1080);
                ivLarge.setImageBitmap(bitmap);
            }
        }
        ivLarge.setActivity(this);
    }

    //}

    public static void actionStart(Context context, String strpath) {
        Intent intent = new Intent(context, LargePictureActivity.class);
        intent.putExtra("path", strpath);
        context.startActivity(intent);
    }

    public static void actionStartBitmap(Context context, byte[] bitmap) {
        Intent intent = new Intent(context, LargePictureActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("bitmap", bitmap);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
        System.gc();
    }
}



