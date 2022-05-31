package com.codebrew.clikat.module.filter.sub_category_selection;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codebrew.clikat.R;
import com.codebrew.clikat.data.constants.AppConstants;
import com.codebrew.clikat.databinding.ItemSubcategryBinding;
import com.codebrew.clikat.modal.other.FilterEvent;
import com.codebrew.clikat.modal.other.SubCategory;
import com.codebrew.clikat.utils.configurations.Configurations;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> {

    //private final OnSubCategoryListener mListener;

    private int all_status = -1;

    private ArrayList<SubCategory> dataBeans;

    private int selectedColor = Color.parseColor(Configurations.colors.tabSelected);

    private int listHead = Color.parseColor(Configurations.colors.textListHead);
    private int listSubHead = Color.parseColor(Configurations.colors.textListSubhead);


    public SubCategoryAdapter(ArrayList<SubCategory> dataBeans) {

        //  mListener = listener;
        this.dataBeans = dataBeans;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemSubcategryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_subcategry, parent, false);
        binding.setColor(Configurations.colors);

        View view = binding.getRoot();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.cbSubcategory.setText(dataBeans.get(position).getName());

        //  if (dataBeans.get(position).getDescription() != null)
        holder.tvSubcategoryItem.setVisibility(View.GONE);


        if (dataBeans.get(position).getSub_category() != null) {

            holder.cbSubcategory.setButtonDrawable(android.R.color.transparent);
            holder.cbSubcategory.setPadding(31, 0, 0, 0);

            if (dataBeans.get(position).getStatus()) {
                holder.cbSubcategory.setTextColor(selectedColor);
                holder.tvSubcategoryItem.setTextColor(selectedColor);
            } else {
                holder.cbSubcategory.setTextColor(listHead);
                holder.tvSubcategoryItem.setTextColor(listSubHead);
            }

        } else {
            if (dataBeans.get(position).getStatus())
                holder.cbSubcategory.setChecked(true);
            else
                holder.cbSubcategory.setChecked(false);
        }


       /* if (all_status == 1) {
            dataBeans.get(position).setStatus(true);
            holder.cbSubcategory.setChecked(true);
            mListener.onSubCategoryAdd(String.valueOf(dataBeans.get(position).getSub_category_id()), dataBeans.get(position).getName());
        } else if(all_status==0)
        {
            dataBeans.get(position).setStatus(false);
            holder.cbSubcategory.setChecked(false);
            mListener.onSubCategoryRemove(String.valueOf(dataBeans.get(position).getSub_category_id()));
        }*/

    }


    @Override
    public int getItemCount() {
        return dataBeans.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CheckBox cbSubcategory;
        TextView tvSubcategoryItem;
        LinearLayout subitemLayout;

        public ViewHolder(View view) {
            super(view);

            cbSubcategory = view.findViewById(R.id.cb_subcategory);
            tvSubcategoryItem = view.findViewById(R.id.tv_subcategory_item);
            subitemLayout = view.findViewById(R.id.subitem_Layout);

            subitemLayout.setOnClickListener(this);
        }


        @Override
        public String toString() {
            return super.toString() + " '" + cbSubcategory.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.subitem_Layout:

                    if (dataBeans.get(getAdapterPosition()).getSub_category() != null) {
                        EventBus.getDefault().post(new FilterEvent(AppConstants.SUBCATEGORY_DETAIL, dataBeans.get(getAdapterPosition()).getId(),
                                dataBeans.get(getAdapterPosition()).getName(), 1, dataBeans.get(getAdapterPosition()).getSub_category()));

                        // mListener.onSubCategoryDetail(dataBeans.get(getAdapterPosition()).getSub_category_id(), dataBeans.get(getAdapterPosition()).getName(),1);
                    } else {
                        if (dataBeans.get(getAdapterPosition()).getStatus()) {
                            dataBeans.get(getAdapterPosition()).setStatus(false);
                            EventBus.getDefault().post(new FilterEvent(AppConstants.SUBCATEGORY_REMOVE, dataBeans.get(getAdapterPosition()).getId(),
                                    dataBeans.get(getAdapterPosition()).getName(), 1, dataBeans.get(getAdapterPosition()).getSub_category()));

                            // mListener.onSubCategoryRemove(dataBeans.get(getAdapterPosition()).getSub_category_id(),dataBeans.get(getAdapterPosition()).getName(),1);
                        } else {
                            dataBeans.get(getAdapterPosition()).setStatus(true);
                            EventBus.getDefault().post(new FilterEvent(AppConstants.SUBCATEGORY_ADD, dataBeans.get(getAdapterPosition()).getId(),
                                    dataBeans.get(getAdapterPosition()).getName(), 1, dataBeans.get(getAdapterPosition()).getSub_category()));
                            //mListener.onSubCategoryAdd(dataBeans.get(getAdapterPosition()).getSub_category_id(), dataBeans.get(getAdapterPosition()).getName(),1);
                        }
                        notifyDataSetChanged();
                    }

                    break;
            }
        }
    }


}
