package com.gjyf.trolleybus.trolleybuss;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.clusterutil.clustering.Cluster;
import com.baidu.mapapi.clusterutil.clustering.ClusterItem;
import com.baidu.mapapi.clusterutil.clustering.ClusterManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.gjyf.app.citylight.MyApplication;
import com.gjyf.app.citylight.MyConstants;
import com.gjyf.trolleybus.trolleybuss.bean.BaiduMapServiceBean;
import com.gjyf.trolleybus.trolleybuss.bean.BaidumapInfo;
import com.gjyf.trolleybus.trolleybuss.myview.MyProgressDialog;
import com.gjyf.trolleybus.trolleybuss.utils.HttpAPIUtils;
import com.gjyf.trolleybus.trolleybuss.utils.MyData;
import com.gjyf.trolleybus.trolleybuss.utils.OrientationSensor;
import com.gjyf.trolleybus.trolleybuss.utils.SharedPreferencesUtils;
import com.gjyf.trolleybus.trolleybuss.utils.ToastUtil;
import com.lidroid.xutils.http.RequestParams;


import java.util.ArrayList;
import java.util.List;

/**
 * 资产信息显示
 */
public class BaiduMapActivity extends AppCompatActivity implements BaiduMap.OnMapLoadedCallback {
    MapView mMapView;
    BaiduMap mBaiduMap;
    LocationClient mLocClient;
    BitmapDescriptor mCurrentMarker;
    InfoWindow mInfoWindow;
    private ImageView addmark;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private LinearLayout search, ll_title_left;
    private AutoCompleteTextView streetnameedit;
    public MyLocationListenner myListener = new MyLocationListenner();
    private OrientationSensor mOrientationSensor;
    private float driection = 0;
    boolean isFirstLoc = true; // 是否首次定位
    private ClusterManager<MyItem> mClusterManager;
    private MyProgressDialog dialog;
    private List<BaidumapInfo> baidumapInfos = new ArrayList<>();
    MapStatus ms;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ms = new MapStatus.Builder().target(new LatLng(Double.parseDouble(baidumapInfos.get(0).getDimensionLatitude()), Double.parseDouble(baidumapInfos.get(0).getDimensionLongitude()))).zoom(15).build();
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
                    break;
                case 400:
                    try {
                        String request = (String) msg.obj;
                        baidumapInfos.clear();
                        mBaiduMap.clear();
                        if (!TextUtils.isEmpty(request)) {

                            ArrayList<BaiduMapServiceBean> serviceBeans = (ArrayList<BaiduMapServiceBean>) JSON.parseArray(request, BaiduMapServiceBean.class);
                            if (serviceBeans != null) {
                                if (serviceBeans.size() > 0) {

                                    for (BaiduMapServiceBean serviceBean : serviceBeans) {
                                        BaidumapInfo info = new BaidumapInfo();
                                        info.setDate(serviceBean.getWinRing());
                                        info.setOtherDevice(serviceBean.getFsuspensionType());
                                        info.setStreetname(serviceBean.getStreetName());
                                        info.setDeviceID(serviceBean.getBarCode());
                                        info.setDeviceName(serviceBean.getMpSectId());
                                        info.setType(serviceBean.getPoleType());
                                        info.setNowNo(serviceBean.getBarCodeOle());
                                        info.setCreatetime(serviceBean.getInstDate());
                                        info.setUsertime(serviceBean.getUseLife());
                                        info.setDimensionLongitude(serviceBean.getLongItude());
                                        info.setDimensionLatitude(serviceBean.getLatItude());
                                        baidumapInfos.add(info);
                                    }   // 添加Marker点
                                    addMarkers(baidumapInfos);
                                    dialog.dismiss();
                                    ms = new MapStatus.Builder().target(new LatLng(Double.parseDouble(baidumapInfos.get(0).getDimensionLatitude()), Double.parseDouble(baidumapInfos.get(0).getDimensionLongitude()) + 0.000001)).zoom(15).build();
                                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
                                    handler.sendEmptyMessageDelayed(0, 500);

                                }
                            }
                        }
                    } catch (Exception e) {

                    }

