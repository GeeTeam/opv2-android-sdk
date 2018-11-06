package com.geetest.onepassv2githubdemo;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendMessageActivity extends AppCompatActivity {
    private TextView textView, tvphone;
    private EditText editText;
    private String custom, phone, process_id;
    private ImageView imageView;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        getWindow().setBackgroundDrawable(null);
        TextView tvTitle = (TextView) findViewById(R.id.tv_msg_title);
        tvTitle.setText(new GTMTextUtils().getText());
        init();
        countDownTimer = new CountDownTimer(60000 + 500, 1000) {

            @Override

            public void onTick(long millisUntilFinished) {

                textView.setText("(" + millisUntilFinished / 1000 + "秒)");

            }

            @Override
            public void onFinish() {
                textView.setText("(0秒)");
            }
        }.start();
    }

    private void init() {
        textView = (TextView) findViewById(R.id.tv_msg);
        tvphone = (TextView) findViewById(R.id.tv_phone);
        imageView = (ImageView) findViewById(R.id.gtm_back_iv);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
        editText = (EditText) findViewById(R.id.et_msg);
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        process_id = intent.getStringExtra("process_id");
        tvphone.setText(phone);
        //进行短信验证
        findViewById(R.id.btn_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
               JSONObject jsonObject= new JSONObject();
                try {
                    jsonObject.put("message_number", editText.getText().toString());
                    jsonObject.put("process_id", process_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i(MainActivity.TAG,jsonObject.toString());
                checkMessageTask = new GOPCheckMessageTask(jsonObject, SendMessageActivity.this);
                checkMessageTask.execute();
            }
        });
    }

    private GOPCheckMessageTask checkMessageTask;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
