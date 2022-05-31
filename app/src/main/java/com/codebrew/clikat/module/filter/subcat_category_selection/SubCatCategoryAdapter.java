package com.codebrew.clikat.module.filter.subcat_category_selection;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codebrew.clikat.R;
import com.codebrew.clikat.data.constants.AppConstants;
import com.codebrew.clikat.databinding.ItemSubcatCategoryBinding;
import com.codebrew.clikat.modal.other.FilterEvent;
import com.codebrew.clikat.modal.other.SubCategory;
import com.codebrew.clikat.module.filter.SubCat_CategoryFragment.OnSubcatCategoryListener;
import com.codebrew.clikat.utils.configurations.Configurations;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


/**
 * specified {@link OnSubcatCategoryListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SubCatCategoryAdapter extends RecyclerView.Adapter<SubCatCategoryAdapter.ViewHolder> {


    // private final OnSubcatCategoryListener mListener;

    private int all_status = -1;

    private ArrayList<SubCategory> dataBeans;

    private int selectedColor = Color.parseColor(Configurations.colors.tabSelected);

    private int listHead = Color.parseColor(Configurations.colors.textListHead);

    private SubCatCallback mCallback;

    private int frag_position;

    public SubCatCategoryAdapter(ArrayList<SubCategory> dataBeans, int frag_position) {

        //  mListener = listener;
        this.dataBeans = dataBeans;
        this.frag_position = frag_position;
    }

    public void settingCallback(SubCatCallback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //item_subcat_category

        ItemSubcatCategoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_subcat_category, parent, false);
        binding.setColor(Configurations.colors);
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subcat_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.cbSubcategoryItem.setText(dataBeans.get(holder.getAdapterPosition()).getName());


        if (dataBeans.get(position).getSub_category() != null) {


            holder.cbSubcategoryItem.setButtonDrawable(android.R.color.transparent);
            holder.cbSubcategoryItem.setPadding(31, 0, 0, 0);

            if (dataBeans.get(position).getStatus()) {
                holder.cbSubcategoryItem.setTextColor(selectedColor);
            } else {
                holder.cbSubcategoryItem.setTextColor(listHead);
            }

        } else {
            if (dataBeans.get(position).getStatus())
                holder.cbSubcategoryItem.setChecked(true);
            else
                holder.cbSubcategoryItem.setChecked(false);
        }

       /* if (all_status == 1) {
            dataBeans.get(position).setStatus(true);
            holder.cbSubcategoryItem.setChecked(true);
            mListener.onSubCategoryAdd(String.valueOf(dataBeans.get(position).getSub_category_id()), dataBeans.get(position).getName());
        } else if(all_status==0)
        {
            dataBeans.get(position).setStatus(false);
            holder.cbSubcategoryItem.setChecked(false);
            mListener.onSubCategoryRemove(String.valueOf(dataBeans.get(position).getSub_category_id()));
        }
*/
    }

    @Override
    public int getItemCount() {
        return dataBeans.size();
    }


    public interface SubCatCallback {

        void onSubCat(int id);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CheckBox cbSubcategoryItem;


        public ViewHolder(View view) {
            super(view);
            cbSubcategoryItem = view.findViewById(R.id.cb_subcategory_item);

            cbSubcategoryItem.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + cbSubcategoryItem.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.cb_subcategory_item) {/*       if (dataBeans.get(getAdapterPosition()).getSub_category_id()==-787) {

                    if(dataBeans.get(getAdapterPosition()).getAll_status())
                    {
                        all_status=0;
                    }
                    else
                    {
                        all_status=1;
                    }
                    notifyDataSetChanged();

                }else {*/

                if (dataBeans.get(getAdapterPosition()).getSub_category() != null) {

                    EventBus.getDefault().post(new FilterEvent(AppConstants.SUBCATEGORY_CATEGORY, dataBeans.get(getAdapterPosition()).getId(),
                            dataBeans.get(getAdapterPosition()).getName(), frag_position, dataBeans.get(getAdapterPosition()).getSub_category()));


                    // mListener.onSubcatCategoryInteraction(dataBeans.get(getAdapterPosition()).getSub_category_id(), dataBeans.get(getAdapterPosition()).getName(),frag_position);
                } else {
                    if (dataBeans.get(getAdapterPosition()).getStatus()) {

                        dataBeans.get(getAdapterPosition()).setStatus(false);
                        mCallback.onSubCat(dataBeans.get(getAdapterPosition()).getId());
                        //  mListener.onSubCategoryRemove(dataBeans.get(getAdapterPosition()).getSub_category_id(),dataBeans.get(getAdapterPosition()).getName(),frag_position);
                        EventBus.getDefault().post(new FilterEvent(AppConstants.SUBCATEGORY_REMOVE, dataBeans.get(getAdapterPosition()).getId(),
                                dataBeans.get(getAdapterPosition()).getName(), frag_position, dataBeans.get(getAdapterPosition()).getSub_category()));

                    } else {
                        dataBeans.get(getAdapterPosition()).setStatus(true);
                        mCallback.onSubCat(dataBeans.get(getAdapterPosition()).getId());
                        //  mListener.onSubCategoryAdd(dataBeans.get(getAdapterPosition()).getSub_category_id(), dataBeans.get(getAdapterPosition()).getName(),frag_position);

                        EventBus.getDefault().post(new FilterEvent(AppConstants.SUBCATEGORY_ADD, dataBeans.get(getAdapterPosition()).getId(),
                                dataBeans.get(getAdapterPosition()).getName(), frag_position, dataBeans.get(getAdapterPosition()).getSub_category()));

                    }

                    notifyDataSetChanged();
                }
                // }
            }
        }
    }

}
