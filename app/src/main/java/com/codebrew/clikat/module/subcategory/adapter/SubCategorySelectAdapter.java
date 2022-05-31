package com.codebrew.clikat.module.subcategory.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codebrew.clikat.BuildConfig;
import com.codebrew.clikat.R;
import com.codebrew.clikat.databinding.ItemSubcategoryBinding;
import com.codebrew.clikat.modal.AppGlobal;
import com.codebrew.clikat.modal.other.SubCategoryData;
import com.codebrew.clikat.utils.StaticFunction;
import com.codebrew.clikat.utils.Utils;
import com.codebrew.clikat.utils.configurations.Configurations;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/*
 * Created by Harman Sandhu .
 */
public class SubCategorySelectAdapter extends RecyclerView.Adapter<SubCategorySelectAdapter.ViewHolder> {

    private List<SubCategoryData> list;

    private SubCategoryCallback mCallback;
    private String viewType;

    public SubCategorySelectAdapter(List<SubCategoryData> list,String viewType) {
        this.list = list;
        this.viewType = viewType;
    }

    public void settingCallback(SubCategoryCallback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemSubcategoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_subcategory, parent, false);
        binding.setColor(Configurations.colors);
        binding.setDrawables(Configurations.drawables);
        binding.setStrings(Configurations.strings);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        SubCategoryData currentData = list.get(position);
        // holder.tvCategoryDesc.setText(currentData.getDescription());
        holder.tvCategoryName.setText(currentData.getName());


        if(BuildConfig.CLIENT_CODE=="dailyooz_0544")
        {
            holder.simpleDraweeView.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.INVISIBLE);
            StaticFunction.INSTANCE.loadImage(currentData.getImage(), holder.simpleDraweeView, false,null,null);
        }else
        {
            holder.simpleDraweeView.setVisibility(View.INVISIBLE);
            holder.imageView.setVisibility(View.VISIBLE);
            StaticFunction.INSTANCE.loadImage(currentData.getImage(), holder.imageView, false,null,null);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public interface SubCategoryCallback {
        void onSubCategoryDtail(SubCategoryData dataBean);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvCategoryName;
    /*    @BindView(R.id.tvCategoryDesc)
        TextView tvCategoryDesc;*/
        ConstraintLayout itemSubCatContainer;
        CircleImageView simpleDraweeView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            simpleDraweeView = itemView.findViewById(R.id.sdvProduct);
            imageView = itemView.findViewById(R.id.sdvProductImage);
            itemSubCatContainer = itemView.findViewById(R.id.itemSubCatContainer);
            // tvCategoryDesc.setTypeface(AppGlobal.regular);
            tvCategoryName.setTypeface(AppGlobal.regular);

            itemView.setOnClickListener(this);

            if(viewType.equals("horizontal")){
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemSubCatContainer.getLayoutParams();
               // params.width = (int) Utils.INSTANCE.dpToPx(150);
                itemSubCatContainer.setLayoutParams(params);
            }

        }


        @Override
        public void onClick(View v) {
            mCallback.onSubCategoryDtail(list.get(getAdapterPosition()));
        }
    }

}
