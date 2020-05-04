package com.example.canteen.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.canteen.Api;
import com.example.canteen.R;
import com.example.canteen.bean.HomeListBean;

public class RestaurantAdapter extends BaseQuickAdapter<HomeListBean.ListBean, BaseViewHolder> {

    public RestaurantAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, HomeListBean.ListBean listBean) {
        baseViewHolder.setText(R.id.tv_name,listBean.getName());
        baseViewHolder.setText(R.id.yv_part,listBean.getGrade()+"分");
        if(Api.TYPE.equals(Api.ADMIN)){
            baseViewHolder.setVisible(R.id.del, true);
            baseViewHolder.setVisible(R.id.reset, true);
            baseViewHolder.setVisible(R.id.tv_df, false);
        }else {
            baseViewHolder.setVisible(R.id.del, false);
            baseViewHolder.setVisible(R.id.reset, false);
            if(Api.TYPE.equals(Api.COOKER)){
                baseViewHolder.setVisible(R.id.tv_df, false);
            }else {
                baseViewHolder.setVisible(R.id.tv_df, true);
            }

        }

    }

}
