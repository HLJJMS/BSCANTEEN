package com.example.canteen.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.canteen.Api;
import com.example.canteen.R;
import com.example.canteen.bean.HomeListBean;

import java.util.List;

public class RestaurantAdapter extends BaseQuickAdapter<HomeListBean.ListBean, BaseViewHolder> {
    public RestaurantAdapter(int layoutResId, List<HomeListBean.ListBean> data) {
        super(layoutResId, data);
    }

    public RestaurantAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, HomeListBean.ListBean listBean) {
        baseViewHolder.setText(R.id.name,listBean.getName());
        if(Api.TYPE.equals(Api.ADMIN)){
            baseViewHolder.setVisible(R.id.del, true);
        }else {
            baseViewHolder.setVisible(R.id.del, false);
        }

    }
}
