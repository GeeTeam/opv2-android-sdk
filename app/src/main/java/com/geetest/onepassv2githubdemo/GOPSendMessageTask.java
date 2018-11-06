package com.geetest.onepassv2githubdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 谷闹年 on 2018/10/15.
 * SendMessage的请求异步任务
 */
public class GOPSendMessageTask extends AsyncTask<String, Void, String> {

    /**
     * 发短信请求
     */
    public static final String SEND_MESSAGE_URL = "https://onepass.geetest.com/v2.0/send_message";
    private ProgressDialog progressDialog;
    /**
     * 页面的activity
     */
    private Activity context;
    /**
     * 手机号
     */
    private String phone;


    public GOPSendMessageTask(Activity context, String phone, ProgressDialog progressDialog) {
        this.context = context;
        this.phone = phone;
        this.progressDialog = progressDialog;
    }

    @Override
    protected String doInBackground(String... params) {
        if (isCancelled()) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(MainActivity.TAG, jsonObject.toString());
        return HttpUtils.requestNetwork(SEND_MESSAGE_URL, jsonObject, null);
    }

    @Override
    protected void onPostExecute(String s) {
        Log.i(MainActivity.TAG, "SendMessage 请求结束");
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (isCancelled()) {
            return;
        }
        if (TextUtils.isEmpty(s)) {
            Log.i(MainActivity.TAG, "SendMessage 请求错误");
            Utils.toastUtil(context.getApplicationContext(), "SendMessage 请求错误");
            return;
        }
        Log.i(MainActivity.TAG, "SendMessage 请求成功" + s);
        try {
            JSONObject jsonObject=new JSONObject(s);
            Intent intent = new Intent(context.getApplicationContext(), SendMessageActivity.class);
            intent.putExtra("process_id", jsonObject.getString("process_id"));
            intent.putExtra("phone", phone);
            context.startActivity(intent);
        } catch (JSONException e) {
            Log.i(MainActivity.TAG, "SendMessage 请求错误"+e.toString());
            Utils.toastUtil(context.getApplicationContext(), "SendMessage 请求错误");
        }
    }
}
