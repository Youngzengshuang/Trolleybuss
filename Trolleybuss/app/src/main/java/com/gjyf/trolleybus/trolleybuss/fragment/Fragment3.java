package com.gjyf.trolleybus.trolleybuss.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.gjyf.trolleybus.trolleybuss.BaiduMapActivity;
import com.gjyf.trolleybus.trolleybuss.R;
import com.gjyf.trolleybus.trolleybuss.myview.MyImageView;

/**
 * 资产信息fragment
 */
public class Fragment3 extends Fragment {
    private ImageButton zhicanxinxicakan;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment3, container, false);
        initview(view);
        initlistener();
        return view;
    }

    private void initlistener() {
        zhicanxinxicakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BaiduMapActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initview(View view) {
        zhicanxinxicakan = (ImageButton) view.findViewById(R.id.zhicanxinxi);
    }

}
