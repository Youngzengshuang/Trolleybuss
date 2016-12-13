package com.gjyf.trolleybus.trolleybuss.utils;

/**
 * 作者：Yang on 2016/10/20 15:46
 */

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.Toast;

import com.iflytek.autoupdate.IFlytekUpdate;
import com.iflytek.autoupdate.IFlytekUpdateListener;
import com.iflytek.autoupdate.UpdateConstants;
import com.iflytek.autoupdate.UpdateErrorCode;
import com.iflytek.autoupdate.UpdateInfo;
import com.iflytek.autoupdate.UpdateType;

/**
 * @author lianwanfei
 *         用于自动更新功能
 */
public class UpdateUtils {

    private IFlytekUpdate updManager;
    private Toast mToast;
    private Context context;

    /**
     *
     */
    public UpdateUtils(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
        init();
    }

    private void init() {
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        updManager = IFlytekUpdate.getInstance(context);
        updManager.setDebugMode(true);
        updManager.setParameter(UpdateConstants.EXTRA_WIFIONLY, "false");
        updManager.setParameter(UpdateConstants.EXTRA_STYLE, UpdateConstants.UPDATE_UI_DIALOG);
    }

    /**
     * 如果是主动请求(如更多界面)，则initiatively传入true
     *
     * @param initiatively
     */
    public void update(boolean initiatively) {
        if (initiatively)
            updManager.autoUpdate(context, updateListener);
        else
            updManager.autoUpdate(context, null);
    }

    //升级版本
    private IFlytekUpdateListener updateListener = new IFlytekUpdateListener() {

        @Override
        public void onResult(int errorcode, UpdateInfo result) {

            if (errorcode == UpdateErrorCode.OK && result != null) {
                if (result.getUpdateType() == UpdateType.NoNeed) {
                    //取得当前版本
                    PackageManager packageManager = context.getPackageManager();
                    PackageInfo packInfo;
                    String mVersion = "";
                    try {
                        packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                        mVersion = packInfo.versionName;
                    } catch (NameNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    showTip("当前版本为:" + mVersion + ",已经是最新版本！");
                    return;
                }
                updManager.showUpdateInfo(context, result);
            } else {
                showTip("无网络或无WIFI");
            }
        }
    };

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }
}
