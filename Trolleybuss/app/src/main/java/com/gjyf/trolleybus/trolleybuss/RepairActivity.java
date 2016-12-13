package com.gjyf.trolleybus.trolleybuss;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.hdhe.uhf.entity.EPC;
import com.android.hdhe.uhf.reader.Tools;
import com.android.hdhe.uhf.reader.UhfReader;
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
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.gjyf.app.citylight.MyApplication;
import com.gjyf.app.citylight.MyConstants;
import com.gjyf.trolleybus.trolleybuss.adapter.TaskInFoAdapter;
import com.gjyf.trolleybus.trolleybuss.bean.BaiduMapServiceBean;
import com.gjyf.trolleybus.trolleybuss.bean.BaidumapInfo;
import com.gjyf.trolleybus.trolleybuss.bean.CityLightInfo;
import com.gjyf.trolleybus.trolleybuss.bean.EPCBean;
import com.gjyf.trolleybus.trolleybuss.bean.TaskInfo;
import com.gjyf.trolleybus.trolleybuss.myview.MyProgressDialog;
import com.gjyf.trolleybus.trolleybuss.myview.MySlidingDrawer;
import com.gjyf.trolleybus.trolleybuss.rfidconnect.OnBtInfoClickListener;
import com.gjyf.trolleybus.trolleybuss.rfidread.ScreenStateReceiver;
import com.gjyf.trolleybus.trolleybuss.rfidread.Util;
import com.gjyf.trolleybus.trolleybuss.utils.EPCUtils;
import com.gjyf.trolleybus.trolleybuss.utils.HttpAPIUtils;
import com.gjyf.trolleybus.trolleybuss.utils.MyData;
import com.gjyf.trolleybus.trolleybuss.utils.OrientationSensor;
import com.gjyf.trolleybus.trolleybuss.utils.PhotoUtils;
import com.gjyf.trolleybus.trolleybuss.utils.SharedPreferencesUtils;
import com.gjyf.trolleybus.trolleybuss.utils.ToastUtil;
import com.lidroid.xutils.http.RequestParams;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 任务抢修
 */
public class RepairActivity extends AppCompatActivity implements View.OnClickListener {
    private BDLocation nowbdLocation;
    private MySlidingDrawer sd;
    private ImageView iv;
    private Button getstreetxy;
    private Button daohang;
    private TextView distance;
    MapView mMapView;
    BaiduMap mBaiduMap;
    LocationClient mLocClient;
    BitmapDescriptor mCurrentMarker;
    InfoWindow mInfoWindow;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    public MyLocationListenner myListener = new MyLocationListenner();
    private OrientationSensor mOrientationSensor;
    private Button cailiao, qiangxiu;
    private float driection = 0;
    boolean isFirstLoc = true; // 是否首次定位
    BaidumapInfo steetinfo = null;
    private TextView totle;
    //導航
    public static List<Activity> activityList = new LinkedList<Activity>();

    private static final String APP_FOLDER_NAME = "BNSDKSimpleDemo";

    private Button mWgsNaviBtn = null;
    private Button mGcjNaviBtn = null;
    private Button mBdmcNaviBtn = null;
    private Button mDb06ll = null;
    private String mSDCardPath = null;
    private MyProgressDialog dialog;

    private TextView guzangdidian, wentimiaoshu, baoxiuren, qiangxiuren, renwubianhao;   //任务信息描述
    private TaskInfo taskInfo;

    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
    public static final String RESET_END_NODE = "resetEndNode";
    public static final String VOID_MODE = "voidMode";

    private ArrayList<BaidumapInfo> baidumapInfoArrayList = new ArrayList<>(); //抢修电杆信息

