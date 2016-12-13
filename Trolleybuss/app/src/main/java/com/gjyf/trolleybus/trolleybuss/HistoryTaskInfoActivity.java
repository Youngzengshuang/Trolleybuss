package com.gjyf.trolleybus.trolleybuss;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gjyf.trolleybus.trolleybuss.adapter.GridViewShowAdapter;
import com.gjyf.trolleybus.trolleybuss.bean.BaidumapInfo;
import com.gjyf.trolleybus.trolleybuss.bean.TaskInfo;
import com.gjyf.trolleybus.trolleybuss.bean.TaskRecordInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 完成抢修历史查看
 */
public class HistoryTaskInfoActivity extends BaseActivity {
    private TaskInfo taskInfo = null;
    private TextView id, guzangdidian, baoxiuren, wentimiaoshu, emptyview, remark_et, cailiaotext;
    private List<String> urlList = new ArrayList<>();

    private HorizontalScrollView horizontalScrollView;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_task_info);

        initview();
        initlistener();
    }

    private void initlistener() {

    }

    private void initview() {
        ImageView back = (ImageView) findViewById(R.id.back);
        TextView top_text = (TextView) findViewById(R.id.main_top_text);
        top_text.setText("任务历史详情");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HistoryTaskInfoActivity.this.finish();
            }
        });
        taskInfo = (TaskInfo) getIntent().getSerializableExtra("taskinfo");
        emptyview = (TextView) findViewById(R.id.emptyview);
        cailiaotext = (TextView) findViewById(R.id.cailiaotext);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalview);
        id = (TextView) findViewById(R.id.id);
        remark_et = (TextView) findViewById(R.id.remark_et);
        guzangdidian = (TextView) findViewById(R.id.guzangdidian);
        baoxiuren = (TextView) findViewById(R.id.baoxiuren);
        wentimiaoshu = (TextView) findViewById(R.id.wentimiaoshu);
        gridView = (GridView) findViewById(R.id.gridhshow);
        TaskRecordInfo recordInfo = JSON.parseObject(taskInfo.getRemark(), TaskRecordInfo.class);

        if (!TextUtils.isEmpty(recordInfo.getImage1())) {
            urlList.add(recordInfo.getImage1());
        }
        if (!TextUtils.isEmpty(recordInfo.getImage2())) {
            urlList.add(recordInfo.getImage2());
        }
        if (!TextUtils.isEmpty(recordInfo.getImage3())) {
            urlList.add(recordInfo.getImage3());
        }
        if (!TextUtils.isEmpty(recordInfo.getImage4())) {
            urlList.add(recordInfo.getImage4());
        }
        if (!TextUtils.isEmpty(recordInfo.getImage5())) {
            urlList.add(recordInfo.getImage5());
        }
        if (!TextUtils.isEmpty(recordInfo.getImage6())) {
            urlList.add(recordInfo.getImage6());
        }
        setGridView(urlList);
        if (!TextUtils.isEmpty(recordInfo.getMaterial())) {
            String[] josn = recordInfo.getMaterial().split(":");
            String jsonstring = "";
            for (int i = 0; i < josn.length; i++) {
                jsonstring += josn[i] + "\n";
            }
            cailiaotext.setText(jsonstring);
        }


        remark_et.setText(recordInfo.getRemark());
        guzangdidian.setText(taskInfo.getDistrictTown() + "," + taskInfo.getStreetNo() + "," + taskInfo.getDefailedAdderss());
        wentimiaoshu.setText(taskInfo.getExcpDesc());
        baoxiuren.setText(taskInfo.getReportName());
        id.setText(taskInfo.getAppNo());

    }

    /**
     * 设置GirdView参数，绑定数据
     */
    private void setGridView(List<String> urlList) {

        int size = urlList.size();
        if (size == 0) {
            emptyview.setVisibility(View.GONE);
            horizontalScrollView.setVisibility(View.GONE);
        } else {
            horizontalScrollView.setVisibility(View.VISIBLE);
            emptyview.setVisibility(View.GONE);
        }
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

        GridViewShowAdapter adapter = new GridViewShowAdapter(HistoryTaskInfoActivity.this,
                urlList);
        gridView.setAdapter(adapter);
    }
}
