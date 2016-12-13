package com.gjyf.trolleybus.trolleybuss;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gjyf.app.citylight.MyConstants;
import com.gjyf.trolleybus.trolleybuss.adapter.CailiaoInFoAdapter;
import com.gjyf.trolleybus.trolleybuss.bean.CaiLiaoBean;
import com.gjyf.trolleybus.trolleybuss.bean.TaskInfo;
import com.gjyf.trolleybus.trolleybuss.myview.ClearEditText;
import com.gjyf.trolleybus.trolleybuss.myview.ListScrollView;
import com.gjyf.trolleybus.trolleybuss.myview.MyDialog;
import com.gjyf.trolleybus.trolleybuss.myview.MyEtDialog;
import com.gjyf.trolleybus.trolleybuss.myview.MyProgressDialog;
import com.gjyf.trolleybus.trolleybuss.utils.HttpAPIUtils;
import com.gjyf.trolleybus.trolleybuss.utils.MyData;
import com.gjyf.trolleybus.trolleybuss.utils.ToastUtil;
import com.lidroid.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;


/**
 * 出库申请单填写
 */
public class OutGoingActivity extends BaseActivity {
    private ClearEditText cukudanhao, jiagongbianma, jiagongmingceng, cengpinshuliang, jiagongdanwei;
    private EditText beizhu;
    private LinearLayout stime, wancengtime;
    private Spinner cukuleibie, culiaocangku, senqingbumen;
    private TextView cukuriqi, wancengriqi;
    private ArrayAdapter<String> cukuleibieadapter = null; // 1级适配器
    private ArrayAdapter<String> culiaocangkuadapter = null; // 2级适配器
    private ArrayAdapter<String> senqingbumenadapter = null; // 3级适配器
    private Button cailiaotianjiabt;
    private String[] nowtime;
    public MyEtDialog.Builder mBuilder;
    private String[] cukuleibiestrings = new String[]{"内部工程1", "内部工程2", "内部工程3", "内部工程4", "内部工程5"};
    private String[] culiaocangkustrings = new String[]{"电料工具库1", "电料工具库2", "电料工具库3", "电料工具库4"};
    private String[] senqingbumenstrings = new String[]{"变电队队部", "维修队队部"};
    private int starttimes, endtimes;
    private ListScrollView scrollView;
    private MyProgressDialog dialog;

