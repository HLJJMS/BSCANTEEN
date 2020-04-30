package com.example.canteen;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.example.canteen.adapter.CommentAdapter;
import com.example.canteen.bean.CommentBean;
import com.google.gson.Gson;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommentActivity extends AppCompatActivity {
    RatingBar ratingBar;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.add_item)
    TextView addItem;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    View viewComment;
    QMUIRoundButton btSave;
    EditText etComment;
    PopupWindow popupWindowComment;
    CommentAdapter commentAdapter = new CommentAdapter(R.layout.item_comment);
    String id;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        context = this;
        id = getIntent().getStringExtra("id");
        setPopComment();
        setRecyclerView();
        GetList getList = new GetList();
        getList.execute();
    }

    private void setRecyclerView() {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(commentAdapter);
        commentAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.del) {
                    DelComment delComment = new DelComment(commentAdapter.getData().get(position).getId(), position);
                    delComment.execute();
                }
            }
        });


    }


    private void setPopComment() {
        popupWindowComment = new PopupWindow(this);
        viewComment = LayoutInflater.from(this).inflate(R.layout.popwindow_assess, null);
        popupWindowComment.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        popupWindowComment.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        popupWindowComment.setContentView(viewComment);
        popupWindowComment.setOutsideTouchable(true);//点击空白键取消
        popupWindowComment.setFocusable(true); //点击返回键取消
        ratingBar = viewComment.findViewById(R.id.mRatingBar);
        etComment = viewComment.findViewById(R.id.et_txt);
        btSave = viewComment.findViewById(R.id.saveMessage);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                SaveComment saveComment = new SaveComment(etComment.getText().toString(), simpleDateFormat.format(date));
                saveComment.execute();
                popupWindowComment.dismiss();
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

    @OnClick(R.id.add_item)
    public void onViewClicked() {
        showPopComment();
    }


    class GetList extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            String s = "";
            OkHttpClient okHttpClient = new OkHttpClient();

            MultipartBody.Builder urlBuilder = new MultipartBody.Builder();
            urlBuilder.addFormDataPart("foodId", id);
            urlBuilder.addFormDataPart("pageIndex", "1");
            urlBuilder.addFormDataPart("pageSize", "100");
            Request request = new Request.Builder().url(Api.BASEURL + Api.COMMENTLIST).post(urlBuilder.build()).build();
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
            CommentBean bean = new Gson().fromJson(s, CommentBean.class);
            commentAdapter.setNewData(new ArrayList<CommentBean.ListBean>());
            commentAdapter.addData(bean.getList());
        }
    }


    class SaveComment extends AsyncTask<Void, Void, String> {
        String txt;
        String valuationDate;

        public SaveComment(String txt, String valuationDate) {
            this.txt = txt;
            this.valuationDate = valuationDate;
        }

        @Override
        protected String doInBackground(Void... voids) {

            String s = "";
            OkHttpClient okHttpClient = new OkHttpClient();

            MultipartBody.Builder urlBuilder = new MultipartBody.Builder();
            urlBuilder.addFormDataPart("foodId", id);
            urlBuilder.addFormDataPart("userId", Api.TOKEN);
            urlBuilder.addFormDataPart("valuation", txt);
            urlBuilder.addFormDataPart("valuation", valuationDate);
            Request request = new Request.Builder().url(Api.BASEURL + Api.COMMENTLIST).post(urlBuilder.build()).build();
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
            CommentBean bean = new Gson().fromJson(s, CommentBean.class);
            commentAdapter.setNewData(new ArrayList<CommentBean.ListBean>());
            commentAdapter.addData(bean.getList());
        }
    }

    class DelComment extends AsyncTask<Void, Void, String> {
        String commnetId;
        int position;

        public DelComment(String commnetId, int position) {
            this.commnetId = commnetId;
            this.position = position;
        }

        @Override
        protected String doInBackground(Void... voids) {

            String s = "";
            OkHttpClient okHttpClient = new OkHttpClient();

            MultipartBody.Builder urlBuilder = new MultipartBody.Builder();
            urlBuilder.addFormDataPart("id", commnetId);
            Request request = new Request.Builder().url(Api.BASEURL + Api.COMMENTDEL).post(urlBuilder.build()).build();
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
                commentAdapter.remove(position);
            } else {
                Toast.makeText(context, "删除失败", Toast.LENGTH_LONG).show();
            }
        }
    }
}
