package com.example.canteen;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.example.canteen.adapter.MaterialAdapter;
import com.example.canteen.bean.MaterialBean;
import com.google.gson.Gson;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MaterialActivity extends AppCompatActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.add_item)
    TextView addItem;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    PopupWindow popupWindow;
    View view;
    QMUIRoundButton ok;
    EditText name, rmb;
    Context context;
    MaterialAdapter adapter = new MaterialAdapter(R.layout.item_material);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);
        ButterKnife.bind(this);
        context = this;
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);


        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapters, @NonNull View view, int position) {
                showPopFood(adapter.getData().get(position).getId());
            }
        });

        adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapters, @NonNull View view, int position) {
                MaterialDel materialDel = new MaterialDel(adapter.getData().get(position).getId(), position);
                materialDel.execute();

                return false;
            }
        });

        setPopwindow();
        GetList getList = new GetList();
        getList.execute();
    }

    @OnClick(R.id.add_item)
    public void onViewClicked() {
        showPopFood("");
    }


    private void setPopwindow() {
        popupWindow = new PopupWindow(context);
        view = LayoutInflater.from(context).inflate(R.layout.pop_food_list_add, null);
        ok = view.findViewById(R.id.ok);
        name = view.findViewById(R.id.name);
        rmb = view.findViewById(R.id.price);
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        popupWindow.setContentView(view);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);//点击空白键取消
        popupWindow.setFocusable(true); //点击返回键取消
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }


    private void showPopFood(String id ) {

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a= name.getText().toString();
                String b = rmb.getText().toString();
                MaterialSave materialSave = new MaterialSave(name.getText().toString(), rmb.getText().toString(),id);
                materialSave.execute();
                popupWindow.dismiss();
            }
        });
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }


    class GetList extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            String s = "";
            OkHttpClient okHttpClient = new OkHttpClient();

            MultipartBody.Builder urlBuilder = new MultipartBody.Builder();
            urlBuilder.addFormDataPart("pageIndex", "1");
            urlBuilder.addFormDataPart("pageSize", "100");
            Request request = new Request.Builder().url(Api.BASEURL + Api.MATERIALLIST).post(urlBuilder.build()).build();
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
            MaterialBean bean = new Gson().fromJson(s, MaterialBean.class);
            adapter.setNewData(new ArrayList<MaterialBean.ListBean>());
            adapter.addData(bean.getList());
        }
    }

    class MaterialDel extends AsyncTask<Void, Void, String> {
        private String id;
        private int position;

        public MaterialDel(String id, int position) {
            this.id = id;
            this.position = position;

        }

        @Override
        protected String doInBackground(Void... voids) {
            String s = "";
            OkHttpClient okHttpClient = new OkHttpClient();

            MultipartBody.Builder urlBuilder = new MultipartBody.Builder();
            urlBuilder.addFormDataPart("id", id);

            Request request = new Request.Builder().url(Api.BASEURL + Api.MATERIALDEL).post(urlBuilder.build()).build();
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
                adapter.remove(position);
            } else {
                Toast.makeText(context, "删除失败", Toast.LENGTH_LONG).show();
            }

        }
    }


    class MaterialSave extends AsyncTask<Void, Void, String> {
        String materialName, materialPrice,id;

        public MaterialSave(String materialName, String materialPrice,String id) {
            this.materialName = materialName;
            this.materialPrice = materialPrice;
            this.id = id;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String s = "" ,url;
            OkHttpClient okHttpClient = new OkHttpClient();
            if(!id.equals("")){
              url =   Api.BASEURL + Api.MATERIALSAVE + "?ingredientsName="+materialName+"&ingredientsPrice=" +materialPrice +"&id=" +id ;
            }else{
                url =   Api.BASEURL + Api.MATERIALSAVE + "?ingredientsName="+materialName+"&ingredientsPrice=" +materialPrice ;
            }
            Log.e("url",url);
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
            if (s.equals("insertSuccess")||s.equals(Api.SUCCESS)) {
                Toast.makeText(context, "保存成功", Toast.LENGTH_LONG).show();
                GetList getList = new GetList();
                getList.execute();
            } else {
                Toast.makeText(context, "保存失败", Toast.LENGTH_LONG).show();
            }

        }
    }


}