                    break;
                case 404:
                    ToastUtil.show("超时，请检查网络或者服务器！");
                    dialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_map);
        mOrientationSensor = new OrientationSensor(this);
        mOrientationSensor.start();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                    1);
        } else {
            init();
        }


        initview();
        initlistener();
    }

    private void initview() {
        ll_title_left = (LinearLayout) findViewById(R.id.ll_title_left);
        addmark = (ImageView) findViewById(R.id.addmark);
        dialog = MyProgressDialog.createDialog(this);
        streetnameedit = (AutoCompleteTextView) findViewById(R.id.input_info);
        search = (LinearLayout) findViewById(R.id.search);
    }

    private void initlistener() {

        ll_title_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaiduMapActivity.this.finish();
            }
        });

        addmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        //街道名选择框    输入首字母选择合适信息
        streetnameedit.setThreshold(1);
        streetnameedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String streetjson = null;

                try {
                    streetjson = SharedPreferencesUtils.getString(BaiduMapActivity.this, "streetname");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(streetjson)) {
                    String[] temp = streetjson.split(",");
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(BaiduMapActivity.this,
                            R.layout.street_name, temp);
                    streetnameedit.setAdapter(adapter);
                }

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editinfo = streetnameedit.getText().toString().trim();
                if (!TextUtils.isEmpty(editinfo)) {
                    ms = new MapStatus.Builder().target(new LatLng(39.9170840000, 116.4039590000)).zoom(12).build();
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
                    int s = MyData.GetChinesenum(editinfo);
                    if (s != 0) {
                        if (s == editinfo.length()) {
                            RequestParams params = new RequestParams();
                            params.addBodyParameter("streetName", editinfo);
                            params.addBodyParameter("barCode", "");
                            HttpAPIUtils.HttpPost(handler, MyConstants.ZHiCAN, params);
                            dialog.show();
                        } else {
                            String[] strings = editinfo.split("-");
                            if (strings.length == 2) {
                                RequestParams params = new RequestParams();
                                params.addBodyParameter("streetName", strings[0]);
                                params.addBodyParameter("barCode", strings[1]);
                                HttpAPIUtils.HttpPost(handler, MyConstants.ZHiCAN, params);
                                dialog.show();
                            }

                        }

                    } else {
                        RequestParams params = new RequestParams();
                        params.addBodyParameter("streetName", "");
                        params.addBodyParameter("barCode", editinfo);
                        HttpAPIUtils.HttpPost(handler, MyConstants.ZHiCAN, params);
                        dialog.show();
                    }


                }
            }
        });
    }

    //地图组件初始化
    private void init() {
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.showScaleControl(false);
        mBaiduMap = mMapView.getMap();
        mMapView.setLogoPosition(LogoPosition.logoPostionCenterBottom);
        // 开启定位图层
        mBaiduMap.setCompassPosition(new android.graphics.Point(50, 50)); //左上角罗盘位置
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.showInfoWindow(mInfoWindow);
        //     mBaiduMap.setTrafficEnabled(true);

        // 定位初始化
        mLocClient = new LocationClient(MyApplication.getContext());
        mLocClient.registerLocationListener(myListener);
        mMapView.removeViewAt(1);
        LocationClientOption option = new LocationClientOption();

        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);//地理位置信息
        option.setNeedDeviceDirect(true);//方向

        mLocClient.setLocOption(option);
        mLocClient.start();
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

        mBaiduMap.setOnMapLoadedCallback(this);
        // 定义点聚合管理类ClusterManager
        mClusterManager = new ClusterManager<MyItem>(this, mBaiduMap);
        // 设置地图监听，当地图状态发生改变时，进行点聚合运算
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        mBaiduMap.setOnMarkerClickListener(mClusterManager);
        mBaiduMap
                .setMyLocationConfigeration(new MyLocationConfiguration(
                        mCurrentMode, true, null));

        mLocClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {


            }
        });


        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyItem> cluster) {
//                BaidumapInfo[] baidumapInfos = (BaidumapInfo[]) cluster.getItems().toArray();
//                for (int i = 0; i < baidumapInfos.length; i++) {
//                    Log.v("yangzssss", baidumapInfos[i].getDeviceID());
//                }
                return false;
            }
        });
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem item) {

                BaidumapInfo info = item.getBaidumapInfo();
                View view = LayoutInflater.from(BaiduMapActivity.this).inflate(
                        R.layout.layout_baidumap_detail, null);
                TextView DeviceID = (TextView) view.findViewById(R.id.DeviceID);  // 编号
                TextView DeviceName = (TextView) view.findViewById(R.id.DeviceName); // 设备名称
                TextView Type = (TextView) view.findViewById(R.id.Type); // 规格类型
                TextView NowNo = (TextView) view.findViewById(R.id.NowNo);  // 现有编号
                TextView usertime = (TextView) view.findViewById(R.id.usertime); // 使用年限
                TextView Streetname = (TextView) view.findViewById(R.id.Streetname);  //位置
                TextView createtime = (TextView) view.findViewById(R.id.createtime);   //创建时间
                TextView OtherDevice = (TextView) view.findViewById(R.id.OtherDevice); // 其他设备类型
                TextView Date = (TextView) view.findViewById(R.id.Date);  //备注信息

                DeviceID.setText(info.getDeviceID());
                DeviceName.setText(info.getDeviceName());
                Type.setText(info.getType());
                NowNo.setText(info.getNowNo());
                usertime.setText(info.getUsertime());
                Streetname.setText(info.getStreetname());
                createtime.setText(info.getCreatetime());
                OtherDevice.setText(info.getOtherDevice());
                Date.setText(info.getDate());
                LatLng ll = MyData.getBaiduLatLng(info.getDimensionLongitude(), info.getDimensionLatitude());
                InfoWindow.OnInfoWindowClickListener listener = null;
                listener = new InfoWindow.OnInfoWindowClickListener() {
                    public void onInfoWindowClick() {
                        mBaiduMap.hideInfoWindow();
                    }
                };
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.setMapStatus(u);
                mInfoWindow = new InfoWindow(BitmapDescriptorFactory
                        .fromView(view), ll, -100, listener);
                mBaiduMap.showInfoWindow(mInfoWindow);
                return false;
            }
        });

    }

    /**
     * 向地图添加Marker点
     */
    public void addMarkers(List<BaidumapInfo> list) {
        List<MyItem> items = new ArrayList<MyItem>();
        mClusterManager.clearItems();
        for (BaidumapInfo info : list) {
            items.add(new MyItem(info));
        }
        mClusterManager.addItems(items);
    }

    @Override
    public void onMapLoaded() {
        ms = new MapStatus.Builder().zoom(10).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
    }

    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    /**
     * 每个Marker点，包含Marker点坐标以及图标
     */
    public class MyItem implements ClusterItem {
        private final LatLng mPosition;
        private BaidumapInfo baidumapInfo;

        public MyItem(BaidumapInfo baidumapInfo) {
            mPosition = MyData.getBaiduLatLng(baidumapInfo.getDimensionLongitude(), baidumapInfo.getDimensionLatitude());
            this.baidumapInfo = baidumapInfo;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        public BaidumapInfo getBaidumapInfo() {
            return baidumapInfo;
        }

        @Override
        public BitmapDescriptor getBitmapDescriptor() {
            LayoutInflater factory = LayoutInflater.from(BaiduMapActivity.this);
            View textEntryView = factory.inflate(R.layout.overlay_view, null); ////把视图转换成Bitmap 再转换成Drawable
            ImageView imageView = (ImageView) textEntryView.findViewById(R.id.drawimage);
            TextView textView = (TextView) textEntryView.findViewById(R.id.drawtext);
            String id = baidumapInfo.getDeviceID();
            textView.setText(id);
            textView.setTextColor(Color.parseColor("#ffb400"));
            imageView.setImageResource(R.mipmap.map_marker);
            textEntryView.setDrawingCacheEnabled(true);
            textEntryView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            textEntryView.layout(0, 0, textEntryView.getMeasuredWidth(),
                    textEntryView.getMeasuredHeight());
            textEntryView.buildDrawingCache();
            Bitmap newbmp = textEntryView.getDrawingCache();
            return BitmapDescriptorFactory
                    .fromBitmap(newbmp);
        }

    }


}
