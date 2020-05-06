package com.example.canteen;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.example.canteen.adapter.FoodAdapter;
import com.example.canteen.bean.FoodListBean;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FoodListActivity extends AppCompatActivity {

    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.ok)
    QMUIRoundButton  add;
    ImageView img;
    TextView foodName;
    EditText name;
    EditText rmd;
    QMUIRoundButton save, dfok;
    RatingBar ratingBar;
    View viewFood, dfView;
    PopupWindow popupWindowFood, dfPopupwindow;
    FoodAdapter foodAdapter = new FoodAdapter(R.layout.item_food_list);
    Context context;
    Uri uri;
    @BindView(R.id.title)
    TextView title;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodlist);
        ButterKnife.bind(this);
        context = this;
        title.setText(getIntent().getStringExtra("name"));
        id = getIntent().getStringExtra("id");
        if (id.equals(Api.TOKEN)) {
            add.setVisibility(View.VISIBLE);
        } else {
            add.setVisibility(View.GONE);
        }
        setPopFood();
        setDFPopWindow();
        setFoodAdapter();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopFood();
            }
        });
        GetList getList = new GetList();
        getList.execute();
    }

    private void setFoodAdapter() {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(foodAdapter);
        foodAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(FoodListActivity.this, CommentActivity.class);
                intent.putExtra("id", foodAdapter.getData().get(position).getId());
                startActivity(intent);
            }
        });
        foodAdapter.addChildClickViewIds(R.id.del, R.id.tv_df);
        foodAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.tv_df) {
                    showPopDF(foodAdapter.getData().get(position).getId().toString());
                } else if (view.getId() == R.id.del) {
                    FoodDel foodDel = new FoodDel(foodAdapter.getData().get(position).getId(), position);
                    foodDel.execute();

                }
            }
        });


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
        rmd = viewFood.findViewById(R.id.rmb);
        img = viewFood.findViewById(R.id.img);
        foodName = viewFood.findViewById(R.id.name);
        save = viewFood.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodName.getText().equals("") || rmd.getText().equals("")) {
                    Toast.makeText(FoodListActivity.this, "数据错误", Toast.LENGTH_LONG).show();
                } else {
                    UpLoad upLoad = new UpLoad(new File(getFilePathForN(context, uri)), foodName.getText().toString(), rmd.getText().toString());
                    upLoad.execute();
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

    private void showPopDF(String id) {
        dfok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingBar.getNumStars();
                Log.e("xingxing星星", String.valueOf(ratingBar.getRating()));
                DFnet dFnet = new DFnet(id, String.valueOf(ratingBar.getRating()));
                dFnet.execute();
            }
        });
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        dfPopupwindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    private void getPermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    getPhoto();
                } else {
                    Toast.makeText(FoodListActivity.this, "无权限", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private static String getFilePathForN(Context context, Uri uri) {
        try {
            Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String name = (returnCursor.getString(nameIndex));
            File file = new File(context.getFilesDir(), name);
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            returnCursor.close();
            inputStream.close();
            outputStream.close();
            return file.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (requestCode == 0) {
            uri = data.getData();
            Glide.with(this).load(uri.toString()).into(img);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择文件"), 0);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(FoodListActivity.this, "亲，木有文件管理器啊-_-!!", Toast.LENGTH_SHORT).show();
        }
    }


    class GetList extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            String s = "", url = Api.BASEURL + Api.GETFOOD + "?userId=" + id + "&pageIndex=" + "1" + "&pageSize=" + "100";
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
            FoodListBean bean = new Gson().fromJson(s, FoodListBean.class);
//            foodAdapter.setNewData(new ArrayList<FoodListBean.ListBean>());
            foodAdapter.setNewData(bean.getList());
        }
    }

    class FoodDel extends AsyncTask<Void, Void, String> {
        private String id;
        private int position;

        public FoodDel(String id, int position) {
            this.id = id;
            this.position = position;

        }

        @Override
        protected String doInBackground(Void... voids) {
            String s = "";
            OkHttpClient okHttpClient = new OkHttpClient();

            MultipartBody.Builder urlBuilder = new MultipartBody.Builder();
            urlBuilder.addFormDataPart("id", id);

            Request request = new Request.Builder().url(Api.BASEURL + Api.FOODDEL).post(urlBuilder.build()).build();
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
            if (s.equals(Api.SUCCESS)) {
                Toast.makeText(context, "删除成功", Toast.LENGTH_LONG).show();
                foodAdapter.remove(position);
            } else {
                Toast.makeText(context, "删除失败", Toast.LENGTH_LONG).show();
            }

        }
    }


    class FoodSave extends AsyncTask<Void, Void, String> {

        String foodName, foodPrice, fileName;

        public FoodSave(String fileName, String foodName, String foodPrice) {
            this.foodName = foodName;
            this.fileName = fileName;
            this.foodPrice = foodPrice;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String s = "";
            OkHttpClient okHttpClient = new OkHttpClient();
            MultipartBody.Builder urlBuilder = new MultipartBody.Builder();
            urlBuilder.addFormDataPart("foodName", foodName);
            urlBuilder.addFormDataPart("foodPrice", foodPrice);
            urlBuilder.addFormDataPart("foodImg", fileName);
            urlBuilder.addFormDataPart("userId", Api.TOKEN);

            Request request = new Request.Builder().url(Api.BASEURL + Api.FOODSAVE).post(urlBuilder.build()).build();
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
                Toast.makeText(context, "保存成功", Toast.LENGTH_LONG).show();
                GetList getList = new GetList();
                getList.execute();
            } else {
                Toast.makeText(context, "保存失败", Toast.LENGTH_LONG).show();
            }

        }
    }


    class UpLoad extends AsyncTask<Void, Void, String> {
        File file;
        String foodName, foodPrice;

        public UpLoad(File file, String foodName, String foodPrice) {
            this.file = file;
            this.foodName = foodName;
            this.foodPrice = foodPrice;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String s = "";
            OkHttpClient okHttpClient = new OkHttpClient();    // 设置文件以及文件上传类型封装
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
            MultipartBody.Builder urlBuilder = new MultipartBody.Builder();
            urlBuilder.setType(MultipartBody.FORM);
            urlBuilder.addFormDataPart("file", file.getName(),requestBody);
            Request request = new Request.Builder().url(Api.BASEURL + Api.UPLOAD).post(urlBuilder.build()).build();
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
            JsonObject returnData = new JsonParser().parse(s).getAsJsonObject();
            String data = returnData.get("dataFile").getAsString();
            if (null != data) {
                FoodSave foodSave = new FoodSave(uri.toString(), foodName, rmd.getText().toString());
                foodSave.execute();
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
            String s = "", url = Api.BASEURL + Api.GRADESAVE + "?foodId=" + id + "&grade=" + grade + "&userId=" + Api.TOKEN;
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
            if (s.equals("insertSuccess")) {
                Toast.makeText(context, "成功", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "失败", Toast.LENGTH_LONG).show();
            }
        }
    }
}
