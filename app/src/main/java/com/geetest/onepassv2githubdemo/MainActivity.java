package com.geetest.onepassv2githubdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geetest.onepassv2.OnePassHelper;
import com.geetest.onepassv2.listener.OnePassListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "OnePassV2";

    /**
     * 请求成功的响应值
     */
    public static final int SUCCESS_CODE = 200;
    /**
     * 控件
     */
    private EditText editText;
    private Button button;
    private TextView textView;
    private ImageView imageView;
    /**
     * onepass的监听类
     */
    private OnePassListener onePassListener;

    /**
     * 服务器配置的verifyUrl接口<>需要用到服务SDK</>
     */
    public static final String GOP_VERIFY_URL = "";
    /**
     * 配置的网关customid<>需要申请ID</>
     */
    private static final String CUSTOM_ID = "";
    /**
     * 进度条
     */
    private ProgressDialog progressDialog;
    /**
     * 校验网关的异步任务
     */
    private GOPCheckGatewayTask checkGatewayTask;
    /**
     * 发送短信的异步任务
     */
    private GOPSendMessageTask sendMessageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawable(null);
        TextView textView = (TextView) findViewById(R.id.gtop_title_name);
        textView.setText(new GTMTextUtils().getText());
        init();
        initGop();
    }

    /**
     * 初始化控件
     */

    private void init() {
        progressDialog = new DemoProgress(this);
        editText = (EditText) findViewById(R.id.et);
        button = (Button) findViewById(R.id.btn);
        textView = (TextView) findViewById(R.id.gtm_check_num_tv);
        imageView = (ImageView) findViewById(R.id.gtm_back_iv);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检测手机号格式
                if (Utils.chargePhoneNum(editText.getText().toString())) {
                    textView.setVisibility(View.INVISIBLE);
                    editText.getBackground().clearColorFilter();
                    if (!Utils.haveIntent(MainActivity.this)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("检测到未开启移动数据，建议手动开启进行操作");
                        builder.setTitle("提示");
                        builder.setPositiveButton("好的,我这就去开启", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("不好，我就不开", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                progressDialog = ProgressDialog.show(MainActivity.this, null, "验证加载中", true, true);
                                // 接入onepass
                                OnePassHelper.with().getToken(editText.getText().toString(), CUSTOM_ID, onePassListener);
                            }
                        });
                        builder.create().show();
                    } else {
                        progressDialog = ProgressDialog.show(MainActivity.this, null, "验证加载中", true, true);
                        // 接入onepass
                        OnePassHelper.with().getToken(editText.getText().toString(), CUSTOM_ID, onePassListener);
                    }

                } else {
                    textView.setVisibility(View.VISIBLE);
                    editText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                }
            }
        });
    }

    private void initGop() {
        OnePassHelper.with().init(MainActivity.this);
        onePassListener = new OnePassListener() {

            @Override
            public void onTokenFail(JSONObject jsonObject) {
                Log.i(TAG, jsonObject.toString());
                Log.i(TAG, "开始发短信");
                String phone = editText.getText().toString();
                sendMessageTask = new GOPSendMessageTask(MainActivity.this, phone,progressDialog);
                sendMessageTask.execute();

            }

            @Override
            public void onTokenSuccess(final JSONObject jsonObject) {
                Log.i(TAG, jsonObject.toString());
                Log.i(TAG, "开始请求checkgateway");
                checkGatewayTask = new GOPCheckGatewayTask(MainActivity.this, jsonObject, editText.getText().toString(),progressDialog);
                checkGatewayTask.execute(GOP_VERIFY_URL);
            }
        };
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        //TODO 必须调用，释放资源，销毁的时候执行
        OnePassHelper.with().cancel();
    }

}
