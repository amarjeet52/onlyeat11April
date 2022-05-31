package com.codebrew.clikat.module.restaurant_detail.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codebrew.clikat.R;

import java.util.List;

public class MenuCategoryAdapter extends RecyclerView.Adapter<MenuCategoryAdapter.ViewHolder> {

    private Context mContext;

    private List<String> categoryNames;

    private MenuCategoryCallback mCallback;

    public MenuCategoryAdapter(List<String> categoryNames) {
        this.categoryNames = categoryNames;
    }

    public void settingCallback(MenuCategoryCallback mCallback) {
        this.mCallback = mCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tvHeaderName.setText(categoryNames.get(holder.getAdapterPosition()));
        // holder.onBind(categoryNames.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return categoryNames.size();
    }


    public interface MenuCategoryCallback {
        void getMenuCategory(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeaderName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeaderName = itemView.findViewById(R.id.tv_header_name);

            tvHeaderName.setOnClickListener(view -> {
                mCallback.getMenuCategory(getAdapterPosition());
            });
        }
    }
}
