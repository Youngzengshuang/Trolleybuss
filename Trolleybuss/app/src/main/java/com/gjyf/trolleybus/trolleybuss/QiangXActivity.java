package com.gjyf.trolleybus.trolleybuss;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gjyf.app.citylight.MyApplication;
import com.gjyf.app.citylight.MyConstants;
import com.gjyf.trolleybus.trolleybuss.adapter.GridViewAdapter;
import com.gjyf.trolleybus.trolleybuss.adapter.GridViewShowAdapter;
import com.gjyf.trolleybus.trolleybuss.bean.BaidumapInfo;
import com.gjyf.trolleybus.trolleybuss.bean.CaiLiaoBean;
import com.gjyf.trolleybus.trolleybuss.bean.QiangXCaiLiao;
import com.gjyf.trolleybus.trolleybuss.bean.TaskInfo;
import com.gjyf.trolleybus.trolleybuss.bean.TaskRecordInfo;
import com.gjyf.trolleybus.trolleybuss.myview.LargePictureActivity;
import com.gjyf.trolleybus.trolleybuss.myview.MyDialog;
import com.gjyf.trolleybus.trolleybuss.myview.MyProgressDialog;
import com.gjyf.trolleybus.trolleybuss.utils.FileUtils;
import com.gjyf.trolleybus.trolleybuss.utils.HttpAPIUtils;
import com.gjyf.trolleybus.trolleybuss.utils.ImageTools;
import com.gjyf.trolleybus.trolleybuss.utils.PhotoUtils;
import com.gjyf.trolleybus.trolleybuss.utils.SharedPreferencesUtils;
import com.gjyf.trolleybus.trolleybuss.utils.ToastUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.RequestParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 抢修完成
 */

