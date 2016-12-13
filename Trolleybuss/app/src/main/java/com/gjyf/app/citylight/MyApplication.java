package com.gjyf.app.citylight;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.dotel.libr900.R900Manager;
import com.gjyf.readtagr900.R900DataSend;
import com.gjyf.trolleybus.trolleybuss.R;
import com.gjyf.trolleybus.trolleybuss.bean.QiangXCaiLiao;
import com.gjyf.trolleybus.trolleybuss.bean.StorageInfo;
import com.gjyf.trolleybus.trolleybuss.bean.TaskRecordInfo;
import com.gjyf.trolleybus.trolleybuss.rfidconnect.OnBtConnectClickListener;
import com.gjyf.trolleybus.trolleybuss.rfidconnect.OnBtInfoClickListener;
import com.gjyf.trolleybus.trolleybuss.utils.EPCUtils;
import com.gjyf.trolleybus.trolleybuss.utils.NetWorkUtils;
import com.gjyf.trolleybus.trolleybuss.utils.ToastUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.core.BitmapSize;
import com.lidroid.xutils.exception.DbException;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 作者：Yang on 2016/10/31 08:57
 */
public class MyApplication extends Application {
    private static R900Manager mR900Manager;
    private static R900DataSend mR900DataSend;
    private static DbUtils dbUtils;
    private static HttpUtils httpUtils;
    private static Context mContext;
    public static LocationClient mLocClient;
    public static int width;
    public static int height;
    public static NetWorkUtils netWorkUtils;
    private static Handler handler;
    private boolean mConnected; // 是否连接
    private static OnBtConnectClickListener onBtConnectClickListener;
    private static OnBtInfoClickListener onBtInfoClickListener;
    private static BitmapDisplayConfig config;
    private static BitmapUtils bitmapUtils;
    private SQLiteDatabase mDb;


    public static void setBtconncetListener(
            OnBtConnectClickListener onBtClickListeners) {
        onBtConnectClickListener = onBtClickListeners;
    }

    public static void setBtInfoListener(
            OnBtInfoClickListener onBtClickListeners) {
        onBtInfoClickListener = onBtClickListeners;
    }


    @Override
    public void onCreate() {
        SDKInitializer.initialize(this);
        super.onCreate();
        mLocClient = new LocationClient(this);
        httpUtils = new HttpUtils();
        ToastUtil.init(this);
        bitmapUtils = new BitmapUtils(this);
        mContext = this;
        netWorkUtils = new NetWorkUtils(this);
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        setBitmapConfig();

        initDbUtils();   //数据库初始化

        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 2:
                        try {
                            byte[] arrayOfByte2 = null;
                            byte[] arrayOfByte1 = (byte[]) msg.obj;
                            String result = new String(arrayOfByte1);
                            result = replaceBlank(result);
                            result = result.substring(0, 12);
                            arrayOfByte2 = Arrays.copyOfRange(arrayOfByte1, 7,
                                    msg.arg1 - 1);
                            String strEpc = bytesToHexString(arrayOfByte2);

                            if (strEpc.substring(0, 4).equals("6400")) {
                                strEpc = strEpc.substring(4, strEpc.length());
                                EPCUtils.GetEpcBean(strEpc);
                            }

                            onBtInfoClickListener
                                    .onBtListener(result);

                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                        break;
                    case 200:
                        onBtConnectClickListener.onBtListener(1);
                        break;
                }
            }

            ;
        };
        mR900Manager = new R900Manager(handler);// 创建R900manager对象

    }

    private void initDbUtils() {
        DbUtils.DaoConfig config = new DbUtils.DaoConfig(getApplicationContext());
        config.setDbName("TrolleyBusInfos.db");
        config.setDbVersion(1);
        config.setDbUpgradeListener(new DbUtils.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbUtils db, int oldVersion, int newVersion) {
                if (newVersion > oldVersion) {
                    try {
                        db.dropDb();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        dbUtils = DbUtils.create(config);
        dbUtils.configAllowTransaction(true);
        dbUtils.configDebug(true);
        try {
            dbUtils.createTableIfNotExist(StorageInfo.class);
            dbUtils.createTableIfNotExist(TaskRecordInfo.class);
            dbUtils.createTableIfNotExist(QiangXCaiLiao.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void setBitmapConfig() {
        config = new BitmapDisplayConfig();
        config.setLoadingDrawable(getResources().getDrawable(
                R.drawable.iamge_loading));
        config.setLoadFailedDrawable(getResources().getDrawable(
                R.drawable.image_loading_fale));
        config.setBitmapMaxSize(new BitmapSize(0, 0));
    }

    public static Context getContext() {
        return mContext;
    }

    public static LocationClient getLocClient() {
        return mLocClient;
    }

    public static NetWorkUtils getNetWorkUtils() {
        return netWorkUtils;
    }

    public static BitmapDisplayConfig getBitmapConfig() {
        return config;
    }

    public static BitmapUtils getBitmapUtils() {
        return bitmapUtils;
    }

    public static HttpUtils getHttpUtils() {
        return httpUtils;
    }

    public static DbUtils getDbUtils() {
        return dbUtils;
    }

    public static R900Manager getR900Manager() {
        return mR900Manager;
    }

    public static R900DataSend getR900DataSend() {
        return mR900DataSend;
    }

    public static Handler getHandler() {
        return handler;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
}
