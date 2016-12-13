package com.gjyf.trolleybus.trolleybuss.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.gjyf.app.citylight.MyApplication;
import com.gjyf.app.citylight.MyConstants;
import com.gjyf.trolleybus.trolleybuss.MainActivity;
import com.gjyf.trolleybus.trolleybuss.R;
import com.gjyf.trolleybus.trolleybuss.adapter.TaskHistoryInFoAdapter;
import com.gjyf.trolleybus.trolleybuss.adapter.TaskInFoAdapter;
import com.gjyf.trolleybus.trolleybuss.bean.TaskInfo;
import com.gjyf.trolleybus.trolleybuss.myview.MyProgressDialog;
import com.gjyf.trolleybus.trolleybuss.myview.MyTaskHistoryDialog;
import com.gjyf.trolleybus.trolleybuss.myview.XListView;
import com.gjyf.trolleybus.trolleybuss.utils.HttpAPIUtils;
import com.gjyf.trolleybus.trolleybuss.utils.MyData;
import com.gjyf.trolleybus.trolleybuss.utils.OrientationSensor;
import com.gjyf.trolleybus.trolleybuss.utils.SharedPreferencesUtils;
import com.gjyf.trolleybus.trolleybuss.utils.SimpleCrypto;
import com.gjyf.trolleybus.trolleybuss.utils.ToastUtil;
import com.lidroid.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

/**
 * 抢修界面fragment
 */
public class Fragment1 extends Fragment {
    private XListView xListView;// 任务处理列表
    private Button home, taskhistory;
    private List<TaskInfo> infos = new ArrayList<>(); // 任务信息
    private TaskInFoAdapter adapter;
    private TextView empty, distance, task_num;
    private MyProgressDialog dialog;
    private MyTaskHistoryDialog taskHistoryDialog;
    private float driection = 0;
    private BDLocation nowbdLocation;
    //地图相关
    private OrientationSensor mOrientationSensor;
    private LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    BitmapDescriptor mCurrentMarker;
    InfoWindow mInfoWindow;
    private MyLocationConfiguration.LocationMode mCurrentMode;

