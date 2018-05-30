package com.zhaoweihao.architechturesample.course;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhaoweihao.architechturesample.R;
import com.zhaoweihao.architechturesample.data.RestResponse;
import com.zhaoweihao.architechturesample.leave.LeaveSubmit;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.zhaoweihao.architechturesample.util.HttpUtil.sendPostRequest;
import static com.zhaoweihao.architechturesample.util.Utils.log;

public class SendNoti extends AppCompatActivity implements View.OnClickListener {
    private static final Class thisClass = SendNoti.class;
    //课程编号、公告的截止日期、增加的截止日期的时间（一星期，两星期、三星期、四星期、一个月、两个月、三个月）
    TextView tv_sendnoti_coursenum, tv_sendnoti_date, tv_sendnoti_order;
            //返回到主页
    ImageView iv_sendnotireturntohome;
    //公告的内容
    EditText et_sendnoti_content;
    //提交发送按钮
    Button bt_sendnotisubmit;
    //课程编号,截止日期，增加长度
    int courseNum, expireTime, expireDuration;
    //显示选择截止日期的数组
    String expires[] = new String[7];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_noti);
        initViews();
        setCurrentCoursename();

        setTvSendNotDate(expireTime);
    }

    private void initViews() {
        expireTime = 0;
        expireDuration = 0;
        tv_sendnoti_coursenum = (TextView) findViewById(R.id.tv_sendnoti_coursenum);
        tv_sendnoti_date = (TextView) findViewById(R.id.tv_sendnoti_date);
        tv_sendnoti_order = (TextView) findViewById(R.id.tv_sendnoti_order);
        iv_sendnotireturntohome=(ImageView) findViewById(R.id.iv_sendnotireturntohome);

        et_sendnoti_content = (EditText) findViewById(R.id.et_sendnoti_content);
        bt_sendnotisubmit = (Button) findViewById(R.id.bt_sendnotisubmit);


        tv_sendnoti_coursenum.setOnClickListener(this);
        tv_sendnoti_date.setOnClickListener(this);
        tv_sendnoti_order.setOnClickListener(this);
        iv_sendnotireturntohome.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_sendnotireturntohome:
                finish();
                break;
            case R.id.bt_sendnotisubmit:
               // submitNoti();
                break;
            case R.id.tv_sendnoti_date:
                new AlertDialog.Builder(SendNoti.this).setItems(expires, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        expireTime = i;
                        setTvSendNotDate(expireTime);
                        tv_sendnoti_date.setText("截止日期："+setTvSendNotDateSetText());
                    }
                }).create().show();
                break;
            case R.id.tv_sendnoti_order:
                String addDays[]={"不增加天数", "已增加7天后截止", "已增加21天后截止", "已增加28天后截止", "已增加30天后截止", "已增加31天后截止"};
                new AlertDialog.Builder(SendNoti.this).setItems(addDays, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tv_sendnoti_order.setText(addDays[i]);
                        switch (i) {
                            case 0:
                                expireDuration = 0;
                                break;
                            case 1:
                                expireDuration = 7;
                                break;
                            case 2:
                                expireDuration = 21;
                                break;
                            case 3:
                                expireDuration = 28;
                                break;
                            case 4:
                                expireDuration = 30;
                                break;
                            case 5:
                                expireDuration = 31;
                                break;
                        }
                        tv_sendnoti_date.setText("截止日期："+setTvSendNotDateSetText());
                    }
                }).create().show();
                break;
            case R.id.et_sendnoti_content:
                break;
        }
    }

    public void setCurrentCoursename() {
        tv_sendnoti_coursenum.setText("当前课程：first head java");
    }



    public void setTvSendNotDate(int currentSelected) {
        LeaveSubmit leaveSubmit1 = new LeaveSubmit();
        for (int date = 0, order = 0; date < 7; date++) {
            if(order==currentSelected){
            expires[order++] = leaveSubmit1.getDateString(LeaveSubmit.getNextkDay(new Date(), date))+"               已选";}
            else {
                expires[order++] = leaveSubmit1.getDateString(LeaveSubmit.getNextkDay(new Date(), date));
            }

        }
    }
    public String setTvSendNotDateSetText(){
        int expireTime1=expireTime+expireDuration;
        return  new LeaveSubmit().getDateString(LeaveSubmit.getNextkDay(new Date(),expireTime1));
    }
    public void submitNoti() {

        com.zhaoweihao.architechturesample.data.course.SendNoti sendNoti=new com.zhaoweihao.architechturesample.data.course.SendNoti();
        sendNoti.setCourseId(4);
        sendNoti.setContent(et_sendnoti_content.getText().toString());
        sendNoti.setDate(new LeaveSubmit().getNowDateShort(LeaveSubmit.getNextkDay(new Date(),expireTime+expireDuration)));
        sendNoti.setTecId("26");

        //转换成json数据，借助gson
        String json = new Gson().toJson(sendNoti);
        log(thisClass, json);
        //发送post请求注册
        String after = "leave/submit";
        sendPostRequest(after, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //发送请求失败，有可能是网络断了或者其他的
                log(thisClass, "发送请求失败，请检查网络");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                //Gson解析数据 json -> 对象
                try {
                    RestResponse restResponse = new Gson().fromJson(body, RestResponse.class);
                    if (restResponse == null) {
                        runOnUiThread(() -> Toast.makeText(SendNoti.this, "发送请求失败！", Toast.LENGTH_SHORT).show());
                        return;
                    }
                    log(thisClass, restResponse.getCode().toString());
                    //状态码500表示失败，打印错误信息
                    if (restResponse.getCode() == 500) {
                        log(thisClass, restResponse.getMsg());
                        runOnUiThread(() -> Toast.makeText(SendNoti.this, "发送请求失败！"+restResponse.getMsg(), Toast.LENGTH_SHORT).show());
                    }
                    //200代表成功，打印成功信息
                    if (restResponse.getCode() == 200) {
                        log(thisClass, "已成功发送");
                        runOnUiThread(() -> {
                            Toast.makeText(SendNoti.this, "已成功发送！", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                log(thisClass, body);
            }
        });

    }
}