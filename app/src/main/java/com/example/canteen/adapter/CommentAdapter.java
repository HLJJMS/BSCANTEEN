package com.example.canteen.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.canteen.R;
import com.example.canteen.bean.CommentBean;

import java.util.List;

public class CommentAdapter extends BaseMultiItemQuickAdapter<CommentBean.ListBean, BaseViewHolder> {
    public CommentAdapter(List<CommentBean.ListBean> data) {
        super(data);
        addItemType(1, R.layout.item_comment);
        addItemType(2, R.layout.item_material);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, CommentBean.ListBean listBean) {
        if(listBean.getItemType()==1){
            baseViewHolder.setText(R.id.name,listBean.getUserName().toString());
            baseViewHolder.setText(R.id.txt,listBean.getValuation());
            baseViewHolder.setText(R.id.time,listBean.getValuationDate());
        }else{
            baseViewHolder.setText(R.id.name,"商家回复");
            baseViewHolder.setText(R.id.rmb,listBean.getValuation());
            baseViewHolder.setText(R.id.del,listBean.getValuationDate());
        }

    }
}
