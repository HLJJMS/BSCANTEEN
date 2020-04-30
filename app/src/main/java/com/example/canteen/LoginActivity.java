package com.example.canteen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.canteen.bean.LoginBean;
import com.google.gson.Gson;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private static final int TIME_EXIT = 2000;
    @BindView(R.id.studen)
    TextView studen;
    @BindView(R.id.teacher)
    TextView teacher;
    @BindView(R.id.cooker)
    TextView cooker;
    @BindView(R.id.ll_type)
    LinearLayout llType;
    @BindView(R.id.tv_type)
    TextView tvType;
    private long mBackPressed;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.login)
    QMUIRoundButton login;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.container)
    ConstraintLayout container;
    SharedPreferences sp = getSharedPreferences("Logindb", MODE_PRIVATE);
    SharedPreferences.Editor editor = sp.edit();
    boolean isLogin = true;
    String tel, pws, status;
    Context context;
    int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        context = this;
        editor.putBoolean("save", false);
        editor.commit();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin) {
                    if (username.getText().equals("") || password.getText().equals("")) {
                        Toast.makeText(LoginActivity.this, "账号密码不能为空", Toast.LENGTH_LONG).show();
                    } else {
                        pws = password.getText().toString();
                        tel = username.getText().toString();
                        LoginGet loginGet = new LoginGet();
                        loginGet.execute();

                    }
                } else {
                    //注册
                    if (username.getText().equals("") || password.getText().equals("")) {
                        Toast.makeText(LoginActivity.this, "账号密码不能为空", Toast.LENGTH_LONG).show();
                    } else {
                        pws = password.getText().toString();
                        tel = username.getText().toString();
                        RegisteGet registeGet = new RegisteGet();
                        registeGet.execute();
                    }
                }
            }
        });
        tvType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin) {
                    isLogin = false;
                    tvType.setText("登录");
                    llType.setVisibility(View.VISIBLE);
                } else {
                    isLogin = true;
                    llType.setVisibility(View.GONE);
                    tvType.setText("注册");
                }
            }
        });

        studen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 0;
                setRadio();
            }
        });
        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 1;
                setRadio();
            }
        });
        cooker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 2;
                setRadio();
            }
        });


    }


    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_EXIT > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "再点击一次返回退出程序", Toast.LENGTH_SHORT).show();
            mBackPressed = System.currentTimeMillis();

        }
    }

    private int txtTure = 0xffffffff;
    private int txtFalse = 0xffFFD700;

    private void setRadio() {
        switch (type) {
            case 0:
                studen.setTextColor(txtTure);
                teacher.setTextColor(txtFalse);
                cooker.setTextColor(txtFalse);
                studen.setBackgroundResource(R.drawable.edit_back_solid);
                teacher.setBackgroundResource(R.drawable.edit_back);
                cooker.setBackgroundResource(R.drawable.edit_back);
                status = Api.STUDENT;
                break;
            case 1:
                studen.setTextColor(txtFalse);
                teacher.setTextColor(txtTure);
                cooker.setTextColor(txtFalse);
                studen.setBackgroundResource(R.drawable.edit_back);
                teacher.setBackgroundResource(R.drawable.edit_back_solid);
                cooker.setBackgroundResource(R.drawable.edit_back);
                status = Api.TEACHER;
                break;
            case 2:
                studen.setTextColor(txtFalse);
                teacher.setTextColor(txtFalse);
                cooker.setTextColor(txtTure);
                studen.setBackgroundResource(R.drawable.edit_back);
                teacher.setBackgroundResource(R.drawable.edit_back);
                cooker.setBackgroundResource(R.drawable.edit_back_solid);
                status = Api.COOKER;
                break;
        }
    }


    class LoginGet extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String s = "";
            OkHttpClient okHttpClient = new OkHttpClient();

            MultipartBody.Builder urlBuilder = new MultipartBody.Builder();
            urlBuilder.addFormDataPart("tel", tel);
            urlBuilder.addFormDataPart("pws", pws);
            Request request = new Request.Builder().url(Api.BASEURL + Api.LOGIN).post(urlBuilder.build()).build();
            try {
                Response response = okHttpClient.newCall(request).execute();
                s = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            LoginBean bean = new Gson().fromJson(s, LoginBean.class);
            editor.putString("user", username.getText().toString());
            editor.putString("password", password.getText().toString());
            editor.putString("token", bean.getToken());
            editor.putString("type", bean.getStatus());
            Api.TOKEN = bean.getToken();
            Api.TYPE = bean.getStatus();
            editor.putBoolean("save", true);
            editor.commit();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }


    class RegisteGet extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String s = "";
            OkHttpClient okHttpClient = new OkHttpClient();

            MultipartBody.Builder urlBuilder = new MultipartBody.Builder();
            urlBuilder.addFormDataPart("tel", tel);
            urlBuilder.addFormDataPart("pws", pws);
            urlBuilder.addFormDataPart("status", status);
            Request request = new Request.Builder().url(Api.BASEURL + Api.REGISTE).post(urlBuilder.build()).build();
            try {
                Response response = okHttpClient.newCall(request).execute();
                s = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (s) {
                case "success":
                    isLogin = true;
                    llType.setVisibility(View.GONE);
                    tvType.setText("注册");
                    username.setText("");
                    password.setText("");
                    Toast.makeText(context, "成功，请重新登录", Toast.LENGTH_LONG).show();
                    break;
                case "fail":
                    Toast.makeText(context, " 注册失败", Toast.LENGTH_LONG).show();
                    break;
                case "exist":
                    Toast.makeText(context, "账号存在", Toast.LENGTH_LONG).show();
                    break;
                case "update":
                    Toast.makeText(context, "update ", Toast.LENGTH_LONG).show();
                    break;
            }


        }
    }
}
