package com.gjyf.trolleybus.trolleybuss.adapter;

/**
 * 作者：Yang on 2016/8/2 18:27
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gjyf.app.citylight.MyApplication;
import com.gjyf.trolleybus.trolleybuss.R;
import com.gjyf.trolleybus.trolleybuss.myview.LargePictureActivity;
import com.gjyf.trolleybus.trolleybuss.utils.SharedPreferencesUtils;
import com.gjyf.trolleybus.trolleybuss.utils.ToastUtil;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * GirdView 数据适配器
 */
public class GridViewShowAdapter extends BaseAdapter {
    Context context;
    List<String> list;

    public GridViewShowAdapter(Context _context, List<String> _list) {
        this.list = _list;
        this.context = _context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // http://101.201.79.110:8089/img/20161103/T20161103115041_01.jpg
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.list_item, null);
        String ip = SharedPreferencesUtils.getString(context, "ip");
        String host = SharedPreferencesUtils.getString(context, "host");
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.itemimage);
        final Bitmap[] nowbitmap = {null};
        String url = "http://" + ip + ":" + host + list.get(position);
        MyApplication.getBitmapUtils().display(imageView, url, MyApplication.getBitmapConfig(), new BitmapLoadCallBack<ImageView>() {

            @Override
            public void onLoadCompleted(ImageView imageView, String s, Bitmap bitmap, BitmapDisplayConfig bitmapDisplayConfig, BitmapLoadFrom bitmapLoadFrom) {
                imageView.setImageBitmap(bitmap);
                nowbitmap[0] = bitmap;
            }

            @Override
            public void onLoadFailed(ImageView imageView, String s, Drawable drawable) {
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nowbitmap[0] != null) {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
                    nowbitmap[0].compress(Bitmap.CompressFormat.PNG, 100, output);//把bitmap100%高质量压缩 到 output对象里
                    byte[] result = output.toByteArray();//转换成功了
                    try {
                        output.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    LargePictureActivity.actionStartBitmap(context, result);
                } else {
                    ToastUtil.show("图片未加载");
                }

            }
        });
        return convertView;
    }
}

