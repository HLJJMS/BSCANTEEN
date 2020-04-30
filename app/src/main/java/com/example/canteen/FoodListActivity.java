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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.example.canteen.adapter.FoodAdapter;
import com.example.canteen.bean.FoodListBean;
import com.example.canteen.bean.HomeListBean;
import com.google.gson.Gson;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FoodListActivity extends AppCompatActivity {

    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.ok)
    QMUIRoundButton add;
    ImageView img;
    TextView foodName;
    EditText name;
    EditText rmd;
    QMUIRoundButton save;
    View viewFood;
    PopupWindow popupWindowFood;
    FoodAdapter foodAdapter = new FoodAdapter(R.layout.item_food_list);
    Context context;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodlist);
        ButterKnife.bind(this);
        context = this;
        setPopFood();
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
        foodAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.del) {
                    FoodDel foodDel = new FoodDel(foodAdapter.getData().get(position).getId(), position);
                    foodDel.execute();
                } else {
                    Intent intent = new Intent(FoodListActivity.this, CommentActivity.class);
                    intent.putExtra("id", foodAdapter.getData().get(position).getId());
                    startActivity(intent);

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
                    FoodSave foodSave = new FoodSave(new File(getFilePathForN(context, uri)), foodName.getText().toString(), rmd.getText().toString());
                    foodSave.execute();
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
            Glide.with(this).load(uri).into(img);

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

            String s = "";
            OkHttpClient okHttpClient = new OkHttpClient();

            MultipartBody.Builder urlBuilder = new MultipartBody.Builder();
            urlBuilder.addFormDataPart("userId", Api.TOKEN);
            urlBuilder.addFormDataPart("pageIndex", "1");
            urlBuilder.addFormDataPart("pageSize", "100");
            Request request = new Request.Builder().url(Api.BASEURL + Api.GETFOOD).post(urlBuilder.build()).build();
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
            foodAdapter.setNewData(new ArrayList<FoodListBean.ListBean>());
            foodAdapter.addData(bean.getList());
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
        File file;
        String foodName, foodPrice;

        public FoodSave(File file, String foodName, String foodPrice) {
            this.file = file;
            this.foodName = foodName;
            this.foodPrice = foodPrice;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String s = "";
            OkHttpClient okHttpClient = new OkHttpClient();
            MultipartBody.Builder urlBuilder = new MultipartBody.Builder();
            urlBuilder.setType(MultipartBody.FORM);
            urlBuilder.addFormDataPart("foodName", foodName);
            urlBuilder.addFormDataPart("foodPrice", foodPrice);
            urlBuilder.addFormDataPart("foodImg", file.getName());
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
            if (s.equals(Api.SUCCESS)) {
                Toast.makeText(context, "保存成功", Toast.LENGTH_LONG).show();
                GetList getList = new GetList();
                getList.execute();
            } else {
                Toast.makeText(context, "保存失败", Toast.LENGTH_LONG).show();
            }

        }
    }
}