public class QiangXActivity extends BaseActivity {
    private TaskInfo taskInfo;
    private BaidumapInfo baidumapInfo;
    private RadioGroup radioGroup;
    private RadioButton finishRadioButton = null;
    private RadioButton unfinishRadioButton = null;
    private String la, lo;
    private TextView id, guzangdidian, baoxiuren, wentimiaoshu;
    private TextView DeviceID, DeviceName, Type, NowNo, usertime, Streetname, createtime, OtherDevice, Date;
    private Button push; //信息 提交按钮
    private GridView gridView;
    private EditText remark_et;
    private List<Bitmap> bitmapList = new ArrayList<>();
    private ImageButton photo;
    private String flag = "1";
    private static final int TAKE_PICTURE = 0;
    private static final int CHOOSE_PICTURE = 1;
    private DbUtils dbUtils = null;
    private List<TaskRecordInfo> taskRecordInfoBD = new ArrayList<>();
    private HorizontalScrollView horizontalScrollView;
    private ArrayList<String> urlList = new ArrayList<>();
    private MyProgressDialog dialog;
    List<QiangXCaiLiao> qiangXCaiLiaos = new ArrayList<>();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 400:
                    String request = (String) msg.obj;
                    if (!TextUtils.isEmpty(request)) {
                        if (request.equals("1")) {
                            dialog.dismiss();
                            try {
                                dbUtils.deleteAll(qiangXCaiLiaos);
                                dbUtils.deleteAll(taskRecordInfoBD);
                            } catch (Exception e) {

                            }
                            QiangXActivity.this.setResult(1, getIntent());
                            QiangXActivity.this.finish();
                            ToastUtil.show("提交成功！");
                        }
                        if (request.equals("0")) {
                            dialog.dismiss();
                            ToastUtil.show("提交失败！");
                        }
                    }
                    break;
                case 404:
                    ToastUtil.show("上传超时，请检查网络或者服务器！");
                    break;
                case 2:
                    dialog.dismiss();
                    QiangXActivity.this.setResult(2, getIntent());
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qiang_x);

        initview();
        initlistener();
    }

    /*
          组件数据初始化
     */
    private void initview() {
        radioGroup = (RadioGroup) findViewById(R.id.login_select);
        finishRadioButton = (RadioButton) findViewById(R.id.ld);
        unfinishRadioButton = (RadioButton) findViewById(R.id.cz);

        dialog = MyProgressDialog.createDialog(this);
        dbUtils = MyApplication.getDbUtils();
        Intent intent = getIntent();
        taskInfo = (TaskInfo) intent.getSerializableExtra("taskinfo");
        //资产信息初始化
        baidumapInfo = (BaidumapInfo) intent.getSerializableExtra("baidumapinfo");
        DeviceID = (TextView) findViewById(R.id.DeviceID);  // 编号
        DeviceName = (TextView) findViewById(R.id.DeviceName); // 设备名称
        Type = (TextView) findViewById(R.id.Type); // 规格类型
        NowNo = (TextView) findViewById(R.id.NowNo);  // 现有编号
        usertime = (TextView) findViewById(R.id.usertime); // 使用年限
        Streetname = (TextView) findViewById(R.id.Streetname);  //位置
        createtime = (TextView) findViewById(R.id.createtime);   //创建时间
        OtherDevice = (TextView) findViewById(R.id.OtherDevice); // 其他设备类型
        Date = (TextView) findViewById(R.id.Date);  //备注信息


        //故障信息初始化
        DeviceID.setText(baidumapInfo.getDeviceID());
        DeviceName.setText(baidumapInfo.getDeviceName());
        Type.setText(baidumapInfo.getType());
        NowNo.setText(baidumapInfo.getNowNo());
        usertime.setText(baidumapInfo.getUsertime());
        Streetname.setText(baidumapInfo.getStreetname());
        createtime.setText(baidumapInfo.getCreatetime());
        OtherDevice.setText(baidumapInfo.getOtherDevice());
        Date.setText(baidumapInfo.getDate());
        la = intent.getStringExtra("la");
        lo = intent.getStringExtra("lo");

        id = (TextView) findViewById(R.id.id);
        guzangdidian = (TextView) findViewById(R.id.guzangdidian);
        baoxiuren = (TextView) findViewById(R.id.baoxiuren);
        wentimiaoshu = (TextView) findViewById(R.id.wentimiaoshu);
        photo = (ImageButton) findViewById(R.id.btn_photo);

        id.setText(taskInfo.getAppNo());

        guzangdidian.setText(taskInfo.getDistrictTown() + "," + taskInfo.getStreetNo() + "," + taskInfo.getDefailedAdderss());
        wentimiaoshu.setText(taskInfo.getExcpDesc());
        baoxiuren.setText(taskInfo.getReportName());

        remark_et = (EditText) findViewById(R.id.remark_et);
        gridView = (GridView) findViewById(R.id.grid);
        push = (Button) findViewById(R.id.btn_confirm);

        ImageView back = (ImageView) findViewById(R.id.back);
        TextView top_text = (TextView) findViewById(R.id.main_top_text);
        top_text.setText("工程抢修");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveConfirm();
            }
        });


        //本地数据获取
        try {
            taskRecordInfoBD = dbUtils.findAll(Selector.from(TaskRecordInfo.class)
                    .where("taskid", "=", taskInfo.getAppNo()));
            Log.v("yangzsss", "---------------" + taskInfo.getAppNo());
            Log.v("yangzsss", "---------1------" + taskRecordInfoBD.size());
            if (taskRecordInfoBD.size() == 0) {
                TaskRecordInfo info = new TaskRecordInfo();
                info.setTaskid(taskInfo.getAppNo());
                info.setRemark("");
                dbUtils.save(info);
                taskRecordInfoBD = dbUtils.findAll(Selector.from(TaskRecordInfo.class)
                        .where("taskid", "=", taskInfo.getAppNo()));
            } else {
                remark_et.setText(taskRecordInfoBD.get(0).getRemark().toString().trim());
                if (!TextUtils.isEmpty(taskRecordInfoBD.get(0).getImage1())) {
                    bitmapList.add(PhotoUtils.convertStringToIcon(taskRecordInfoBD.get(0).getImage1()));
                }
                if (!TextUtils.isEmpty(taskRecordInfoBD.get(0).getImage2())) {
                    bitmapList.add(PhotoUtils.convertStringToIcon(taskRecordInfoBD.get(0).getImage2()));
                }
                if (!TextUtils.isEmpty(taskRecordInfoBD.get(0).getImage3())) {
                    bitmapList.add(PhotoUtils.convertStringToIcon(taskRecordInfoBD.get(0).getImage3()));
                }
                if (!TextUtils.isEmpty(taskRecordInfoBD.get(0).getImage4())) {
                    bitmapList.add(PhotoUtils.convertStringToIcon(taskRecordInfoBD.get(0).getImage4()));
                }
                if (!TextUtils.isEmpty(taskRecordInfoBD.get(0).getImage5())) {
                    bitmapList.add(PhotoUtils.convertStringToIcon(taskRecordInfoBD.get(0).getImage5()));
                }
                if (!TextUtils.isEmpty(taskRecordInfoBD.get(0).getImage6())) {
                    bitmapList.add(PhotoUtils.convertStringToIcon(taskRecordInfoBD.get(0).getImage6()));
                }
                Log.v("yangzsss", "--------2-------" + taskRecordInfoBD.size());
                setGridView();
            }
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void initlistener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO 选项
                if (finishRadioButton.getId() == checkedId) {
                    flag = "1";
                } else if (unfinishRadioButton.getId() == checkedId) {
                    flag = "2";
                }
            }
        });
        //上传
        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmapList.size() > 6) {
                    ToastUtil.show("最多上传6张照片！");
                } else {
                    MyDialog.Builder mBuilder = new MyDialog.Builder(QiangXActivity.this);
                    mBuilder.setMessage("是否提交任务完成信息？");
                    mBuilder.setNegativeButton("确认", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {


                            try {
                                qiangXCaiLiaos = dbUtils.findAll(Selector.from(QiangXCaiLiao.class)
                                        .where("taskid", "=", taskInfo.getAppNo()));
                                String string = "";

                                if (qiangXCaiLiaos != null) {
                                    if (qiangXCaiLiaos.size() > 0) {
                                        List<CaiLiaoBean> cailiaolist = JSON.parseArray(qiangXCaiLiaos.get(0).getJsonstring(), CaiLiaoBean.class);
                                        for (int i = 0; i < cailiaolist.size(); i++) {
                                            CaiLiaoBean bean = cailiaolist.get(i);
                                            String st = bean.getName() + "," + bean.getNum();
                                            string += st + ":";
                                        }
                                        if (!TextUtils.isEmpty(string)) {
                                            string = string.substring(0, string.length() - 1);
                                        }

                                    }
                                }

                                final String remark = remark_et.getText().toString().trim();
                                final String finalString = string;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        RequestParams params = new RequestParams();
                                        params.addBodyParameter("uid", SharedPreferencesUtils.getString(QiangXActivity.this, "uid"));
                                        params.addBodyParameter("repairId", taskInfo.getAppNo());
                                        params.addBodyParameter("flag", flag);

                                        String image1 = "", image2 = "", image3 = "", image4 = "", image5 = "", image6 = "";
                                        for (int i = 0; i < bitmapList.size(); i++) {
                                            switch (i) {
                                                case 0:
                                                    image1 = PhotoUtils.bitmaptoString(bitmapList.get(i));
                                                    break;
                                                case 1:
                                                    image2 = PhotoUtils.bitmaptoString(bitmapList.get(i));
                                                    break;
                                                case 2:
                                                    image3 = PhotoUtils.bitmaptoString(bitmapList.get(i));
                                                    break;
                                                case 3:
                                                    image4 = PhotoUtils.bitmaptoString(bitmapList.get(i));
                                                    break;
                                                case 4:
                                                    image5 = PhotoUtils.bitmaptoString(bitmapList.get(i));
                                                    break;
                                                case 5:
                                                    image6 = PhotoUtils.bitmaptoString(bitmapList.get(i));
                                                    break;
                                            }

                                        }

                                        params.addBodyParameter("img1", image1);
                                        params.addBodyParameter("img2", image2);
                                        params.addBodyParameter("img3", image3);
                                        params.addBodyParameter("img4", image4);
                                        params.addBodyParameter("img5", image5);
                                        params.addBodyParameter("img6", image6);
                                        params.addBodyParameter("result", remark);

                                        params.addBodyParameter("material", finalString);
                                        HttpAPIUtils.HttpPost(handler, MyConstants.REPAIR_POST_RECOR, params);
                                        Log.v("yangzsss", "---------------" + taskInfo.getAppNo() + "-----" + finalString + "------" + remark + "-------");
                                    }
                                }).start();

                                dialog.show();
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                            arg0.dismiss();
                        }
                    });

                    mBuilder.setPositiveButton("取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                        }
                    });
                    mBuilder.create().show();
                }


            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Bitmap[] nowbitmap = {null};
                nowbitmap[0] = bitmapList.get(i);
                if (nowbitmap[0] != null) {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
                    nowbitmap[0].compress(Bitmap.CompressFormat.PNG, 100, output);//把bitmap100%高质量压缩 到 output对象里
                    byte[] result = output.toByteArray();//转换成功了
                    try {
                        output.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    LargePictureActivity.actionStartBitmap(QiangXActivity.this, result);
                } else {
                    ToastUtil.show("图片未加载");
                }

            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int x = i;
                MyDialog.Builder mBuilder = new MyDialog.Builder(QiangXActivity.this);
                mBuilder.setMessage("是否删除材料信息？");
                mBuilder.setNegativeButton("确认", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        bitmapList.remove(x);
                        setGridView();
                        arg0.dismiss();
                    }
                });
                mBuilder.setPositiveButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
                mBuilder.create().show();


                return true;
            }
        });
        //图片获取
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmapList.size() <= 6) {

                    if (ActivityCompat.checkSelfPermission(QiangXActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(QiangXActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                1);
                    } else {
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
                        //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                    }

                } else {
                    ToastUtil.show("最多上传五张图片");
                }

            }
        });
    }

    /**
     * 设置GirdView参数，绑定数据
     */
    private void setGridView() {
        int size = bitmapList.size();
        int length = 100;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 4) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth(itemWidth); // 设置列表项宽
        gridView.setHorizontalSpacing(5); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(size); // 设置列数量=列表集合数

        GridViewAdapter adapter = new GridViewAdapter(getApplicationContext(),
                bitmapList);
        gridView.setAdapter(adapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    //将保存在本地的图片取出并缩小后显示在界面上
                    Bitmap bitmap = FileUtils.tryGetBitmap(Environment.getExternalStorageDirectory() + "/image.jpg", -1, 480 * 360);
                    //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                    //将处理过的图片显示在界面上，并保存到本地
                    bitmapList.add(bitmap);
                    setGridView();
                    break;

            }
        }
    }


    public void saveConfirm() {
        MyDialog.Builder mBuilder = new MyDialog.Builder(this);
        mBuilder.setMessage("是否保存页面信息？");
        mBuilder.setNegativeButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                String etstring = remark_et.getText().toString().trim();
                final TaskRecordInfo info = taskRecordInfoBD.get(0);
                info.setTaskid(taskInfo.getAppNo());
                info.setImage1("");
                info.setImage2("");
                info.setImage3("");
                info.setImage4("");
                info.setImage5("");
                info.setImage6("");
                info.setRemark(etstring);

                if (bitmapList.size() > 6) {
                    ToastUtil.show("最多保存6张图片");
                    arg0.dismiss();
                } else {
                    if (bitmapList.size() != 0) {
                        dialog.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < bitmapList.size(); i++) {
                                    switch (i) {
                                        case 0:
                                            info.setImage1(PhotoUtils.bitmaptoString(bitmapList.get(i)));
                                            break;
                                        case 1:
                                            info.setImage2(PhotoUtils.bitmaptoString(bitmapList.get(i)));
                                            break;
                                        case 2:
                                            info.setImage3(PhotoUtils.bitmaptoString(bitmapList.get(i)));
                                            break;
                                        case 3:
                                            info.setImage4(PhotoUtils.bitmaptoString(bitmapList.get(i)));
                                            break;
                                        case 4:
                                            info.setImage5(PhotoUtils.bitmaptoString(bitmapList.get(i)));
                                            break;
                                        case 5:
                                            info.setImage6(PhotoUtils.bitmaptoString(bitmapList.get(i)));
                                            break;
                                    }

                                }
                                try {
                                    dbUtils.deleteAll(taskRecordInfoBD);
                                    dbUtils.save(info);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                                handler.sendEmptyMessage(2);
                            }
                        }).start();
                    } else {
                        try {
                            dbUtils.deleteAll(taskRecordInfoBD);
                            dbUtils.save(info);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                        QiangXActivity.this.setResult(2, getIntent());
                        finish();
                    }
                    arg0.dismiss();
                }
            }
        });

        mBuilder.setPositiveButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                QiangXActivity.this.setResult(2, getIntent());
                finish();
            }
        });
        mBuilder.create().show();


    }

    @Override
    public void onBackPressed() {
        saveConfirm();
    }
}
