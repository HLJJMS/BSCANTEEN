package com.example.canteen.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.canteen.R;
import com.example.canteen.bean.CommentBean;

public class CommentAdapter extends BaseQuickAdapter<CommentBean.ListBean, BaseViewHolder> {
    public CommentAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, CommentBean.ListBean listBean) {
        baseViewHolder.setText(R.id.name,listBean.getUserName().toString());
        baseViewHolder.setText(R.id.txt,listBean.getValuation());
        baseViewHolder.setText(R.id.time,listBean.getValuationDate());
    }
}
