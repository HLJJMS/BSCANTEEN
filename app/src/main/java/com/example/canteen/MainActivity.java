package com.example.canteen;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.canteen.adapter.HomeLeftAdapter;
import com.example.canteen.adapter.RestaurantAdapter;
import com.example.canteen.bean.HomeListBean;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.example.canteen.Api.ADMIN;
import static com.example.canteen.Api.COOKER;
import static com.example.canteen.Api.STUDENT;
import static com.example.canteen.Api.SUCCESS;
import static com.example.canteen.Api.TEACHER;
import static com.example.canteen.Api.TYPE;

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
    RestaurantAdapter restaurantAdapter = new RestaurantAdapter(R.layout.item_right);
    HomeLeftAdapter homeLeftAdapter = new HomeLeftAdapter();
    Context context;
    View popView, dfView;
    PopupWindow editPsdPopupwindow, dfPopupwindow;
    EditText editTextPsd;
    QMUIRoundButton popOk, dfok;
    RatingBar ratingBar;
    String myRating="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;
        setData();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(restaurantAdapter);
        restaurantAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                Intent intent = new Intent(MainActivity.this, FoodListActivity.class);
                intent.putExtra("name", restaurantAdapter.getData().get(position).getName());
                intent.putExtra("id", restaurantAdapter.getData().get(position).getId());
                startActivity(intent);
            }
        });
        restaurantAdapter.addChildClickViewIds(R.id.del, R.id.reset, R.id.tv_df);
        restaurantAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.del) {
                    DelUser delUser = new DelUser(restaurantAdapter.getItem(position).getId());
                    delUser.execute();
                } else if (view.getId() == R.id.reset) {
                    ResetPwd resetPwd = new ResetPwd(restaurantAdapter.getItem(position).getId());
                    resetPwd.execute();
                } else if (view.getId() == R.id.tv_df) {
                    showPopDF(restaurantAdapter.getItem(position).getId());
                }
            }
        });
        tvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });

        recyclerLeft.setLayoutManager(new LinearLayoutManager(this));
        recyclerLeft.setAdapter(homeLeftAdapter);
        homeLeftAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                title.setText(homeLeftAdapter.getData().get(position).getName());
                GetRightList getRightList = new GetRightList(homeLeftAdapter.getData().get(position).getId());
                getRightList.execute();
            }
        });

        setEDPopWindow();
        setDFPopWindow();
    }

    private void setDFPopWindow() {
        dfView = LayoutInflater.from(context).inflate(R.layout.pop_dafen, null);
        dfPopupwindow = new PopupWindow();
        dfPopupwindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        dfPopupwindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        dfPopupwindow.setContentView(dfView);
        dfPopupwindow.setTouchable(true);
        dfPopupwindow.setOutsideTouchable(true);
        dfPopupwindow.setFocusable(true); //点击返回键取消
        dfPopupwindow.setBackgroundDrawable(new BitmapDrawable());
        dfok = dfView.findViewById(R.id.saveMessage);
        ratingBar = dfView.findViewById(R.id.mRatingBar);

        dfPopupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }

    private void setEDPopWindow() {
        popView = LayoutInflater.from(context).inflate(R.layout.pop_edit_psd, null);
        editPsdPopupwindow = new PopupWindow();
        editPsdPopupwindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        editPsdPopupwindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        editPsdPopupwindow.setContentView(popView);
        editPsdPopupwindow.setTouchable(true);
        editPsdPopupwindow.setOutsideTouchable(true);
        editPsdPopupwindow.setFocusable(true); //点击返回键取消
        editPsdPopupwindow.setBackgroundDrawable(new BitmapDrawable());
        popOk = popView.findViewById(R.id.ok);
        editTextPsd = popView.findViewById(R.id.edit_psd);
        popOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextPsd.getText().toString().equals("")) {
                    EditPwd editPwd = new EditPwd(Api.TOKEN, editTextPsd.getText().toString());
                    editPwd.execute();
                    editPsdPopupwindow.dismiss();
                } else {
                    Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
                }


            }
        });

        editPsdPopupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }

    private void showPopEditPds() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        editPsdPopupwindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    private void showPopDF(String id) {
        dfok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingBar.getNumStars();
                Log.e("xingxing星星",String.valueOf(ratingBar.getRating()));
                DFnet dFnet = new DFnet(id,String.valueOf(ratingBar.getRating()));
                dFnet.execute();
            }
        });
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        dfPopupwindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
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
            recyclerLeft.setVisibility(View.GONE);
            title.setText("用户管理");

        }
        tvNumber.setText(Api.TOKEN);
        GetLeftList getLeftList = new GetLeftList();
        getLeftList.execute();
        GetRightList getRightList = new GetRightList("1");
        getRightList.execute();
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

    @OnClick({R.id.add_item, R.id.tv_look_rmb, R.id.bt_edit_pwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_item:
                break;
            case R.id.tv_look_rmb:
                startActivity(new Intent(this, MaterialActivity.class));
                break;
            case R.id.bt_edit_pwd:
                showPopEditPds();
                break;

        }
    }


    class GetRightList extends AsyncTask<Void, Void, String> {
        String id;

        public GetRightList(String id) {
            this.id = id;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String s = "", url = "";
            if (!TYPE.equals(ADMIN)) {
                url = Api.BASEURL + Api.GETUSER + "?status=" + COOKER + "&departmentId=" + id + "&pageIndex=1" + "&pageSize=" + "100";
            } else {
                url = Api.BASEURL + Api.GETUSER + "?pageIndex=1" + "&pageSize=" + "100";
            }
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
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
            restaurantAdapter.setNewData(bean.getList());
        }
    }

    class GetLeftList extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String s = "", url = Api.BASEURL + Api.DEPARTMENT + "?pageIndex=1" + "&pageSize=" + "100";
            OkHttpClient okHttpClient = okhttpclient();
            Request request = new Request.Builder().url(url).build();
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
            homeLeftAdapter.addData(bean.getList());
        }
    }


    private OkHttpClient okhttpclient() {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .addNetworkInterceptor(logInterceptor)
                .build();
        return mOkHttpClient;
    }


    class DelUser extends AsyncTask<Void, Void, String> {
        String id;

        public DelUser(String id) {
            this.id = id;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String s = "", url = Api.BASEURL + Api.USERDEL + "?id=" + id;
            OkHttpClient okHttpClient = okhttpclient();
            Request request = new Request.Builder().url(url).build();
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
            if (s.equals(SUCCESS)) {
                Toast.makeText(context, "删除成功", Toast.LENGTH_LONG).show();
                GetRightList getRightList = new GetRightList("1");
                getRightList.execute();
            } else {
                Toast.makeText(context, "删除失败", Toast.LENGTH_LONG).show();
            }
        }
    }


    class ResetPwd extends AsyncTask<Void, Void, String> {
        String id;

        public ResetPwd(String id) {
            this.id = id;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String s = "", url = Api.BASEURL + Api.RESETPWD + "?id=" + id;
            OkHttpClient okHttpClient = okhttpclient();
            Request request = new Request.Builder().url(url).build();
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
            if (s.equals(SUCCESS)) {
                Toast.makeText(context, "成功", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    class EditPwd extends AsyncTask<Void, Void, String> {
        String id, pwd;

        public EditPwd(String id, String pwd) {
            this.id = id;
            this.pwd = pwd;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String s = "", url = Api.BASEURL + Api.EDITPWD + "?id=" + id + "&pwd=" + pwd;
            OkHttpClient okHttpClient = okhttpclient();
            Request request = new Request.Builder().url(url).build();
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
            if (s.equals(SUCCESS)) {
                Toast.makeText(context, "成功", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "失败", Toast.LENGTH_LONG).show();
            }
        }
    }


    class DFnet extends AsyncTask<Void, Void, String> {
        String id, grade;

        public DFnet(String id, String grade) {
            this.id = id;
            this.grade = grade;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String s = "", url = Api.BASEURL + Api.GRADESAVE + "?shopId=" + id + "&grade=" + grade + "&userId=" + Api.TOKEN;
            OkHttpClient okHttpClient = okhttpclient();
            Request request = new Request.Builder().url(url).build();
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
            if (s.equals("insertSuccess")) {
                Toast.makeText(context, "成功", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "失败", Toast.LENGTH_LONG).show();
            }
        }
    }
}
