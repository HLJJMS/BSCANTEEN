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
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
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
    CommentAdapter commentAdapter = new CommentAdapter(new ArrayList<>());
    String id;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        context = this;
        id = getIntent().getStringExtra("id");
        title.setText(getIntent().getStringExtra("name"));
        setPopComment();
        setRecyclerView();
        GetList getList = new GetList();
        getList.execute();
    }

    private void setRecyclerView() {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(commentAdapter);
        commentAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (commentAdapter.getData().get(position).getItemType() == 1) {
                    DelComment delComment = new DelComment(commentAdapter.getData().get(position).getId(), position);
                    delComment.execute();
                } else {
                    DelReply delReply = new DelReply(commentAdapter.getData().get(position).getId(), position);
                    delReply.execute();
                }
                return false;
            }
        });

        commentAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                showPopComment(false,commentAdapter.getData().get(position).getId());
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


    private void showPopComment(boolean isComment,String id) {
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (!isComment) {
                    SaveReply saveReply = new SaveReply(etComment.getText().toString(), simpleDateFormat.format(date),id);
                    saveReply.execute();
                } else {
                    SaveComment saveComment = new SaveComment(etComment.getText().toString(), simpleDateFormat.format(date));
                    saveComment.execute();
                }
                popupWindowComment.dismiss();
            }
        });


        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        popupWindowComment.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    @OnClick(R.id.add_item)
    public void onViewClicked() {
        showPopComment(true,"");
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
            commentAdapter.setNewData(bean.getList());
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

            String s = "", url = Api.BASEURL + Api.COMMENTSAVE + "?foodId=" + id + "&userId=" + Api.TOKEN + "&valuation=" + txt + "&valuationDate=" + valuationDate;
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
                GetList getList = new GetList();
                getList.execute();
                Toast.makeText(context, "评论成功", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "评论失败", Toast.LENGTH_LONG).show();
            }

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


    class DelReply extends AsyncTask<Void, Void, String> {
        String commnetId;
        int position;

        public DelReply(String commnetId, int position) {
            this.commnetId = commnetId;
            this.position = position;
        }

        @Override
        protected String doInBackground(Void... voids) {

            String s = "";
            OkHttpClient okHttpClient = new OkHttpClient();

            MultipartBody.Builder urlBuilder = new MultipartBody.Builder();
            urlBuilder.addFormDataPart("id", commnetId);
            Request request = new Request.Builder().url(Api.BASEURL + Api.REPLYDEL).post(urlBuilder.build()).build();
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

    class SaveReply extends AsyncTask<Void, Void, String> {
        String txt;
        String valuationDate;
        String valuationId ;
        public SaveReply(String txt, String valuationDate,String valuationId) {
            this.txt = txt;
            this.valuationDate = valuationDate;
            this.valuationId=valuationId;
        }

        @Override
        protected String doInBackground(Void... voids) {

            String s = "", url = Api.BASEURL + Api.REPLYSAVE + "?foodId=" + id + "&userId=" + Api.TOKEN + "&replyContent=" + txt + "&replyDate=" + valuationDate+"&valuationId="+valuationId;
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
                GetList getList = new GetList();
                getList.execute();
                Toast.makeText(context, "评论成功", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "评论失败", Toast.LENGTH_LONG).show();
            }

        }
    }
}