    private Button buttonConnect;
    private Button buttonStart;
    private ArrayList<EPC> listEPC;
    private ArrayList<Map<String, Object>> listMap;
    private boolean runFlag = true;
    private boolean startFlag = false;
    private boolean connectFlag = false;
    private UhfReader reader; //超高频读写器
    Thread thread = null;
    private ScreenStateReceiver screenReceiver;
    private KeyReceiver keyReceiver;
    private boolean isRunFlag = true;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    EPCBean epcBean = (EPCBean) msg.obj;
                    if (epcBean != null) {
                        startFlag = false;
                        buttonStart.setText("开始");
                        LinearLayout rfidview = (LinearLayout) findViewById(R.id.rfidview);
                        TextView streetname = (TextView) findViewById(R.id.streetname);
                        TextView streetnum = (TextView) findViewById(R.id.streetnum);
                        TextView num = (TextView) findViewById(R.id.num);

                        rfidview.setVisibility(View.VISIBLE);
                        streetname.setText(epcBean.getSteetname());
                        streetnum.setText(epcBean.getStreetid());
                        num.setText(epcBean.getNum());

                        Log.v("yzs", "街道号---" + epcBean.getStreetid());
                        Log.v("yzs", "资产码---" + epcBean.getNum());
                        Log.v("yzs", "道路名---" + epcBean.getSteetname());
//                        taskInfo = (TaskInfo) getIntent().getSerializableExtra("taskinfo");
//                        RequestParams params = new RequestParams();
//                        params.addBodyParameter("uid", SharedPreferencesUtils.getString(RepairActivity.this, "uid"));
//                        params.addBodyParameter("poleId", epcBean.getNum());
//                        HttpAPIUtils.HttpPostAndShow(handler, MyConstants.REPAIRE_GET_ZHICAN, params);
//                        dialog.show();
                    }

                    break;
                case 600:
                    String string = (String) msg.obj;
                    if (!TextUtils.isEmpty(string)) {
                        if (!string.equals("null")) {
                            dialog.dismiss();
                            mBaiduMap.clear();
                            baidumapInfoArrayList.clear();
                            BaiduMapServiceBean serviceBean = (BaiduMapServiceBean) JSON.parseObject(string, BaiduMapServiceBean.class);
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
                            baidumapInfoArrayList.add(info);
                            addInfosOverlay(baidumapInfoArrayList);
                            if (mInfoWindow != null) {
                                if ((System.currentTimeMillis() - exitTime) > 2000) {
                                    mBaiduMap.showInfoWindow(mInfoWindow);
                                    LatLng ll = MyData.getBaiduLatLng(steetinfo.getDimensionLongitude(), steetinfo.getDimensionLatitude());
                                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                                    mBaiduMap.setMapStatus(u);
                                    exitTime = System.currentTimeMillis();
                                } else {
                                }

                            }
                        }

                    }
                    break;
                case 400:
                    String request = (String) msg.obj;
                    if (!TextUtils.isEmpty(request)) {
                        if (!request.equals("null")) {
                            dialog.dismiss();
                            mBaiduMap.clear();
                            baidumapInfoArrayList.clear();
                            BaiduMapServiceBean serviceBean = (BaiduMapServiceBean) JSON.parseObject(request, BaiduMapServiceBean.class);
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
                            baidumapInfoArrayList.add(info);
                            addInfosOverlay(baidumapInfoArrayList);
                        }
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
        activityList.add(this);
        setContentView(R.layout.activity_repair);
        initrfid();
        // 打开log开关
        BNOuterLogUtil.setLogSwitcher(false);
        if (initDirs()) {
            initNavi();
        }
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
        registerReceiver();
    }

