package com.gjyf.trolleybus.trolleybuss;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.gjyf.trolleybus.trolleybuss.bean.CaiLiaoBean;
import com.gjyf.trolleybus.trolleybuss.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 选择地址对话框
 *
 * @author wtw
 * @create_date 2015-5-8下午4:50:26
 */
public class SelectCitiesDialogActivity extends Activity {
    public static final int SELECT_CITY = 1002;
    private String[] strings0 = new String[]{"选择材料", "手录"};
    private JSONObject mJsonObj;//材料信息json的格式保存，解析 完成后赋值为null
    private String[] mProvinceDatas;//所有省
    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();//key - 省 value - 市s
    private Map<String, String[]> mAreaDatasMap = new HashMap<String, String[]>();//key - 市 values - 区s
    private String mCurrentProviceName;//当前省的名称
    private String mCurrentAreaName = "";//当前区的名称
    private Spinner mSpProvince;//省下拉控件
    private Spinner mSpCity;//城市下拉控件
    private Spinner mSpArea;//地区下拉控件
    private Spinner switchspinner;
    private ArrayAdapter<String> adapter0 = null; //
    private ArrayAdapter<String> mProvinceAdapter;//省份数据适配器
    private ArrayAdapter<String> mCityAdapter;//城市数据适配器
    private ArrayAdapter<String> mAreaAdapter;//省份数据适配器
    private EditText mEtDetailAddre;//地址的详细信息
    private String mAddress;//原有的地址
    private String[] mAddressList;//原有地址，用空格进行切分地址
    private Boolean isFirstLord = true;//判断是不是最近进入对话框
    private Boolean ifSetFirstAddress = true;//判断是否已经设置了，初始的详细地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_select_citys_dialog);
        initJsonData();
        initDatas();
        initView();
    }

    private void initView() {
        switchspinner = (Spinner) findViewById(R.id.switchspinner);
        mEtDetailAddre = (EditText) findViewById(R.id.et_detailAddre);
        mSpProvince = (Spinner) findViewById(R.id.spProvince);
        mSpCity = (Spinner) findViewById(R.id.spCity);
        mSpArea = (Spinner) findViewById(R.id.spArea);
        adapter0 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, strings0);
        switchspinner.setAdapter(adapter0);

        switchspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    mSpProvince.setVisibility(View.VISIBLE);
                    mSpCity.setVisibility(View.VISIBLE);
                    mSpArea.setVisibility(View.VISIBLE);

                } else if (i == 1) {
                    mSpProvince.setVisibility(View.GONE);
                    mSpCity.setVisibility(View.GONE);
                    mSpArea.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        int selectPro = 0;//有传输数据时
        mAddress = getIntent().getStringExtra("address");//通过传入的地区的信息进行初始化，控件数据
        if (mAddress != null && !mAddress.equals("")) {
            mAddressList = mAddress.split(" ");
        }

        /**
         * 设置，省，市，县，的适配器，进行动态设置其中的值  begin
         */
        mProvinceAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);
        for (int i = 0; i < mProvinceDatas.length; i++) {
            //当有传入值时，进行判断选中的条目，设置默认值
            if (mAddress != null && !mAddress.equals("") && mAddressList.length > 0 && mAddressList[0].equals(mProvinceDatas[i])) {
                selectPro = i;
            }
            mProvinceAdapter.add(mProvinceDatas[i]);
        }
        mProvinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpProvince.setAdapter(mProvinceAdapter);
        mSpProvince.setSelection(selectPro);
        mCityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        mCityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpCity.setAdapter(mCityAdapter);
        mAreaAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        mAreaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpArea.setAdapter(mAreaAdapter);
        /**
         * 设置，省，市，县，的适配器，进行动态设置其中的值  end
         */
        setupViewsListener();
    }

    /**
     * 设置控件点击选择监听
     */
    private void setupViewsListener() {
        mSpProvince.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                mCurrentProviceName = arg0.getSelectedItem() + "";
                if (isFirstLord) {
                    if (mAddress != null && !mAddress.equals("") && mAddressList.length > 1 && mAddressList.length < 3) {
                        updateCitiesAndAreas(mCurrentProviceName, mAddressList[1], null);
                    } else if (mAddress != null && !mAddress.equals("") && mAddressList.length >= 3) {
                        updateCitiesAndAreas(mCurrentProviceName, mAddressList[1], mAddressList[2]);
                    } else {
                        updateCitiesAndAreas(mCurrentProviceName, null, null);
                    }
                } else {
                    updateCitiesAndAreas(mCurrentProviceName, null, null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        mSpCity.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (!isFirstLord) {
                    updateAreas(arg0.getSelectedItem(), null);
                } else {
                    if (mAddress != null && !mAddress.equals("") && mAddressList.length == 4) {
                        mEtDetailAddre.setText(mAddressList[3]);
                    }
                }
                isFirstLord = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        mSpArea.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                mCurrentAreaName = arg0.getSelectedItem() + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    /**
     * 根据当前的省，更新市和地区信息
     */
    private void updateCitiesAndAreas(Object object, Object city, Object myArea) {
        int selectPosition = 0;//当有数据时，进行匹配城市，默认选中
        String[] cities = mCitisDatasMap.get(object);
        mCityAdapter.clear();
        for (int i = 0; i < cities.length; i++) {
            if (city != null && city.toString().equals(cities[i])) {
                selectPosition = i;
            }
            mCityAdapter.add(cities[i]);
        }
        mCityAdapter.notifyDataSetChanged();
        if (city == null) {
            updateAreas(cities[0], null);
        } else {
            mSpCity.setSelection(selectPosition);
            updateAreas(city, myArea);
        }
    }

    /**
     * 根据当前的市，更新地区信息
     */
    private void updateAreas(Object object, Object myArea) {
        boolean isAreas = false;//判断第三个地址是地区还是详细地址
        int selectPosition = 0;//当有数据时，进行匹配地区，默认选中
        String[] area = mAreaDatasMap.get(object);
        mAreaAdapter.clear();
        if (area != null) {
            for (int i = 0; i < area.length; i++) {
                if (myArea != null && myArea.toString().equals(area[i])) {
                    selectPosition = i;
                    isAreas = true;
                }
                mAreaAdapter.add(area[i]);
            }
            mAreaAdapter.notifyDataSetChanged();
            mSpArea.setSelection(selectPosition);
        }
        //第三个地址是详细地址，并且是第一次设置edtext值，正好，地址的长度为3的时候，设置详细地址
        if (!isAreas && ifSetFirstAddress && mAddress != null && !mAddress.equals("") && mAddressList.length == 3) {
            mEtDetailAddre.setText(mAddressList[2]);
            ifSetFirstAddress = false;
        }
    }

    //点击确定后，进行将选择的值返回
    public void showChoose(View view) {
        if (!TextUtils.isEmpty(mEtDetailAddre.getText().toString().trim())) {
            if (Integer.parseInt(mEtDetailAddre.getText().toString().trim()) < 500) {
                Intent intent = new Intent();
                mCurrentAreaName = mSpArea.getSelectedItem() == null ? "" : mSpArea.getSelectedItem().toString() + " ";
//		intent.putExtra("address", mSpProvince.getSelectedItem() + " "+
//				mSpCity.getSelectedItem() + " "+
//				mCurrentAreaName+"数量"+
//				mEtDetailAddre.getText().toString());
                CaiLiaoBean caiLiaoBean = new CaiLiaoBean();
                caiLiaoBean.setName(mCurrentAreaName);
                caiLiaoBean.setNum(mEtDetailAddre.getText().toString().trim());
                intent.putExtra("address", caiLiaoBean);
                setResult(SELECT_CITY, intent);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                finish();
            } else {
                ToastUtil.show("申请数量过多！");
            }

        } else {
            ToastUtil.show("请选择材料数量！");
        }

    }

    /**
     * 解析整个Json对象，完成后释放Json对象的内存
     */
    private void initDatas() {
        try {
            JSONArray jsonArray = mJsonObj.getJSONArray("citylist");
            mProvinceDatas = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonP = jsonArray.getJSONObject(i);// 每个省的json对象
                String province = jsonP.getString("p");// 省名字

                mProvinceDatas[i] = province;

                JSONArray jsonCs = null;
                try {
                    /**
                     * Throws JSONException if the mapping doesn't exist or is
                     * not a JSONArray.
                     */
                    jsonCs = jsonP.getJSONArray("c");
                } catch (Exception e1) {
                    continue;
                }
                String[] mCitiesDatas = new String[jsonCs.length()];
                for (int j = 0; j < jsonCs.length(); j++) {
                    JSONObject jsonCity = jsonCs.getJSONObject(j);
                    String city = jsonCity.getString("n");// 市名字
                    mCitiesDatas[j] = city;
                    JSONArray jsonAreas = null;
                    try {
                        /**
                         * Throws JSONException if the mapping doesn't exist or
                         * is not a JSONArray.
                         */
                        jsonAreas = jsonCity.getJSONArray("a");
                    } catch (Exception e) {
                        continue;
                    }

                    String[] mAreasDatas = new String[jsonAreas.length()];// 当前市的所有区
                    for (int k = 0; k < jsonAreas.length(); k++) {
                        String area = jsonAreas.getJSONObject(k).getString("s");// 区域的名称
                        mAreasDatas[k] = area;
                    }
                    mAreaDatasMap.put(city, mAreasDatas);
                }

                mCitisDatasMap.put(province, mCitiesDatas);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mJsonObj = null;
    }

    /**
     * 从assert文件夹中读取省市区的json文件，然后转化为json对象
     */
    private void initJsonData() {
        try {
            StringBuffer sb = new StringBuffer();
            InputStream is = getAssets().open("city.json");
            int len = -1;
            byte[] buf = new byte[is.available()];
            while ((len = is.read(buf)) != -1) {
                sb.append(new String(buf, 0, len, "gb2312"));
            }
            is.close();
            mJsonObj = new JSONObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
