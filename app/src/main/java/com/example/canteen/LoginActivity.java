package com.example.canteen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        editor.putBoolean("save", false);
        editor.commit();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin) {
                    if (username.getText().equals("") || password.getText().equals("")) {
                        Toast.makeText(LoginActivity.this, "账号密码不能为空", Toast.LENGTH_LONG).show();
                    } else {
                        editor.putString("user", username.getText().toString());
                        editor.putString("password", password.getText().toString());
                        editor.putBoolean("save", true);
                        editor.commit();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                } else {
                    //注册
                    if (username.getText().equals("") || password.getText().equals("")) {
                        Toast.makeText(LoginActivity.this, "账号密码不能为空", Toast.LENGTH_LONG).show();
                    } else {
                        editor.putString("user", username.getText().toString());
                        editor.putString("password", password.getText().toString());
                        editor.putBoolean("save", true);
                        editor.commit();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
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
                break;
            case 1:
                studen.setTextColor(txtFalse);
                teacher.setTextColor(txtTure);
                cooker.setTextColor(txtFalse);
                studen.setBackgroundResource(R.drawable.edit_back);
                teacher.setBackgroundResource(R.drawable.edit_back_solid);
                cooker.setBackgroundResource(R.drawable.edit_back);
                break;
            case 2:
                studen.setTextColor(txtFalse);
                teacher.setTextColor(txtFalse);
                cooker.setTextColor(txtTure);
                studen.setBackgroundResource(R.drawable.edit_back);
                teacher.setBackgroundResource(R.drawable.edit_back);
                cooker.setBackgroundResource(R.drawable.edit_back_solid);
                break;


        }


    }

}
