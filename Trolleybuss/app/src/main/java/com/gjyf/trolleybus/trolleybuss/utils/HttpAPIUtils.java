package com.gjyf.trolleybus.trolleybuss.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;

import com.baidu.location.BDLocation;
import com.gjyf.app.citylight.MyApplication;
import com.gjyf.app.citylight.MyConstants;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * 作者：Yang on 2016/11/28 14:41
 */
public class HttpAPIUtils {


    public static void HttpLocationPost(Context context, String url, String la, String lo) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("longItude", lo);
        params.addBodyParameter("latItude", la);
        params.addBodyParameter("uid", SharedPreferencesUtils.getString(context, "uid"));
        params.addBodyParameter("rmerNo", SharedPreferencesUtils.getString(context, "szImei"));
        MyApplication.getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                    }
                });

    }

    public static void HttpPost(final Handler handler, String url, RequestParams params) {
        final String[] result = {null};
        MyApplication.getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {

                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Message message = Message.obtain();
                        message.what = MyConstants.SUCCESS;
                        message.obj = responseInfo.result.toString();
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Message message = Message.obtain();
                        message.what = MyConstants.FAILURE;
                        message.obj = msg;
                        handler.sendMessage(message);
                    }
                });

    }

    public static void HttpPostAndShow(final Handler handler, String url, RequestParams params) {
        final String[] result = {null};
        MyApplication.getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {

                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Message message = Message.obtain();
                        message.what = 600;
                        message.obj = responseInfo.result.toString();
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Message message = Message.obtain();
                        message.what = MyConstants.FAILURE;
                        message.obj = msg;
                        handler.sendMessage(message);
                    }
                });

    }

    public static void TaskHistoryHttpPost(final Handler handler, String url, RequestParams params) {
        final String[] result = {null};
        MyApplication.getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {

                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Message message = Message.obtain();
                        message.what = 300;
                        message.obj = responseInfo.result.toString();
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Message message = Message.obtain();
                        message.what = MyConstants.FAILURE;
                        message.obj = msg;
                        handler.sendMessage(message);
                    }
                });

    }

    public static void HttpGet(final Handler handler, String url) {

        RequestParams params = new RequestParams();
        MyApplication.getHttpUtils().send(HttpRequest.HttpMethod.GET, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {

                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Message message = Message.obtain();
                        message.what = MyConstants.GETSUCCESS;
                        message.obj = responseInfo.result.toString();
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Message message = Message.obtain();
                        message.what = MyConstants.FAILURE;
                        message.obj = msg;
                        handler.sendMessage(message);
                    }
                });

    }
}
