package com.example.canteen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteen.adapter.RestaurantAdapter;
import com.example.canteen.bean.HomeListBean;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.canteen.Api.ADMIN;
import static com.example.canteen.Api.COOKER;
import static com.example.canteen.Api.STUDENT;
import static com.example.canteen.Api.TEACHER;

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


    QMUIRoundButton save, saveComment;


    RatingBar mRatingBar;
    EditText etComment;
    String path;
    RestaurantAdapter restaurantAdapter = new RestaurantAdapter(R.layout.item_right);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setData();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(restaurantAdapter);
        tvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });

    }

    private void setData() {
        if (Api.TYPE.equals(STUDENT)) {
            tvWelcome.setText("欢迎同学");
            imageView.setImageResource(R.mipmap.ic_student);
        } else if (Api.TYPE.equals(TEACHER)) {
            tvWelcome.setText("欢迎教师");
            imageView.setImageResource(R.mipmap.ic_teacher);
        } else if (Api.TYPE.equals(COOKER)) {
            tvWelcome.setText("欢迎厨师");
            imageView.setImageResource(R.mipmap.ic_cook);
        } else if (Api.TYPE.equals(ADMIN)) {
            tvWelcome.setText("欢迎管理员");
            imageView.setImageResource(R.mipmap.ic_cpu);
        }
        tvNumber.setText(Api.TOKEN);
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

    @OnClick({R.id.add_item, R.id.tv_look_rmb})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_item:
                break;
            case R.id.tv_look_rmb:
                startActivity(new Intent(this, MaterialActivity.class));
                break;
        }
    }


    class GetList extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String s = "";
            OkHttpClient okHttpClient = new OkHttpClient();

            MultipartBody.Builder urlBuilder = new MultipartBody.Builder();
            urlBuilder.addFormDataPart("status", Api.TYPE);
            urlBuilder.addFormDataPart("pageIndex", "1");
            urlBuilder.addFormDataPart("pageSize", "100");
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
            HomeListBean bean = new Gson().fromJson(s, HomeListBean.class);
            restaurantAdapter.addData(bean.getList());
        }
    }


}
