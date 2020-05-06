package com.example.canteen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
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
import butterknife.OnClick;
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
    @BindView(R.id.radio)
    RadioGroup radio;
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
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    boolean isLogin = true;
    String tel, pws, status;
    Context context;
    int type = 0;
   String depratmentId = "1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        sp = getSharedPreferences("Logindb", MODE_PRIVATE);
        editor = sp.edit();
        context = this;
        editor.putBoolean("save", false);
        editor.commit();

        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio1:
                        depratmentId="1";
                        break;
                    case R.id.radio2:
                        depratmentId="2";
                        break;
                    case R.id.radio3:
                        depratmentId="3";
                        break;
                    case R.id.radio4:
                        depratmentId="4";
                        break;
                    case R.id.radio5:
                        depratmentId="5";
                        break;
                }
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

    @OnClick({R.id.login, R.id.studen, R.id.teacher, R.id.cooker, R.id.ll_type, R.id.tv_type})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login:
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
                break;
            case R.id.studen:
                type = 0;
                setRadio();
                break;
            case R.id.teacher:
                type = 1;
                setRadio();
                break;
            case R.id.cooker:
                type = 2;
                setRadio();
                break;
            case R.id.ll_type:
                break;
            case R.id.tv_type:
                if (isLogin) {
                    isLogin = false;
                    tvType.setText("登录");
                    login.setText("注册");
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    llType.setVisibility(View.VISIBLE);
                    password.setHint("请输入名字");
                    radio.setVisibility(View.VISIBLE);
                } else {
                    isLogin = true;
                    llType.setVisibility(View.GONE);
                    tvType.setText("注册");
                    login.setText("登陆");
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setHint("请输入密码");
                    radio.setVisibility(View.GONE);
                }
                break;
        }
    }


    class LoginGet extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String s = "", url = Api.BASEURL + Api.LOGIN + "?tel=" + tel + "&pwd=" + pws;
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Log.e("结果", url);
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
            if (!s.equals("")) {
                LoginBean bean = new Gson().fromJson(s, LoginBean.class);
                editor.putString("user", username.getText().toString());
                editor.putString("password", password.getText().toString());
                editor.putString("token", bean.getId());
                editor.putString("type", bean.getStatus());
                Api.TOKEN = bean.getId();
                Api.TYPE = bean.getStatus();
                editor.putBoolean("save", true);
                editor.commit();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(context, "账号密码错误", Toast.LENGTH_LONG).show();
            }

        }
    }


    class RegisteGet extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String s = "";
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(Api.BASEURL + Api.REGISTE + "?tel=" + tel + "&pwd=" + pws + "&status=" + status + "&departmentName=" + depratmentId).build();
            Log.e("url", Api.BASEURL + Api.REGISTE + "?tel=" + tel + "&name=" + pws + "&status=" + status+"&departmentName=" + depratmentId);
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
                    password.setHint("请输入密码");
                    radio.setVisibility(View.GONE);
                    login.setText("登陆");
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
