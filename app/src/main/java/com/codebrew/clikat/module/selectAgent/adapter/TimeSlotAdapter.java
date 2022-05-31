package com.codebrew.clikat.module.selectAgent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codebrew.clikat.R;
import com.codebrew.clikat.databinding.ItemTimeslotViewBinding;
import com.codebrew.clikat.modal.other.TimeSlot;
import com.codebrew.clikat.utils.configurations.Configurations;

import java.util.List;


public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {


    private List<TimeSlot> timeSlots;

    private TimeSlotCallback mCallabck;

    private Context context;
    private String type;
    private int adapterPosition;

    public TimeSlotAdapter(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public void settingCallback(TimeSlotCallback mCallabck) {
        this.mCallabck = mCallabck;
    }

    public void refreshAdapter(String type, int adapterPosition) {
        this.type = type;
        this.adapterPosition = adapterPosition;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TimeSlotAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();


        ItemTimeslotViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_timeslot_view, parent, false);
        binding.setColor(Configurations.colors);
        return new ViewHolder(binding.getRoot());

    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotAdapter.ViewHolder holder, int position) {

        String name = timeSlots.get(holder.getAdapterPosition()).getHeaderName();
        if (name.equals(context.getString(R.string.morning))) {
           holder.ivImage.setImageResource(R.drawable.ic_morning);
        } else  if(name.equals(context.getString(R.string.aftenoon)))
            holder.ivImage.setImageResource(R.drawable.ic_afternoon);
        else  if(name.equals(context.getString(R.string.night)))
            holder.ivImage.setImageResource(R.drawable.ic_night);
        if(name.equals(context.getString(R.string.evening)))
            holder.ivImage.setImageResource(R.drawable.ic_night);

        holder.tvTitle.setText(timeSlots.get(holder.getAdapterPosition()).getHeaderName());

        LinearLayoutManager layoutManager = new GridLayoutManager(context, 4);

        holder.rvTimePeriod.setLayoutManager(layoutManager);

        TimePeriodAdapter mAdapter = new TimePeriodAdapter(
                timeSlots.get(holder.getAdapterPosition()).getTimeName(),
                timeSlots.get(holder.getAdapterPosition()).getHeaderName());

        mAdapter.settingCallback(mCallabck);

        if (timeSlots.get(holder.getAdapterPosition()).getHeaderName().equals(type)) {
            mAdapter.slotPosition(timeSlots.get(holder.getAdapterPosition()).getHeaderName(), adapterPosition);
        }


        holder.rvTimePeriod.setAdapter(mAdapter);


    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }


    public interface TimeSlotCallback {
        void selectTimeSlot(String slot);

        void refreshAdapter(String type, int adapterPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView rvTimePeriod;
        TextView tvTitle;
        ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rvTimePeriod = itemView.findViewById(R.id.rv_timeperiod_slot);
            tvTitle = itemView.findViewById(R.id.tv_title);
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }
}
