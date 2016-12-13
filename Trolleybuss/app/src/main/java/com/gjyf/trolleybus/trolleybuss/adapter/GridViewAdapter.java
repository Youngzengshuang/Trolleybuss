package com.gjyf.trolleybus.trolleybuss.adapter;

/**
 * 作者：Yang on 2016/8/2 18:27
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gjyf.trolleybus.trolleybuss.R;
import com.gjyf.trolleybus.trolleybuss.myview.LargePictureActivity;
import com.gjyf.trolleybus.trolleybuss.utils.ToastUtil;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * GirdView 数据适配器
 */
public class GridViewAdapter extends BaseAdapter {
    Context context;
    List<Bitmap> list;

    public GridViewAdapter(Context _context, List<Bitmap> _list) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.list_item, null);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.itemimage);
        imageView.setImageBitmap(list.get(position));
        return convertView;
    }
}

