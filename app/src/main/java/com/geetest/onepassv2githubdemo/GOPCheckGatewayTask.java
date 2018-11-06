package com.geetest.onepassv2githubdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by 谷闹年 on 2018/10/15.
 * CheckGateway的请求异步任务
 */
public class GOPCheckGatewayTask extends AsyncTask<String, Void, String> {
    /**
     * 手机号
     */
    private String phone;
    /**
     * 构造网关校验参数
     */
    private JSONObject jsonObject;
    private GOPSendMessageTask sendMessageTask;
    /**
     * 页面的activity
     */
    private Activity context;
    private ProgressDialog progressDialog;

    public GOPCheckGatewayTask(Activity context, JSONObject jsonObject, String phone, ProgressDialog progressDialog) {
        this.context = context;
        this.jsonObject = jsonObject;
        this.phone = phone;
        this.progressDialog = progressDialog;
    }


    @Override
    protected String doInBackground(String... params) {
        if (isCancelled()) {
            return null;
        }
        return HttpUtils.requestNetwork(params[0], jsonObject, null);
    }

    @Override
    protected void onPostExecute(String s) {
        Log.i(MainActivity.TAG, "CheckGateway 请求结束");
        if (isCancelled()) {
            return;
        }
        if (TextUtils.isEmpty(s)) {
            Log.e(MainActivity.TAG, "CheckGateway 校验异常");
            Log.i(MainActivity.TAG, "CheckGateway 开始发短信");
            sendMessageTask = new GOPSendMessageTask(context, phone,progressDialog);
            sendMessageTask.execute();
            return;
        }
        Log.i(MainActivity.TAG, "CheckGateway 请求成功" + s);
            try {
            JSONObject jsonObject = new JSONObject(s);
            int status = jsonObject.getInt("status");
            if (status == MainActivity.SUCCESS_CODE && "0".equals(jsonObject.getString("result"))) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                context.startActivity(new Intent(context.getApplicationContext(), SuccessActivity.class));
            } else {
                Log.e(MainActivity.TAG, "CheckGateway 校验失败" + s);
                Log.i(MainActivity.TAG, "CheckGateway 开始发短信");
                sendMessageTask = new GOPSendMessageTask(context, phone,progressDialog);
                sendMessageTask.execute();
            }


        } catch (Exception e) {
            Log.e(MainActivity.TAG, "CheckGateway 校验失败" + s);
            Log.i(MainActivity.TAG, "CheckGateway 开始发短信");
            sendMessageTask = new GOPSendMessageTask(context, phone,progressDialog);
            sendMessageTask.execute();
        }
    }
}
