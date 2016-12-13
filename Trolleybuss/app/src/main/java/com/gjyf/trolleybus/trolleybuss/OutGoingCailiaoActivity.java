package com.gjyf.trolleybus.trolleybuss;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gjyf.app.citylight.MyConstants;
import com.gjyf.trolleybus.trolleybuss.adapter.CailiaoInFoAdapter;
import com.gjyf.trolleybus.trolleybuss.bean.CaiLiaoBean;
import com.gjyf.trolleybus.trolleybuss.utils.HttpAPIUtils;
import com.gjyf.trolleybus.trolleybuss.utils.SharedPreferencesUtils;
import com.gjyf.trolleybus.trolleybuss.utils.ToastUtil;
import com.lidroid.xutils.http.RequestParams;

import java.util.ArrayList;

/**
 * @author wtw
 * @create_date 2015-5-22下午5:13:01
 */
public class OutGoingCailiaoActivity extends BaseActivity {

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
                            OutGoingCailiaoActivity.this.finish();
                            ToastUtil.show("上传成功！");
                        }
                        if (request.equals("0")) {
                            ToastUtil.show("上传失败！");
                        }
                    }
                    break;
                case 404:
                    ToastUtil.show("上传超时，请检查网络或者服务器！");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoingcailiao);
        initView();
        initlistener();
    }

    private void initlistener() {

        cailiaolistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                cailiaolist.remove(i);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        btn_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                RequestParams params = new RequestParams();
                params.addBodyParameter("time", intent.getStringExtra("cukuriqistring"));
                params.addBodyParameter("type", intent.getStringExtra("cukuleibiestring"));
                params.addBodyParameter("warehouse", intent.getStringExtra("culiaocangkustring"));
                params.addBodyParameter("section", intent.getStringExtra("senqingbumenstring"));

                String string = "";
                for (CaiLiaoBean bean : cailiaolist) {
                    String st = bean.getName() + "," + bean.getNum();
                    string += st + ":";
                }
                string = string.substring(0, string.length() - 1);
                params.addBodyParameter("material", string);
                HttpAPIUtils.HttpPost(handler, MyConstants.OUTGOING_POST, params);
            }
        });

        mBtnShow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //跳往选择界面
                Intent intent = new Intent(OutGoingCailiaoActivity.this, SelectCitiesDialogActivity.class);
                intent.putExtra("address", "");//不传参数也可以，就不会有默认选中某个值
                startActivityForResult(intent, 1001);
            }
        });
    }

    private void initView() {
        ImageView back = (ImageView) findViewById(R.id.back);
        TextView top_text = (TextView) findViewById(R.id.main_top_text);
        top_text.setText("材料添加");
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                OutGoingCailiaoActivity.this.finish();
            }
        });
        btn_send = (Button) findViewById(R.id.btn_send);
        empty = (TextView) findViewById(R.id.empty);
        cailiaolistview = (ListView) findViewById(R.id.cailiaolist);
        mBtnShow = (Button) findViewById(R.id.btn_show);
        mTvShowAddress = (TextView) findViewById(R.id.tv_showAddress);
        adapter = new CailiaoInFoAdapter(OutGoingCailiaoActivity.this, cailiaolist);
        cailiaolistview.setEmptyView(empty);
        cailiaolistview.setAdapter(adapter);

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