    TextureMapView mMapView;
    BaiduMap mBaiduMap;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //任务历史
                case 300:
                    String hisrequest = (String) msg.obj;
                    if (!TextUtils.isEmpty(hisrequest)) {
                        dialog.dismiss();
                        ArrayList<TaskInfo> historytaskInfos = (ArrayList<TaskInfo>) JSON.parseArray(hisrequest, TaskInfo.class);
                        if (historytaskInfos.size() > 0) {
                            View viewt = LayoutInflater.from(getActivity()).inflate(
                                    R.layout.task_history_show_info, null);
                            ImageView close = (ImageView) viewt.findViewById(R.id.close);
                            ListView listView = (ListView) viewt.findViewById(R.id.task_history_list);
                            TaskHistoryInFoAdapter adapter = new TaskHistoryInFoAdapter(getActivity(), historytaskInfos);
                            listView.setAdapter(adapter);

                            MyTaskHistoryDialog.Builder builder = new MyTaskHistoryDialog.Builder(getActivity());
                            builder.setContentView(viewt);
                            taskHistoryDialog = builder.create();
                            taskHistoryDialog.show();
                            close.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    taskHistoryDialog.dismiss();
                                }
                            });
                        }
                    } else {
                        dialog.dismiss();
                    }
                    break;
                //待处理任务
                case 400:
                    String request = (String) msg.obj;
                    if (!TextUtils.isEmpty(request)) {
                        infos.clear();
                        dialog.dismiss();
                        infos = JSON.parseArray(request, TaskInfo.class);
                        if (infos != null) {
                            if (infos.size() > 0) {
                                task_num.setText("待处理任务：" + infos.size());
                                adapter = new TaskInFoAdapter(getActivity(), infos, dialog);
                                addInfosOverlay(infos);
                                xListView.setPullLoadEnable(false);
                                xListView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                xListView.stopRefresh();
                                xListView.stopLoadMore();
                            } else {

                                try {
                                    if (adapter != null) {
                                        adapter.notifyDataSetChanged();
                                    }
                                    infos.clear();
                                    mBaiduMap.clear();
                                    xListView.stopRefresh();
                                    xListView.stopLoadMore();
                                    task_num.setText("待处理任务：无");
                                } catch (Exception e) {

                                }

                            }
                        }
                    }
                    break;
                case 404:
                    dialog.dismiss();
                    ToastUtil.show("超时，请检查网络或者服务器！");
                    xListView.stopRefresh();
                    xListView.stopLoadMore();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragment1, container, false);
        initview(view);

        mOrientationSensor = new OrientationSensor(getActivity());
        mOrientationSensor.start();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                    1);
        } else {
            init(view);
        }
        initlistener();
        return view;
    }    //地图组件初始化   位置信息获取

    private void init(View view) {


        taskhistory = (Button) view.findViewById(R.id.task_history);
        task_num = (TextView) view.findViewById(R.id.task_num);
        home = (Button) view.findViewById(R.id.home);
        distance = (TextView) view.findViewById(R.id.distance);
        // 地图初始化
        mMapView = (TextureMapView) view.findViewById(R.id.bmapView);
        mMapView.showScaleControl(false);
        mBaiduMap = mMapView.getMap();
        mMapView.setLogoPosition(LogoPosition.logoPostionCenterBottom);
        // 开启定位图层
        mBaiduMap.setTrafficEnabled(false);
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化

        mLocClient = MyApplication.getLocClient();

        mMapView.removeViewAt(1);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("GCJ02"); // 设置坐标类型
        option.setScanSpan(10000);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);//地理位置信息
        option.setNeedDeviceDirect(true);//方向
        mLocClient.setLocOption(option);
        mLocClient.registerLocationListener(myListener);
        mLocClient.start();
        if (!mLocClient.isStarted()) {
            mLocClient.registerLocationListener(new BDLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    nowbdLocation = bdLocation;
                    String la = "" + bdLocation.getLatitude();
                    String lo = "" + bdLocation.getLongitude();
                    HttpAPIUtils.HttpLocationPost(getActivity(), MyConstants.LOCATION, la, lo);
                }
            });
        }

        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        mBaiduMap
                .setMyLocationConfigeration(new MyLocationConfiguration(
                        mCurrentMode, true, null));


    }

    private void initlistener() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("uid", SharedPreferencesUtils.getString(getActivity(), "uid"));
        params.addBodyParameter("flag", "0");
        HttpAPIUtils.HttpPost(handler, MyConstants.REPAIRE_GET_TASK, params);
        dialog.show();
        //任务历史
        taskhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestParams params = new RequestParams();
                params.addBodyParameter("uid", SharedPreferencesUtils.getString(getActivity(), "uid"));
                params.addBodyParameter("flag", "1");
                HttpAPIUtils.TaskHistoryHttpPost(handler, MyConstants.REPAIRE_GET_TASK, params);
                dialog.show();
            }
        });

        //列表为空时显示
        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestParams params = new RequestParams();
                params.addBodyParameter("uid", SharedPreferencesUtils.getString(getActivity(), "uid"));
                params.addBodyParameter("flag", "0");
                HttpAPIUtils.HttpPost(handler, MyConstants.REPAIRE_GET_TASK, params);
                dialog.show();
            }
        });
        //选择任务，查看位置信息
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (nowbdLocation != null) {
                    TaskInfo info = infos.get(i - 1);
                    LatLng latt = MyData.getBaiduLatLng(info.getLongItude(), info.getLatItude());
                    MapStatus ms = new MapStatus.Builder().target(latt).zoom(15).build();
                    int m = (int) DistanceUtil.getDistance(new LatLng(nowbdLocation.getLatitude(), nowbdLocation.getLongitude()), latt);
                    distance.setText(m / 1000 + "公里");
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
                } else {
                    ToastUtil.show("定位中......");
                }

            }
        });
        xListView.setXListViewListener(new XListView.IXListViewListener() {

            @Override
            public void onRefresh() {
                // TODO 下拉刷新获取数据
                RequestParams params = new RequestParams();
                params.addBodyParameter("uid", SharedPreferencesUtils.getString(getActivity(), "uid"));
                params.addBodyParameter("flag", "0");
                HttpAPIUtils.HttpPost(handler, MyConstants.REPAIRE_GET_TASK, params);
            }

            @Override
            public void onLoadMore() {
                xListView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        xListView.stopRefresh();
                        xListView.stopLoadMore();
                    }
                }, 1500);
            }

        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapStatus ms;
                ms = new MapStatus.Builder().target(new LatLng(39.9170840000, 116.4039590000)).zoom(12).build();
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
            }
        });
    }

    // 添加标记的方法（传入一个集合）
    public void addInfosOverlay(List<TaskInfo> infos) {
        mBaiduMap.clear();
        OverlayOptions overlayOptions = null;
        Marker marker = null;
        LatLng latt = null;
        for (TaskInfo info : infos) {
            // 得带经纬度
            LayoutInflater factory = LayoutInflater.from(getActivity());
            View textEntryView = factory.inflate(R.layout.overlay_view, null); ////把视图转换成Bitmap 再转换成Drawable
            ImageView imageView = (ImageView) textEntryView.findViewById(R.id.drawimage);
            TextView textView = (TextView) textEntryView.findViewById(R.id.drawtext);
            String id = info.getAppNo();
            textView.setText(id);
            textView.setTextColor(Color.RED);
            imageView.setImageResource(R.mipmap.err);
            textEntryView.setDrawingCacheEnabled(true);
            textEntryView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            textEntryView.layout(0, 0, textEntryView.getMeasuredWidth(),
                    textEntryView.getMeasuredHeight());
            textEntryView.buildDrawingCache();
            Bitmap newbmp = textEntryView.getDrawingCache();

            latt = MyData.getBaiduLatLng(info.getLongItude(), info.getLatItude());

            overlayOptions = new MarkerOptions()
                    .position(latt)
                    .icon(BitmapDescriptorFactory
                            .fromBitmap(newbmp)).zIndex(5);
            marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
            Bundle bundle = new Bundle();
            bundle.putSerializable("info", info);
            marker.setExtraInfo(bundle);
        }
    }

    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {   // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }


            mOrientationSensor.setOrientationListener(new OrientationSensor.OrientationSensorListener() {
                @Override
                public void getOrientation(float x) {
                    driection = x;
                }
            });

            LatLng latLng = MyData.getBaiduLatLng("" + location.getLongitude(), "" + location.getLatitude());
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(0).latitude(latLng.latitude)
                    .longitude(latLng.longitude).build();
            mBaiduMap.setMyLocationData(locData);
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }

    }


    private void initview(View view) {
        dialog = MyProgressDialog.createDialog(getActivity());
        empty = (TextView) view.findViewById(R.id.empty);
        xListView = (XListView) view.findViewById(R.id.xlistview);
        xListView.setEmptyView(empty);
    }

    @Override
    public void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
//取消位置提醒  
        mMapView = null;
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        MyConstants.SetServiceInfo(getActivity());
        RequestParams params = new RequestParams();
        params.addBodyParameter("uid", SharedPreferencesUtils.getString(getActivity(), "uid"));
        params.addBodyParameter("flag", "0");
        HttpAPIUtils.HttpPost(handler, MyConstants.REPAIRE_GET_TASK, params);
        mMapView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        dialog.dismiss();
    }
}
