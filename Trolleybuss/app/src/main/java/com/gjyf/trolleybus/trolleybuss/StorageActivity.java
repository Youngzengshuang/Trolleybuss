package com.gjyf.trolleybus.trolleybuss;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gjyf.app.citylight.MyConstants;
import com.gjyf.trolleybus.trolleybuss.adapter.ApprovalAdapter;
import com.gjyf.trolleybus.trolleybuss.adapter.StorageInFoAdapter;
import com.gjyf.trolleybus.trolleybuss.bean.ApprovalOutGoing;
import com.gjyf.trolleybus.trolleybuss.bean.StorageInfo;
import com.gjyf.trolleybus.trolleybuss.myview.MyProgressDialog;
import com.gjyf.trolleybus.trolleybuss.utils.HttpAPIUtils;
import com.gjyf.trolleybus.trolleybuss.utils.SharedPreferencesUtils;
import com.gjyf.trolleybus.trolleybuss.utils.ToastUtil;
import com.lidroid.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 库房信息
 */
public class StorageActivity extends BaseActivity {
    private ListView storagelist;
    private StorageInFoAdapter adapter;
    private LinearLayout search;
    private AutoCompleteTextView autoCompleteTextView;
    private List<StorageInfo> storagestrings = new ArrayList<>();
    private List<StorageInfo> storageInfossearch = new ArrayList<>();
    private TextView canpinzongshu;
    private MyProgressDialog dialog;
    String namestring = "";
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 400:
                    String st = msg.obj.toString();
                    Log.v("yzs", st);
                    if (!TextUtils.isEmpty(st)) {
                        storagestrings = (ArrayList<StorageInfo>) JSON.parseArray(st, StorageInfo.class);
                        if (storagestrings != null) {
                            if (storagestrings.size() != 0) {
                                namestring = "";
                                int sum = 0;
                                int sumtype = storagestrings.size();
                                for (StorageInfo info : storagestrings) {
                                    sum += Integer.parseInt(info.getSumnum());
                                    namestring += info.getName() + ",";
                                }
                                if (namestring.length() > 0) {
                                    namestring.substring(0, namestring.length() - 1);
                                }

                                canpinzongshu.setText("产品总数:" + sumtype + "款 ");
                                Collections.sort(storagestrings, new Comparator<StorageInfo>() {
                                    @Override
                                    public int compare(StorageInfo storageInfo, StorageInfo t1) {
                                        StorageInfo storageInfo1 = storageInfo;
                                        StorageInfo storageInfo2 = t1;
                                        if (TextUtils.isEmpty(storageInfo1.getNum()) || storageInfo1.getNum() == null) {
                                            storageInfo1.setNum("0");
                                        }
                                        if (TextUtils.isEmpty(storageInfo2.getNum()) || storageInfo2.getNum() == null) {
                                            storageInfo2.setNum("0");
                                        }
                                        Integer s1 = (int) ((Double.parseDouble(storageInfo1.getNum()) / Double.parseDouble(storageInfo1.getSumnum())) * 100);
                                        Integer s2 = (int) ((Double.parseDouble(storageInfo2.getNum()) / Double.parseDouble(storageInfo2.getSumnum())) * 100);
                                        return s1.compareTo(s2);
                                    }
                                });
                                adapter = new StorageInFoAdapter(StorageActivity.this, storagestrings);
                                storagelist.setAdapter(adapter);
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm != null) {
                                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                                }
                            }
                        }

                    }
                    dialog.dismiss();
                    break;
                case 404:
                    dialog.dismiss();
                    ToastUtil.show("请求超时");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        initview();
        initlistener();
    }

    private void initview() {
        search = (LinearLayout) findViewById(R.id.search);
        dialog = MyProgressDialog.createDialog(this);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.input_info);
        canpinzongshu = (TextView) findViewById(R.id.canpinzongshu);
        ImageView back = (ImageView) findViewById(R.id.back);
        TextView top_text = (TextView) findViewById(R.id.main_top_text);
        top_text.setText("库房信息");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                StorageActivity.this.finish();
            }
        });
        storagelist = (ListView) findViewById(R.id.storagelist);
        storagestrings = new ArrayList<>();

        autoCompleteTextView.clearFocus();

        RequestParams params = new RequestParams();
        params.addBodyParameter("uid", "");
        HttpAPIUtils.HttpPost(handler, MyConstants.STORAGE, params);
        dialog.show();

    }


    private void initlistener() {
        //适配器初始化
        adapter = new StorageInFoAdapter(this, storagestrings);
        storagelist.setAdapter(adapter);

        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String streetjson = null;

                try {
                    streetjson = namestring;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(streetjson)) {
                    String[] temp = streetjson.split(",");
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(StorageActivity.this,
                            R.layout.street_name, temp);
                    autoCompleteTextView.setAdapter(adapter);
                }

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //搜索材料信息
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = autoCompleteTextView.getText().toString();
                storageInfossearch.clear();

                if (!TextUtils.isEmpty(string)) {
                    for (StorageInfo info : storagestrings) {
                        if (TextUtils.isEmpty(info.getType())) {
                            info.setType("");
                        }
                        if (info.getName().contains(string) || info.getType().contains(string)) {
                            storageInfossearch.add(info);
                        }
                    }

                } else {
                    storageInfossearch.addAll(storagestrings);

                }

                Collections.sort(storageInfossearch, new Comparator<StorageInfo>() {
                    @Override
                    public int compare(StorageInfo storageInfo, StorageInfo t1) {
                        Integer s1 = (int) ((Double.parseDouble(storageInfo.getNum()) / Double.parseDouble(storageInfo.getSumnum())) * 100);
                        Integer s2 = (int) ((Double.parseDouble(t1.getNum()) / Double.parseDouble(t1.getSumnum())) * 100);
                        return s1.compareTo(s2);
                    }
                });
                adapter = new StorageInFoAdapter(StorageActivity.this, storageInfossearch);
                storagelist.setAdapter(adapter);
                namestring = "";
                int sum = 0;
                int sumtype = storageInfossearch.size();
                for (StorageInfo info : storagestrings) {
                    sum += Integer.parseInt(info.getSumnum());
                    namestring += info.getName() + ",";
                }
                if (namestring.length() > 0) {
                    namestring.substring(0, namestring.length() - 1);
                }

                canpinzongshu.setText("产品总数:" + sumtype + "款");
            }
        });

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        super.onDestroy();

    }
}
