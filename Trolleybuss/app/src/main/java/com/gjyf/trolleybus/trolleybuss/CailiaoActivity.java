package com.gjyf.trolleybus.trolleybuss;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gjyf.app.citylight.MyApplication;
import com.gjyf.trolleybus.trolleybuss.adapter.CailiaoInFoAdapter;
import com.gjyf.trolleybus.trolleybuss.bean.CaiLiaoBean;
import com.gjyf.trolleybus.trolleybuss.bean.QiangXCaiLiao;
import com.gjyf.trolleybus.trolleybuss.bean.TaskInfo;

import com.gjyf.trolleybus.trolleybuss.bean.TaskRecordInfo;
import com.gjyf.trolleybus.trolleybuss.myview.MyDialog;
import com.gjyf.trolleybus.trolleybuss.utils.PhotoUtils;
import com.gjyf.trolleybus.trolleybuss.utils.ToastUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 抢修材料添加
 * @create_date 2015-5-22下午5:13:01
 */
public class CailiaoActivity extends BaseActivity {

    private TextView empty;
    private Button mBtnShow, cauliaoBt;
    private ListView cailiaolistview;
    private List<CaiLiaoBean> cailiaolist = new ArrayList<>();
    private List<QiangXCaiLiao> qiangXCaiLiaos = new ArrayList<>();
    private CailiaoInFoAdapter adapter = null;
    private TextView mTvShowAddress;
    private TaskInfo taskInfo;
    private DbUtils dbUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cailiao);
        initView();
        initlistener();
    }

    private void initlistener() {

        //材料信息确认
        cauliaoBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                saveConfirm();
                finish();
            }
        });

        cailiaolistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final int x = i;
                MyDialog.Builder mBuilder = new MyDialog.Builder(CailiaoActivity.this);
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

        mBtnShow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //跳往选择界面
                Intent intent = new Intent(CailiaoActivity.this, SelectCitiesDialogActivity.class);
                intent.putExtra("address", "");//不传参数也可以，就不会有默认选中某个值
                startActivityForResult(intent, 1001);
            }
        });
    }

    private void initView() {
        dbUtils = MyApplication.getDbUtils();
        taskInfo = (TaskInfo) getIntent().getSerializableExtra("taskinfo");
        ImageView back = (ImageView) findViewById(R.id.back);
        TextView top_text = (TextView) findViewById(R.id.main_top_text);
        top_text.setText("材料添加");
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                saveConfirm();
                CailiaoActivity.this.finish();
            }
        });
        empty = (TextView) findViewById(R.id.empty);
        cailiaolistview = (ListView) findViewById(R.id.cailiaolist);
        mBtnShow = (Button) findViewById(R.id.btn_show);
        cauliaoBt = (Button) findViewById(R.id.cailiaobt);
        mTvShowAddress = (TextView) findViewById(R.id.tv_showAddress);
        adapter = new CailiaoInFoAdapter(CailiaoActivity.this, cailiaolist);
        cailiaolistview.setEmptyView(empty);
        cailiaolistview.setAdapter(adapter);

        try {
            qiangXCaiLiaos = dbUtils.findAll(Selector.from(QiangXCaiLiao.class)
                    .where("taskid", "=", taskInfo.getAppNo()));
            if (qiangXCaiLiaos.size() == 0) {
                QiangXCaiLiao info = new QiangXCaiLiao();
                info.setTaskid(taskInfo.getAppNo());
                dbUtils.save(info);
                qiangXCaiLiaos = dbUtils.findAll(Selector.from(QiangXCaiLiao.class)
                        .where("taskid", "=", taskInfo.getAppNo()));
                cailiaolist = JSON.parseArray(qiangXCaiLiaos.get(0).getJsonstring(), CaiLiaoBean.class);
                if (cailiaolist == null) {
                    cailiaolist = new ArrayList<>();
                }
                adapter = new CailiaoInFoAdapter(CailiaoActivity.this, cailiaolist);
                cailiaolistview.setAdapter(adapter);
            } else {
                cailiaolist = JSON.parseArray(qiangXCaiLiaos.get(0).getJsonstring(), CaiLiaoBean.class);
                if (cailiaolist == null) {
                    cailiaolist = new ArrayList<>();
                }
                adapter = new CailiaoInFoAdapter(CailiaoActivity.this, cailiaolist);
                cailiaolistview.setAdapter(adapter);
            }


        } catch (DbException e) {
            e.printStackTrace();
        }


    }

    public void saveConfirm() {
        String cailiaojson = JSON.toJSONString(cailiaolist);
        QiangXCaiLiao qiangXCaiLiao = qiangXCaiLiaos.get(0);
        qiangXCaiLiao.setTaskid(taskInfo.getAppNo());
        qiangXCaiLiao.setJsonstring(cailiaojson);
        try {

            dbUtils.delete(qiangXCaiLiao);
            dbUtils.save(qiangXCaiLiao);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == 1002) {
            CaiLiaoBean caiLiaoBean = (CaiLiaoBean) data.getSerializableExtra("address");
            mTvShowAddress.setText(data.getStringExtra("address"));
            cailiaolist.add(caiLiaoBean);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        saveConfirm();
        finish();
    }
}
