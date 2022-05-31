package com.codebrew.clikat.module.selectAgent.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codebrew.clikat.R;
import com.codebrew.clikat.databinding.ItemFilterChecksizeBinding;
import com.codebrew.clikat.utils.configurations.Configurations;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class TimePeriodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<String> mChecklist;
    private Context mContext;

    //private int checkedPosition = -1;

    private TimeSlotAdapter.TimeSlotCallback mCallabck;
    private String type;
    private int adapterPosition = -1;
    TimePeriodAdapter(List<String> mChecklist, String type) {
        this.mChecklist = mChecklist;
        this.type = type;
    }

    public void settingCallback(TimeSlotAdapter.TimeSlotCallback mCallabck) {
        this.mCallabck = mCallabck;
    }

    void slotPosition(String type, int adapterPosition) {
        this.type = type;
        this.adapterPosition = adapterPosition;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        ItemFilterChecksizeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.item_filter_checksize, viewGroup, false);
        binding.setColor(Configurations.colors);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        int pos = viewHolder.getAdapterPosition();

        ViewHolder holder = (ViewHolder) viewHolder;

        holder.tvName.setText(mChecklist.get(pos));

        if (adapterPosition == i) {
            holder.tvName.setBackgroundColor(Color.parseColor(Configurations.colors.primaryColor));
            holder.tvName.setTextColor(Color.parseColor(Configurations.colors.appBackground));
            //checked
        } else {
            holder.tvName.setBackgroundColor(Color.parseColor(Configurations.colors.appBackground));
            //  holder.tvName.setStrokeWidth(2);
            // holder.tvName.setStrokeColor(ColorStateList.valueOf(Color.parseColor(Configurations.colors.primaryColor)));
            holder.tvName.setTextColor(Color.parseColor(Configurations.colors.textHead));
            //unchecked
        }


    }

    @Override
    public int getItemCount() {
        return mChecklist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        MaterialButton tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);

            tvName.setOnClickListener(v -> {
                if (mCallabck != null) {
                    mCallabck.selectTimeSlot(mChecklist.get(getAdapterPosition()));

                    mCallabck.refreshAdapter(type, getAdapterPosition());
                }
            });
        }
    }

}
