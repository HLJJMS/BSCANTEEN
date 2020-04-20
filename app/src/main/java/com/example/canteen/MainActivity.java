package com.example.canteen;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.tbruyelle.rxpermissions2.RxPermissions;


import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {
    private static final int TIME_EXIT = 2000;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.add_item)
    TextView addItem;
    @BindView(R.id.tv_look_rmb)
    TextView tvLookRmb;
    private long mBackPressed;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.imageView)
    QMUIRadiusImageView imageView;
    @BindView(R.id.tv_welcome)
    TextView tvWelcome;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.recycler_left)
    RecyclerView recyclerLeft;
    @BindView(R.id.tv_exit)
    QMUIRoundButton tvExit;
    @BindView(R.id.navigationView)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    View viewFood;
    PopupWindow popupWindowFood;
    ImageView img;
    TextView foodName;
    QMUIRoundButton save, saveComment;
    View viewComment;
    PopupWindow popupWindowComment;
    RatingBar mRatingBar;
    EditText etComment;
    UriToFliePath uriToFliePath = new UriToFliePath();
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setPopComment();
        setPopFood();
        tvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showPopFood();
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


    private void setPopFood() {
        popupWindowFood = new PopupWindow(this);
        viewFood = LayoutInflater.from(this).inflate(R.layout.pop_add_food, null);
        popupWindowFood.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        popupWindowFood.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        popupWindowFood.setContentView(viewFood);
        popupWindowFood.setTouchable(true);
        popupWindowFood.setOutsideTouchable(true);//点击空白键取消
        popupWindowFood.setFocusable(true); //点击返回键取消
        popupWindowFood.setBackgroundDrawable(new BitmapDrawable());
        img = viewFood.findViewById(R.id.img);
        foodName = viewFood.findViewById(R.id.name);
        save = viewFood.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodName.getText().equals("")) {
                    Toast.makeText(MainActivity.this, "数据错误", Toast.LENGTH_LONG).show();
                } else {

                }
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPermissions();
            }
        });


        popupWindowFood.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }


    private void showPopFood() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        popupWindowFood.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }


    private void setPopComment() {
        popupWindowComment = new PopupWindow(this);
        viewComment = LayoutInflater.from(this).inflate(R.layout.popwindow_assess, null);
        popupWindowComment.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        popupWindowComment.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        popupWindowComment.setContentView(viewComment);
        popupWindowComment.setOutsideTouchable(true);//点击空白键取消
        popupWindowComment.setFocusable(true); //点击返回键取消
        mRatingBar = viewComment.findViewById(R.id.mRatingBar);
        etComment = viewComment.findViewById(R.id.et_txt);
        save = viewComment.findViewById(R.id.saveMessage);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodName.getText().equals("")) {
                    Toast.makeText(MainActivity.this, "数据错误", Toast.LENGTH_LONG).show();
                } else {

                }
            }
        });
        popupWindowComment.setTouchable(true);

        popupWindowComment.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }


    private void showPopComment() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        popupWindowComment.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    private void getPermissions(){
        RxPermissions  rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    getPhoto();
                } else {
                    Toast.makeText(MainActivity.this, "无权限", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getPhoto(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择文件"), 0);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "亲，木有文件管理器啊-_-!!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (requestCode == 0) {
            Uri uri = data.getData();
            Glide.with(this).load(uri).into(img);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}