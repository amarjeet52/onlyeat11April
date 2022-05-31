package com.codebrew.clikat.module.supplier_detail.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codebrew.clikat.R;
import com.codebrew.clikat.databinding.ItemSupplierServiceBinding;
import com.codebrew.clikat.modal.other.SupplierServiceModel;
import com.codebrew.clikat.utils.configurations.Configurations;
import com.codebrew.clikat.utils.customviews.ClikatTextView;

import java.util.List;


public class SupplierServiceAdapter extends RecyclerView.Adapter<SupplierServiceAdapter.ViewHolder> {


    private List<SupplierServiceModel> serviceList;

    private Context context;

    public SupplierServiceAdapter(List<SupplierServiceModel> serviceList) {
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();

        ItemSupplierServiceBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_supplier_service, parent, false);
        binding.setColor(Configurations.colors);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SupplierServiceModel serviceModel = serviceList.get(holder.getAdapterPosition());

        // StaticFunction.loadImage(serviceModel.getImage_id(),holder.ivServiceType,false);

        Glide.with(context).load(serviceModel.getImage_id()).into(holder.ivServiceType);

        holder.tvServiceName.setText(serviceModel.getService_name());

        holder.tvServiceData.setText(serviceModel.getService_value());

        //      DrawableCompat.setTint(holder.ivServiceType.getDrawable(), Color.parseColor(Configurations.colors.primaryColor));

    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivServiceType;
        ClikatTextView tvServiceName;
        ClikatTextView tvServiceData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivServiceType = itemView.findViewById(R.id.iv_serviceType);
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            tvServiceData = itemView.findViewById(R.id.tv_service_data);
        }
    }
}
