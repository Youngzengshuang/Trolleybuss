package com.gjyf.trolleybus.trolleybuss;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.gjyf.app.citylight.MyApplication;
import com.gjyf.app.citylight.MyConstants;
import com.gjyf.trolleybus.trolleybuss.fragment.Fragment1;
import com.gjyf.trolleybus.trolleybuss.fragment.Fragment2;
import com.gjyf.trolleybus.trolleybuss.fragment.Fragment3;
import com.gjyf.trolleybus.trolleybuss.rfidconnect.OnBtConnectClickListener;
import com.gjyf.trolleybus.trolleybuss.rfidconnect.OnBtInfoClickListener;
import com.gjyf.trolleybus.trolleybuss.utils.HttpAPIUtils;
import com.gjyf.trolleybus.trolleybuss.utils.ToastUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener, OnBtConnectClickListener {
    private SlidingMenu slidingMenu;   //侧滑菜单栏
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private View fragmentview1, fragmentview2, fragmentview3;
    private Fragment fragment1;
    private Fragment fragment2;
    private Fragment fragment3;
    private ViewPager mViewPager;
    private long exitTime = 0;
    private FragmentPagerAdapter mFragmentAdapter;
    // 頂部按钮
    private LinearLayout linearLayout1;
    private LinearLayout linearLayout2;
    private LinearLayout linearLayout3;
    private TextView weather;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageButton topmenu, jiaojiebt;
    private TextView title;
    private TextView bttext;
    private ImageView btloading;
    // 离线状态检测
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    bttext.setText("连接中···");
                    break;
                case 2:
                    bttext.setText("");
                    break;
                case 500:
                    if (!TextUtils.isEmpty(msg.obj.toString())) {
                        String s = parseJSONObjectOrJSONArray(msg.obj.toString());
                        weather.setVisibility(View.VISIBLE);
                        weather.setText(s);
                    } else {
                        weather.setVisibility(View.GONE);
                    }

                    break;
                case 4:
                    weather.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApplication.setBtconncetListener(this);
        initView();
        initSlidingMenu();
        initPage();
        initlistener();

        String url = "http://api.map.baidu.com/telematics/v3/weather?location=北京&output=json&ak=fTd6UAMa58Nc9PGPYpGbOeG3D0z87PQ4&mcode=E4:AB:FC:AB:11:6A:8F:CA:11:1B:48:E9:79:B8:28:6D:4B:EB:10:18;com.gjyf.trolleybus.trolleybuss";
        HttpAPIUtils.HttpGet(handler, url);
    }


    //控件获取
    private void initView() {
        jiaojiebt = (ImageButton) findViewById(R.id.jiaojiebt);
        fragmentview1 = findViewById(R.id.fragmentview1);
        fragmentview2 = findViewById(R.id.fragmentview2);
        fragmentview3 = findViewById(R.id.fragmentview3);
        linearLayout1 = (LinearLayout) findViewById(R.id.ll_mainbottom_user_info);
        linearLayout2 = (LinearLayout) findViewById(R.id.ll_mainbottom_plan);
        linearLayout3 = (LinearLayout) findViewById(R.id.ll_mainbottom_task);
        topmenu = (ImageButton) findViewById(R.id.top_menu);
        textView1 = (TextView) findViewById(R.id.tv_mainbotton_user);
        textView2 = (TextView) findViewById(R.id.tv_mainbotton_plan);
        textView3 = (TextView) findViewById(R.id.tv_mainbotton_task);
        title = (TextView) findViewById(R.id.title);
        imageView1 = (ImageView) findViewById(R.id.iv_mainbotton_user);
        imageView2 = (ImageView) findViewById(R.id.iv_mainbotton_plan);
        imageView3 = (ImageView) findViewById(R.id.iv_mainbotton_task);
        mViewPager = (ViewPager) findViewById(R.id.vp_main);

    }

    //滑动页设置
    private void initPage() {

        // 获取子页面
        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        mFragments.add(fragment1);
        mFragments.add(fragment2);
        mFragments.add(fragment3);

        linearLayout1.setOnClickListener(this);
        linearLayout2.setOnClickListener(this);
        linearLayout3.setOnClickListener(this);
        // 子页面加载
        mFragmentAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments.get(arg0);
            }
        };

        mViewPager.setAdapter(mFragmentAdapter);


        // 设置title
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                int currentItem = mViewPager.getCurrentItem();
                setTab(currentItem);