    private TextView empty;
    private Button mBtnShow, btn_send;
    private ListView cailiaolistview;
    private ArrayList<CaiLiaoBean> cailiaolist = new ArrayList<>();
    private CailiaoInFoAdapter adapter = null;
    private TextView mTvShowAddress;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 400:
                    String request = (String) msg.obj;
                    if (!TextUtils.isEmpty(request)) {
                        if (request.equals("1")) {
                            dialog.dismiss();
                            OutGoingActivity.this.finish();
                            ToastUtil.show("上传成功！");
                        }
                        if (request.equals("0")) {
                            dialog.dismiss();
                            ToastUtil.show("上传失败！");
                        }
                    }
                    break;
                case 404:
                    dialog.dismiss();
                    ToastUtil.show("上传超时，请检查网络或者服务器！");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_going);

        initview();
        initlistener();
    }

    private void initview() {
        nowtime = MyData.getFileName().split("-");
        dialog = MyProgressDialog.createDialog(this);
        ImageView back = (ImageView) findViewById(R.id.back);
        TextView top_text = (TextView) findViewById(R.id.main_top_text);
        top_text.setText("出库申请");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OutGoingActivity.this.finish();
            }
        });
        cailiaotianjiabt = (Button) findViewById(R.id.cailiaotianjiabt);
        scrollView = (ListScrollView) findViewById(R.id.list_scrollview);

        //输入框
        cukudanhao = (ClearEditText) findViewById(R.id.cukudanhao);
        jiagongbianma = (ClearEditText) findViewById(R.id.jiagongbianma);
        jiagongmingceng = (ClearEditText) findViewById(R.id.jiagongmingceng);
        cengpinshuliang = (ClearEditText) findViewById(R.id.jiagongcengpinshiliang);
        jiagongdanwei = (ClearEditText) findViewById(R.id.jiagongdanwei);
        beizhu = (EditText) findViewById(R.id.beizhu);
        cukuriqi = (TextView) findViewById(R.id.cukuriqi);
        wancengriqi = (TextView) findViewById(R.id.wancengriqi);
        mBuilder = new MyEtDialog.Builder(this);

        //Linearlayout
        stime = (LinearLayout) findViewById(R.id.stime);
        wancengtime = (LinearLayout) findViewById(R.id.wancengtime);
        //Spinner
        cukuleibie = (Spinner) findViewById(R.id.cukuleibie);
        culiaocangku = (Spinner) findViewById(R.id.cangku);
        senqingbumen = (Spinner) findViewById(R.id.shenqingbumen);

        btn_send = (Button) findViewById(R.id.btn_send);
        empty = (TextView) findViewById(R.id.empty);
        cailiaolistview = (ListView) findViewById(R.id.cailiaolist);
        mBtnShow = (Button) findViewById(R.id.btn_show);
        mTvShowAddress = (TextView) findViewById(R.id.tv_showAddress);
        adapter = new CailiaoInFoAdapter(OutGoingActivity.this, cailiaolist);
        cailiaolistview.setEmptyView(empty);
        cailiaolistview.setAdapter(adapter);
    }

    private void initlistener() {
        scrollView.setListView(cailiaolistview);
        cailiaolistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final int x = i;
                MyDialog.Builder mBuilder = new MyDialog.Builder(OutGoingActivity.this);
                mBuilder.setMessage("是否删除材料信息？");
                mBuilder.setNegativeButton("确认", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        cailiaolist.remove(x);
                        adapter.notifyDataSetChanged();
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

                return false;
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cukuriqistring = cukuriqi.getText().toString().trim();
                String cukuleibiestring = cukuleibie.getSelectedItem().toString().trim();
                String culiaocangkustring = culiaocangku.getSelectedItem().toString().trim();
                String senqingbumenstring = senqingbumen.getSelectedItem().toString().trim();

                RequestParams params = new RequestParams();
                params.addBodyParameter("time", cukuriqistring);
                params.addBodyParameter("type", cukuleibiestring);
                params.addBodyParameter("warehouse", culiaocangkustring);
                params.addBodyParameter("section", senqingbumenstring);
                if (!TextUtils.isEmpty(cukuriqistring) && !TextUtils.isEmpty(cukuleibiestring) && !TextUtils.isEmpty(culiaocangkustring) && !TextUtils.isEmpty(senqingbumenstring) && cailiaolist.size() > 0) {
                    String string = "";
                    for (CaiLiaoBean bean : cailiaolist) {
                        String st = bean.getName() + "," + bean.getNum();
                        string += st + ":";
                    }
                    if (!TextUtils.isEmpty(string)) {
                        string = string.substring(0, string.length() - 1);
                    }

                    params.addBodyParameter("material", string);
                    HttpAPIUtils.HttpPost(handler, MyConstants.OUTGOING_POST, params);
                    dialog.show();
                } else {
                    ToastUtil.show("请将信息补充完整！");
                }

            }
        });

        mBtnShow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //跳往选择界面
                Intent intent = new Intent(OutGoingActivity.this, SelectCitiesDialogActivity.class);
                intent.putExtra("address", "");//不传参数也可以，就不会有默认选中某个值
                startActivityForResult(intent, 1001);
            }
        });

        cukuleibieadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, cukuleibiestrings);
        cukuleibie.setAdapter(cukuleibieadapter);
        culiaocangkuadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, culiaocangkustrings);
        culiaocangku.setAdapter(culiaocangkuadapter);

        senqingbumenadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, senqingbumenstrings);
        senqingbumen.setAdapter(senqingbumenadapter);


        // 开始时间选择器
        stime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog datePicker = new DatePickerDialog(
                        OutGoingActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        cukuriqi.setText(year + "-" + (monthOfYear + 1)
                                + "-" + dayOfMonth);
                        starttimes = year * 365 + (monthOfYear + 1)
                                * 30 + dayOfMonth;
                    }
                }, Integer.parseInt(nowtime[0]), Integer.parseInt(nowtime[1]) - 1, Integer.parseInt(nowtime[2]));
                datePicker.show();
            }
        });


        //完成时间选择器
        wancengtime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog datePicker = new DatePickerDialog(
                        OutGoingActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        wancengriqi.setText(year + "-" + (monthOfYear + 1)
                                + "-" + dayOfMonth);
                        endtimes = year * 365 + (monthOfYear + 1)
                                * 30 + dayOfMonth;
                    }
                }, Integer.parseInt(nowtime[0]), Integer.parseInt(nowtime[1]) - 1, Integer.parseInt(nowtime[2]));
                datePicker.show();
            }
        });

        //前往材料添加
        cailiaotianjiabt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  String cukudanhaostring = cukudanhao.getText().toString().trim();
                String cukuriqistring = cukuriqi.getText().toString().trim();
                String cukuleibiestring = cukuleibie.getSelectedItem().toString().trim();
                String culiaocangkustring = culiaocangku.getSelectedItem().toString().trim();
                String senqingbumenstring = senqingbumen.getSelectedItem().toString().trim();

                if (!TextUtils.isEmpty(cukuriqistring) && !TextUtils.isEmpty(cukuleibiestring) && !TextUtils.isEmpty(culiaocangkustring) && !TextUtils.isEmpty(senqingbumenstring)) {
                    Intent intent = new Intent(OutGoingActivity.this, OutGoingCailiaoActivity.class);
                    intent.putExtra("cukuriqistring", cukuriqistring);
                    intent.putExtra("cukuleibiestring", cukuleibiestring);
                    intent.putExtra("culiaocangkustring", senqingbumenstring);
                    intent.putExtra("senqingbumenstring", senqingbumenstring);
                    startActivity(intent);
                } else {
                    ToastUtil.show("请将信息补充完整！");
                }
                // Log.v("yangzss", cukudanhaostring + "--" + cukuriqistring + "--" + cukuleibiestring + "--" + culiaocangkustring + "--" + senqingbumenstring);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == 1002) {
            CaiLiaoBean caiLiaoBean = (CaiLiaoBean) data.getSerializableExtra("address");
            cailiaolist.add(caiLiaoBean);
            adapter.notifyDataSetChanged();
        }
    }
}
