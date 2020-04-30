package com.example.canteen.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.canteen.R;
import com.example.canteen.bean.FoodListBean;

public class FoodAdapter extends BaseQuickAdapter<FoodListBean.ListBean, BaseViewHolder> {
    public FoodAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, FoodListBean.ListBean listBean) {
        baseViewHolder.setText(R.id.tv_name,listBean.getFoodName().toString());
        baseViewHolder.setText(R.id.yv_part,listBean.getFoodPrice());
        Glide.with(getContext()).load(listBean.getFoodImg()).into((ImageView)baseViewHolder.getView(R.id.img));
    }
}