    //地图组件初始化   位置信息获取
    private void init() {
        dialog = MyProgressDialog.createDialog(this);
        taskInfo = (TaskInfo) getIntent().getSerializableExtra("taskinfo");
        RequestParams params = new RequestParams();
        params.addBodyParameter("uid", SharedPreferencesUtils.getString(RepairActivity.this, "uid"));
        params.addBodyParameter("poleId", taskInfo.getPoleId());

        HttpAPIUtils.HttpPost(handler, MyConstants.REPAIRE_GET_ZHICAN, params);
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessageDelayed(1, 1500);
            }
        }).start();
        renwubianhao = (TextView) findViewById(R.id.renwubianhao);
        guzangdidian = (TextView) findViewById(R.id.guzangdidian);
        wentimiaoshu = (TextView) findViewById(R.id.wentimiaoshu);
        baoxiuren = (TextView) findViewById(R.id.baoxiuren);
        qiangxiuren = (TextView) findViewById(R.id.qiangxiuren);
        //任務信息初始化显示
        renwubianhao.setText(taskInfo.getAppNo());
        guzangdidian.setText(taskInfo.getDistrictTown() + "," + taskInfo.getStreetNo() + "," + taskInfo.getDefailedAdderss());
        wentimiaoshu.setText(taskInfo.getExcpDesc());
        baoxiuren.setText(taskInfo.getReportName());
        qiangxiuren.setText(taskInfo.getHandleEmpNo());


        totle = (TextView) findViewById(R.id.distance);
        distance = (TextView) findViewById(R.id.distance);
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.showScaleControl(false);


        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(17).build()));//设置缩放级别
        mMapView.setLogoPosition(LogoPosition.logoPostionCenterBottom);
        // 开启定位图层

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMapView.setZoomControlsPosition(new Point(MyApplication.width * 1 / 11, MyApplication.height * 4 / 9));
            }
        });

        mBaiduMap.setTrafficEnabled(true);
        mBaiduMap.setCompassPosition(new Point(50, 50)); //左上角罗盘位置


        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(MyApplication.getContext());
        mLocClient.registerLocationListener(myListener);
        mMapView.removeViewAt(1);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("GCJ02"); // 设置坐标类型
        option.setScanSpan(10000);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);//地理位置信息
        option.setNeedDeviceDirect(true);//方向


        if (!mLocClient.isStarted()) {
            mLocClient.setLocOption(option);
            mLocClient.registerLocationListener(myListener);
            mLocClient.start();
            mLocClient.registerLocationListener(new BDLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    nowbdLocation = bdLocation;
                    if (!TextUtils.isEmpty(bdLocation.getAddrStr())) {
                        totle.setText(bdLocation.getAddrStr());
                    } else {
                        totle.setText("未获取道路名称请检查网络连接 ");
                    }
                }
            });
        }


        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.mipmap.err);
        mBaiduMap
                .setMyLocationConfigeration(new MyLocationConfiguration(
                        mCurrentMode, true, null));


        // mask的点击事件
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                // TODO Auto-generated method stub
                BaidumapInfo info = (BaidumapInfo) arg0.getExtraInfo().get(
                        "info");
                View view = LayoutInflater.from(RepairActivity.this).inflate(
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
                mInfoWindow = new InfoWindow(BitmapDescriptorFactory
                        .fromView(view), ll, -100, listener);
                mBaiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });

    }

    private void initlistener() {
        getstreetxy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskInfo = (TaskInfo) getIntent().getSerializableExtra("taskinfo");
                RequestParams params = new RequestParams();
                params.addBodyParameter("uid", SharedPreferencesUtils.getString(RepairActivity.this, "uid"));
                params.addBodyParameter("poleId", taskInfo.getPoleId());
                HttpAPIUtils.HttpPost(handler, MyConstants.REPAIRE_GET_ZHICAN, params);
                dialog.show();
            }
        });

        daohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nowbdLocation != null && taskInfo != null) {
                    dialog.show();
                    if (BaiduNaviManager.isNaviInited()) {
                        routeplanToNavi(BNRoutePlanNode.CoordinateType.GCJ02, "" + nowbdLocation.getLongitude(), "" + nowbdLocation.getLatitude(), nowbdLocation.getAddrStr(), taskInfo.getLongItude(), taskInfo.getLatItude(), taskInfo.getDistrictTown() + taskInfo.getStreetNo() + taskInfo.getDefailedAdderss());
                    }
                } else {
                    ToastUtil.show("定位中···请稍等");
                }


            }
        });
        //材料添加
        cailiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RepairActivity.this, CailiaoActivity.class);
                intent.putExtra("taskinfo", taskInfo);
                startActivity(intent);
            }
        });

        //工程抢修
        qiangxiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nowbdLocation != null) {
                    Intent intent = new Intent(RepairActivity.this, QiangXActivity.class);

                    if (baidumapInfoArrayList.size() > 0) {
                        intent.putExtra("taskinfo", taskInfo);
                        intent.putExtra("baidumapinfo", baidumapInfoArrayList.get(0));
                        intent.putExtra("la", nowbdLocation.getLatitude() + "");
                        intent.putExtra("lo", nowbdLocation.getLongitude() + "");
                        startActivityForResult(intent, 100);
                    } else {
                        ToastUtil.show("未获取资产信息");
                    }

                } else {
                    ToastUtil.show("定位中···");
                }


            }
        });

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


    // 添加标记的方法（传入一个集合）
    public void addInfosOverlay(List<BaidumapInfo> infos) {
        mBaiduMap.clear();
        OverlayOptions overlayOptions = null;
        Marker marker = null;
        LatLng latt = null;
        for (BaidumapInfo info : infos) {
            // 得带经纬度
            steetinfo = info;
            latt = MyData.getBaiduLatLng(info.getDimensionLongitude(), info.getDimensionLatitude());
            LayoutInflater factory = LayoutInflater.from(RepairActivity.this);
            View textEntryView = factory.inflate(R.layout.overlay_view, null); ////把视图转换成Bitmap 再转换成Drawable
            ImageView imageView = (ImageView) textEntryView.findViewById(R.id.drawimage);
            TextView textView = (TextView) textEntryView.findViewById(R.id.drawtext);
            String id = info.getDeviceID();
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

            overlayOptions = new MarkerOptions()
                    .position(latt)
                    .icon(BitmapDescriptorFactory
                            .fromBitmap(newbmp)).zIndex(5);
            marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
            Bundle bundle = new Bundle();
            bundle.putSerializable("info", info);
            marker.setExtraInfo(bundle);

            View view = LayoutInflater.from(RepairActivity.this).inflate(
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
            mInfoWindow = new InfoWindow(BitmapDescriptorFactory
                    .fromView(view), ll, -100, listener);
        }
        MapStatus ms = new MapStatus.Builder().target(latt).zoom(15).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));


    }


    private void initview() {
        getstreetxy = (Button) findViewById(R.id.street);
        daohang = (Button) findViewById(R.id.daohang);
        qiangxiu = (Button) findViewById(R.id.qiangxiu);
        cailiao = (Button) findViewById(R.id.cailiao);
        sd = (MySlidingDrawer) findViewById(R.id.sliding);
//        iv = (ImageView) findViewById(R.id.imageViewIcon);
//        iv.setImageResource(R.drawable.arr_down);// 响应开抽屉事件
        sd.open();

        sd.setTouchableIds(new int[]{R.id.handle, R.id.daohang});
        ImageView back = (ImageView) findViewById(R.id.back);
        TextView top_text = (TextView) findViewById(R.id.main_top_text);
        top_text.setText("设备维修");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    ToastUtil.show("再按一次退出该次任务！"); // 居中显示
                    exitTime = System.currentTimeMillis();
                } else {
                    RepairActivity.this.finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        startFlag = false;
        if (thread != null) {
            thread.interrupt();
        }
        unregisterReceiver();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        dialog.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

//------------------------------百度导航------------------------------------//

    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    String authinfo = null;

    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    //showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    //showToastMsg("Handler : TTS play end");
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 内部TTS播报状态回调接口
     */
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {

        }

        @Override
        public void playStart() {

        }
    };

    public void showToastMsg(final String msg) {
        RepairActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(RepairActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initNavi() {

        BNOuterTTSPlayerCallback ttsCallback = null;

        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
                RepairActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //    Toast.makeText(RepairActivity.this, authinfo, Toast.LENGTH_LONG).show();
                    }
                });
            }

            public void initSuccess() {
                totle.setText("导航引擎初始化成功");
                initSetting();
            }

            public void initStart() {
                totle.setText("导航引擎初始化开始");
            }

            public void initFailed() {
                Toast.makeText(RepairActivity.this, "导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }


        }, null, ttsHandler, ttsPlayStateListener);

    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private void routeplanToNavi(BNRoutePlanNode.CoordinateType coType, String slo, String sla, String sname, String elo, String ela, String ename) {
        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;
        switch (coType) {
//            case GCJ02: {
//                sNode = new BNRoutePlanNode(116.2311448500, 40.2073778347, "!!", null, coType);
//                eNode = new BNRoutePlanNode(116.2281658044, 40.1994760580, "@@", null, coType);
//                break;
//            }
            case GCJ02: {
                sNode = new BNRoutePlanNode(Double.parseDouble(slo), Double.parseDouble(sla), sname, null, coType);
                eNode = new BNRoutePlanNode(Double.parseDouble(elo), Double.parseDouble(ela), ename, null, coType);
                break;
            }
//            case BD09_MC: {
//                sNode = new BNRoutePlanNode(12947471, 4846474, "百度大厦", null, coType);
//                eNode = new BNRoutePlanNode(12958160, 4825947, "北京天安门", null, coType);
//                break;
//            }
            case BD09LL: {
                sNode = new BNRoutePlanNode(Double.parseDouble(slo), Double.parseDouble(sla), sname, null, coType);
                eNode = new BNRoutePlanNode(Double.parseDouble(elo), Double.parseDouble(ela), ename, null, coType);

                break;
            }
            default:
                ;
        }
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode)); //導航開始
        }
    }

    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
            /*
             * 设置途径点以及resetEndNode会回调该接口
			 */

            for (Activity ac : activityList) {

                if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) {

                    return;
                }
            }
            Intent intent = new Intent(RepairActivity.this, BNDemoGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);

        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(RepairActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void initSetting() {
        // 设置是否双屏显示
        BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        // 设置导航播报模式
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // 是否开启路况
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
    }

    private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {

        @Override
        public void stopTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "stopTTS");
        }

        @Override
        public void resumeTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "resumeTTS");
        }

        @Override
        public void releaseTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "releaseTTSPlayer");
        }

        @Override
        public int playTTSText(String speech, int bPreempt) {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "playTTSText" + "_" + speech + "_" + bPreempt);

            return 1;
        }

        @Override
        public void phoneHangUp() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneHangUp");
        }

        @Override
        public void phoneCalling() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneCalling");
        }

        @Override
        public void pauseTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "pauseTTS");
        }

        @Override
        public void initTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "initTTSPlayer");
        }

        @Override
        public int getTTSState() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "getTTSState");
            return 1;
        }
    };

    private long exitTime = 0;


    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.show("再按一次退出该次任务！"); // 居中显示
            exitTime = System.currentTimeMillis();
        } else {
            this.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("res", requestCode + "-------" + resultCode + data);
        if (requestCode == 100) {
            switch (resultCode) {
                case 1:
                    RepairActivity.this.finish();
                    break;
                case 2:
                    break;
            }
        }
    }


    /**
     * 盘存线程
     *
     * @author Administrator
     */
    class InventoryThread extends Thread {
        private List<byte[]> epcList;

        @Override
        public void run() {
            super.run();
            while (runFlag) {
                if (startFlag) {
//					reader.stopInventoryMulti()
                    epcList = reader.inventoryRealTime(); //实时盘存
                    if (epcList != null && !epcList.isEmpty()) {
                        //播放提示音
                        Util.play(1, 0);
                        for (byte[] epc : epcList) {
                            String epcStr = Tools.Bytes2HexString(epc, epc.length);
                            if (epc.length > 15) {
                                EPCBean bean = EPCUtils.GetEpcBean(epcStr);
                                if (bean != null) {
                                    if (!TextUtils.isEmpty(bean.getNum()) && !TextUtils.isEmpty(bean.getSteetname()) && !TextUtils.isEmpty(bean.getStreetid())) {
                                        if (MyData.GetChinesenum(bean.getSteetname()) != 0 && bean.getSteetname().length() <= 8) {
                                            Message message = Message.obtain();
                                            message.what = 0;
                                            message.obj = bean;
                                            handler.sendMessage(message);
                                        }
                                    }
                                }
                            }


                        }
                    }
                    epcList = null;
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private class KeyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (isRunFlag) {
                int keyCode = intent.getIntExtra("keyCode", 0);
                if (keyCode == 0) {
                    keyCode = intent.getIntExtra("keycode", 0);
                }
                boolean keyDown = intent.getBooleanExtra("keydown", false);
                if (keyDown) {

                    switch (keyCode) {
                        case KeyEvent.KEYCODE_F1:
                            //f1
                            //材料添加

                            Intent intent1 = new Intent(RepairActivity.this, CailiaoActivity.class);
                            intent1.putExtra("taskinfo", taskInfo);
                            startActivity(intent1);

                            break;
                        case KeyEvent.KEYCODE_F2:
                            //f2
                            //工程抢修
                            Intent intent2 = new Intent(RepairActivity.this, QiangXActivity.class);

                            if (baidumapInfoArrayList.size() > 0) {
                                intent2.putExtra("taskinfo", taskInfo);
                                intent2.putExtra("baidumapinfo", baidumapInfoArrayList.get(0));
                                intent2.putExtra("la", nowbdLocation.getLatitude() + "");
                                intent2.putExtra("lo", nowbdLocation.getLongitude() + "");
                                startActivityForResult(intent2, 100);
                            } else {
                                ToastUtil.show("未获取资产信息");
                            }
                            break;
                        case KeyEvent.KEYCODE_F3:
                            //左边的
                            if (!startFlag) {
                                startFlag = true;
                                buttonStart.setText("停止");
                            } else {
                                startFlag = false;
                                buttonStart.setText("开始");
                            }
                            break;
                        case KeyEvent.KEYCODE_F4:
                            ToastUtil.show("KEYCODE_F4");
                            break;
                        case KeyEvent.KEYCODE_F5:
                            // 右边的
                            if (!startFlag) {
                                startFlag = true;
                                buttonStart.setText("停止");
                            } else {
                                startFlag = false;
                                buttonStart.setText("开始");
                            }
                            break;
                    }
                }

            }
        }
    }


    private void registerReceiver() {
        keyReceiver = new KeyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.rfid.FUN_KEY");
        registerReceiver(keyReceiver, filter);
    }

    private void unregisterReceiver() {
        unregisterReceiver(keyReceiver);
    }

    //设置按钮是否可用
    private void setButtonClickable(Button button, boolean flag) {
        button.setClickable(flag);
        if (flag) {
            button.setTextColor(Color.BLACK);
        } else {
            button.setTextColor(Color.GRAY);
        }
    }

    private void initrfid() {
        buttonStart = (Button) findViewById(R.id.button_start);
        buttonConnect = (Button) findViewById(R.id.button_connect);
        listEPC = new ArrayList<EPC>();
        buttonStart.setOnClickListener(this);
        buttonConnect.setOnClickListener(this);
        //获取读写器实例，若返回Null,则串口初始化失败
        reader = UhfReader.getInstance();
        if (reader == null) {

            setButtonClickable(buttonStart, false);
            setButtonClickable(buttonConnect, false);
            return;
        }

        //获取用户设置功率,并设置
        SharedPreferences shared = getSharedPreferences("power", 0);
        int value = shared.getInt("value", 26);
        Log.e("", "value" + value);
        reader.setOutputPower(value);


        //添加广播，默认屏灭时休眠，屏亮时唤醒
        screenReceiver = new ScreenStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenReceiver, filter);

        thread = new InventoryThread();
        thread.start();
        //初始化声音池
        Util.initSoundPool(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start:
                if (!startFlag) {
                    startFlag = true;
                    buttonStart.setText("停止");
                } else {
                    startFlag = false;
                    buttonStart.setText("开始");
                }
                break;
            case R.id.button_connect:

                byte[] versionBytes = reader.getFirmware();
                if (versionBytes != null) {
                    Util.play(10, 0);
                    String version = new String(versionBytes);
                    setButtonClickable(buttonConnect, false);
                    setButtonClickable(buttonStart, true);
                }
                setButtonClickable(buttonConnect, false);
                setButtonClickable(buttonStart, true);
                break;

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isRunFlag = true;
    }

    @Override
    protected void onStop() {
        isRunFlag = false;
        super.onStop();
    }
}