//                switch (arg0) {
//                    case 0:
//                        title.setText("任务");
//                        break;
//                    case 1:
//                        title.setText("库房");
//                        break;
//                    case 2:
//                        title.setText("资产");
//                        break;
//                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        setSelect(0);
    }

    //监听事件
    private void initlistener() {
        //点击显示侧滑栏
        topmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingMenu.showMenu();
            }
        });
        jiaojiebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, JiaoJieActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initSlidingMenu() {
        // 侧滑的相关设置
        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT); // 设置滑动模式
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_slidingmenu, null);
        weather = (TextView) view.findViewById(R.id.weather);
        ImageButton setting = (ImageButton) view.findViewById(R.id.setting);
        ImageButton offline = (ImageButton) view.findViewById(R.id.offline);
        btloading = (ImageView) view.findViewById(R.id.bt_loading);
        bttext = (TextView) view.findViewById(R.id.bt_connecton_info);
        final ToggleButton btconnect = (ToggleButton) view.findViewById(R.id.lanya);
        ImageButton exit = (ImageButton) view.findViewById(R.id.exit);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.lanya:
                        if (btconnect.isChecked()) {
                            bttext.setText("设备搜索中····");
                            // ```````````````````````````蓝牙连接初始化`````````````````````````
                            if (MyApplication.getR900Manager().isBluetoothEnabled() == false) {
                                MyApplication.getR900Manager().mBluetoothAdapter.enable();
                            }
                            MyApplication.getR900Manager().startDiscovery();
                            // ``````````````````````````````````````````````
                            handler.sendEmptyMessage(200);
                            btloading.setVisibility(View.VISIBLE);
                            MyApplication.getR900Manager().disconnect2();

                            IntentFilter filter = new IntentFilter(
                                    BluetoothDevice.ACTION_FOUND);
                            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                            registerReceiver(mReceiver, filter);

                        } else {
                            bttext.setText("");
                            btloading.setVisibility(View.INVISIBLE);
                            unregisterReceiverSafe();
                            MyApplication.getR900Manager().disconnect2();
                            MyApplication.getR900Manager().stopDiscovery();
                        }
                        break;
                    case R.id.setting:
                        Intent intentset = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intentset);
                        break;
                    case R.id.offline:
                        Intent intent = new Intent(MainActivity.this, OfflineBaiduMap.class);
                        startActivity(intent);
                        break;
                    case R.id.exit:
                        MainActivity.this.finish();
                        break;
                }
            }
        };
        btconnect.setOnClickListener(listener);
        setting.setOnClickListener(listener);
        offline.setOnClickListener(listener);
        exit.setOnClickListener(listener);
//        bttext = (TextView) view.findViewById(R.id.bt_connecton_info);
//        btloading = (Switch) view.findViewById(R.id.lanya);
//        exit = (Button) view.findViewById(R.id.exit);
//        Bt_loading_image = (ImageView) view.findViewById(R.id.bt_loading_image);
//        mLevelDrawable = new LoadingDrawable(new GuardLoadingRenderer(this));
//        Bt_loading_image.setImageDrawable(mLevelDrawable);
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        slidingMenu.setMenu(view);
        slidingMenu.setBehindWidth(width * 2 / 3);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);

        /**
         * 侧滑栏滑动监听事件
         */
