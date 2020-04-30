package com.example.canteen;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.canteen.bean.MaterialBean;

public class MaterialAdapter extends BaseQuickAdapter<MaterialBean.ListBean, BaseViewHolder> {
    public MaterialAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, MaterialBean.ListBean listBean) {
        baseViewHolder.setText(R.id.name,listBean.getIngredientsName());
        baseViewHolder.setText(R.id.rmb,listBean.getIngredientsPrice());

    }
}
