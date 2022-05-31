package com.codebrew.clikat.module.filter.adapter;

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
import com.codebrew.clikat.modal.other.FilterVarientCatModel;
import com.codebrew.clikat.module.filter.BrandListAdapter;
import com.codebrew.clikat.module.filter.FilterVarientListAdapter;
import com.codebrew.clikat.utils.configurations.Configurations;

import java.util.List;

public class FilterListAdapter extends RecyclerView.Adapter<FilterListAdapter.ViewHolder> {

    private List<FilterVarientCatModel.DataBean> modelList;

    private Context context;

    private VarientListCallback callback;

    private int listPosition = 0;

    public FilterListAdapter(List<FilterVarientCatModel.DataBean> modelList) {

        this.modelList = modelList;
    }

    public void settingCallback(VarientListCallback callback) {
        this.callback = callback;
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

       /* if(modelList.get(pos).getVariant_values().size()>0)
        {*/
        holder.tvVarientName.setText(modelList.get(pos).getVariant_name());

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        holder.rvVarientList.setLayoutManager(layoutManager);

        if (modelList.get(pos).getVariant_type() == 1)
            holder.rvVarientList.setAdapter(new FilterVarientListAdapter(modelList.get(pos).getVariant_values(), "color", listPosition));
        else if (modelList.get(pos).getVariant_name().toLowerCase().equals("brands"))
            holder.rvVarientList.setAdapter(new BrandListAdapter(modelList.get(pos).getBrands_values()));
        else
            holder.rvVarientList.setAdapter(new FilterVarientListAdapter(modelList.get(pos).getVariant_values(), "size", listPosition));


        listPosition += 1;

        // }
       /* else
        {
            holder.tvVarientName.setVisibility(View.GONE);
            holder.rvVarientList.setVisibility(View.GONE);
        }*/

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public interface VarientListCallback {
        void addVarientId(int id, int listPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rvVarientList;
        TextView tvVarientName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVarientName = itemView.findViewById(R.id.tv_varientName);
            rvVarientList = itemView.findViewById(R.id.rv_varient_list);
        }
    }
}
