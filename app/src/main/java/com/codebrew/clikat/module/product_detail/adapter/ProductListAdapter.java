package com.codebrew.clikat.module.product_detail.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codebrew.clikat.R;
import com.codebrew.clikat.databinding.ItemVarientListBinding;
import com.codebrew.clikat.modal.other.VariantsBean;
import com.codebrew.clikat.utils.NpaGridLayoutManager;
import com.codebrew.clikat.utils.configurations.Configurations;

import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private List<VariantsBean> modelList;
    private Context context;
    private ProductVarientListAdapter.FilterVarientCallback mfilterCallback;
    private String varientId;

    public ProductListAdapter(List<VariantsBean> modelList) {
        this.modelList = modelList;
    }

    public void settingCallback(ProductVarientListAdapter.FilterVarientCallback mfilterCallback) {
        this.mfilterCallback = mfilterCallback;
    }

    public void settingQuery(String adapterPosition, int adpaterPosition) {
        this.varientId = adapterPosition;

        // notifyItemChanged(adpaterPosition,modelList);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        ItemVarientListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_varient_list, parent, false);
        binding.setColor(Configurations.colors);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int pos = holder.getAdapterPosition();

        if (modelList.get(pos).getVariant_values().size() > 0) {
            holder.tvVarientName.setText(modelList.get(pos).getVariant_name());

            ProductVarientListAdapter adapter;
            if (modelList.get(pos).getVariant_type() != null && modelList.get(pos).getVariant_type() == 1) {
                adapter = new ProductVarientListAdapter();
                adapter.settingCallback(mfilterCallback, "color", modelList.get(pos).getVariant_values(), pos);
                holder.rvVarientList.clearAnimation();
                holder.rvVarientList.setAdapter(adapter);

                if (varientId != null) {
                    adapter.getFilter().filter(varientId);
                }

            } else {
                adapter = new ProductVarientListAdapter();
                adapter.settingCallback(mfilterCallback, "size", modelList.get(pos).getVariant_values(), pos);
                holder.rvVarientList.clearAnimation();
                holder.rvVarientList.setAdapter(adapter);

                if (varientId != null) {
                    adapter.getFilter().filter(varientId);
                }
            }
        } else {
            holder.tvVarientName.setVisibility(View.GONE);
            holder.rvVarientList.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView rvVarientList;
        TextView tvVarientName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVarientName = itemView.findViewById(R.id.tv_varientName);
            rvVarientList = itemView.findViewById(R.id.rv_varient_list);

            LinearLayoutManager layoutManager = new NpaGridLayoutManager(context, 1, LinearLayoutManager.HORIZONTAL, false);
            rvVarientList.setLayoutManager(layoutManager);
        }
    }
}
