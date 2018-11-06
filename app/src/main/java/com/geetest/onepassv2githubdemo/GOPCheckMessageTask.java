package com.geetest.onepassv2githubdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by 谷闹年 on 2018/10/15.
 * CheckMessage的请求异步任务
 */
public class GOPCheckMessageTask extends AsyncTask<String, Void, String> {

    /**
     * 服务器配置的checkMessageUrl的接口
     */
    public static final String GOP_CHECK_MSG = "https://onepass.geetest.com/v2.0/check_message";

    public GOPCheckMessageTask(JSONObject jsonObject, Activity context) {
        this.jsonObject = jsonObject;
        this.context = context;
    }


    private JSONObject jsonObject;
    /**
     * 页面的activity
     */
    private Activity context;

    @Override
    protected String doInBackground(String... params) {
        if (isCancelled()) {
            return null;
        }
        return HttpUtils.requestNetwork(GOP_CHECK_MSG, jsonObject, null);
    }

    @Override
    protected void onPostExecute(String s) {
        Log.i(MainActivity.TAG, "CheckMessage 请求结束");
        if (isCancelled()) {
            return;
        }
        if (TextUtils.isEmpty(s)) {
            Log.i(MainActivity.TAG, "CheckMessage 请求错误");
            Utils.toastUtil(context, "CheckMessage 请求错误");
            return;
        }
        Log.i(MainActivity.TAG, "CheckMessage 请求成功" + s);
        try {
            JSONObject jsonObject=new JSONObject(s);
            if ((jsonObject.getBoolean("result"))) {
                context.startActivity(new Intent(context.getApplicationContext(), SuccessActivity.class));
            } else {
                Log.i(MainActivity.TAG, "CheckMessage 请求错误");
                Utils.toastUtil(context, "CheckMessage 请求错误");
            }
        } catch (JSONException e) {
            Log.i(MainActivity.TAG, "CheckMessage 请求错误");
            Utils.toastUtil(context, "CheckMessage 请求错误");
        }


    }
}