//        slidingMenu.setOnOpenListener(new SlidingMenu.OnOpenListener() {
//            @Override
//            public void onOpen() {
//                getslid.setImageResource(R.drawable.close);
//            }
//        });
//        slidingMenu.setOnCloseListener(new SlidingMenu.OnCloseListener() {
//            @Override
//            public void onClose() {
//                getslid.setImageResource(R.drawable.open);
//            }
//        });

    }

    public void unregisterReceiverSafe() {

        try {

            this.unregisterReceiver(mReceiver);

        } catch (IllegalArgumentException e) {

            Log.v("1234", "未启动广播");

        }

    }

    // 蓝牙搜索广播
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String devicename = "";
                try {
                    devicename = device.getName().substring(0, 4);
                } catch (Exception e) {
                    devicename = device.getName();
                }
                Log.v("1234", "搜索到====>" + device.getAddress() + "----" + devicename);
                if (devicename.equals(MyConstants.DEVICENAME)) {
                    MyApplication.getR900Manager().stopDiscovery();
                    Log.v("1234", "尝试连接====>" + device.getAddress() + "====   " + device.getName());
                    handler.sendEmptyMessage(1);
                    MyApplication.getR900Manager().connectToBluetoothDevice2(
                            device, MyConstants.MY_UUID);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                MyApplication.getR900Manager().stopDiscovery();

            } else {
                Log.d("1234", "[Bluetooth] Other event : " + action);
            }
        }

    };

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.show("再按一次返回键退出登陆"); // 居中显示
            exitTime = System.currentTimeMillis();
        } else {
            unregisterReceiverSafe();

            MyApplication.getR900Manager().setConnectionStatus(false);
            MyApplication.getR900Manager().disconnect2();
            this.finish();
        }
    }

    private void setTab(int i) {
        resetBottom();
        // 设置图片为亮色
        // 切换内容区域
        switch (i) {
            case 0:
                textView1.setTextColor(getResources().getColor(R.color.title_background_bule));
                imageView1.setImageDrawable(getResources().getDrawable(
                        R.drawable.icon_main_bottom_task));
                fragmentview1.setVisibility(View.VISIBLE);
                break;
            case 1:
                textView2.setTextColor(getResources().getColor(R.color.title_background_bule));
                imageView2.setImageDrawable(getResources().getDrawable(
                        R.drawable.icon_main_bottom_plan));
                fragmentview2.setVisibility(View.VISIBLE);

                break;
            case 2:
                textView3.setTextColor(getResources().getColor(R.color.title_background_bule));
                imageView3.setImageDrawable(getResources().getDrawable(
                        R.drawable.icon_main_bottom_page));
                fragmentview3.setVisibility(View.VISIBLE);

                break;
        }
    }

    public void resetBottom() {
        textView1.setTextColor(getResources().getColor(R.color.mainthem));
        fragmentview1.setVisibility(View.INVISIBLE);
        imageView1.setImageDrawable(getResources().getDrawable(
                R.drawable.icon_main_bottom_task_press));
        textView2.setTextColor(getResources().getColor(R.color.mainthem));
        fragmentview2.setVisibility(View.INVISIBLE);
        imageView2.setImageDrawable(getResources().getDrawable(
                R.drawable.icon_main_bottom_plan_press));
        textView3.setTextColor(getResources().getColor(R.color.mainthem));
        fragmentview3.setVisibility(View.INVISIBLE);
        imageView3.setImageDrawable(getResources().getDrawable(
                R.drawable.icon_main_bottom_page_press));
//        linearLayout1.setBackgroundColor(getResources().getColor(
//                R.color.bottom_background_color));
//        linearLayout2.setBackgroundColor(getResources().getColor(
//                R.color.bottom_background_color));
//        linearLayout3.setBackgroundColor(getResources().getColor(
//                R.color.bottom_background_color));
    }

    private void setSelect(int i) {
        setTab(i);
        mViewPager.setCurrentItem(i);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ll_mainbottom_user_info:
                resetBottom();
                setSelect(0);
                break;

            case R.id.ll_mainbottom_plan:
                resetBottom();
                setSelect(1);
                break;

            case R.id.ll_mainbottom_task:
                resetBottom();
                setSelect(2);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBtListener(int i) {
        if (i == 1) {
            bttext.setText("连接成功");
            btloading.setVisibility(View.INVISIBLE);
        }
    }

    //解析JSON数据
    private String parseJSONObjectOrJSONArray(String jsonData) {
        String count = null;
        try {
            count = "";
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            if (jsonArray.length() > 0) {
                JSONObject object = jsonArray.getJSONObject(0);
                String city = object.optString("currentCity");
                JSONArray array = object.getJSONArray("weather_data");
                JSONObject jsonObject1 = array.getJSONObject(0);
                String dateDay = jsonObject1.optString("date");
                String weather = jsonObject1.optString("weather");
                String wind = jsonObject1.optString("wind");
                String temperature = jsonObject1.optString("temperature");
                count = dateDay + " " + weather + " " + wind + " " + temperature;

//                for (int i = 0; i < array.length(); i++) {
//                    JSONObject jsonObject1 = array.getJSONObject(i);
//                    String dateDay = jsonObject1.optString("date");
//                    String weather = jsonObject1.optString("weather");
//                    String wind = jsonObject1.optString("wind");
//                    String temperature = jsonObject1.optString("temperature");
//                    count = count + "\n" + dateDay + " " + weather + " " + wind + " " + temperature;
//                    Log.i("AAA", count);
//                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return count;
    }

    @Override
    protected void onResume() {
        MyConstants.SetServiceInfo(MainActivity.this);
        super.onResume();
    }

    @Override
    protected void onRestart() {
        MyConstants.SetServiceInfo(MainActivity.this);
        super.onRestart();
    }
}
