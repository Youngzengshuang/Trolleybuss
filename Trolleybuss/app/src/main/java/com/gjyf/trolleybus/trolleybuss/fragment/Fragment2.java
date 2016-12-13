package com.gjyf.trolleybus.trolleybuss.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.gjyf.trolleybus.trolleybuss.ApprovalActivity;
import com.gjyf.trolleybus.trolleybuss.OutGoingActivity;
import com.gjyf.trolleybus.trolleybuss.R;
import com.gjyf.trolleybus.trolleybuss.StorageActivity;
import com.gjyf.trolleybus.trolleybuss.myview.MyImageView;
import com.gjyf.trolleybus.trolleybuss.myview.MyRadioRelativeLayout;

/**
 * 库房信息fragment
 */
public class Fragment2 extends Fragment {
    private ImageButton imageView1, imageView2, imageView3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment2, container, false);

        initview(view);
        initlistener();
        return view;
    }

    private void initlistener() {
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), OutGoingActivity.class);
                startActivity(intent);
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), StorageActivity.class);
                startActivity(intent);
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ApprovalActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initview(View view) {

        imageView1 = (ImageButton) view.findViewById(R.id.chukushenqing);
        imageView2 = (ImageButton) view.findViewById(R.id.kufangxinxi);
        imageView3 = (ImageButton) view.findViewById(R.id.cukushenpi);
    }


}
