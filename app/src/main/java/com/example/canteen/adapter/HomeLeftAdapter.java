package com.example.canteen.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.canteen.R;
import com.example.canteen.bean.HomeListBean;

public class HomeLeftAdapter extends BaseQuickAdapter<HomeListBean.ListBean, BaseViewHolder> {
    public HomeLeftAdapter() {
        super(R.layout.item_left);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, HomeListBean.ListBean listBean) {
        baseViewHolder.setText(R.id.txt,listBean.getName());
    }
}
