package com.codebrew.clikat.module.searchProduct.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codebrew.clikat.R;
import com.codebrew.clikat.databinding.ItemSearchSuggestionBinding;
import com.codebrew.clikat.modal.database.SearchCategoryModel;
import com.codebrew.clikat.utils.configurations.Configurations;
import com.codebrew.clikat.utils.customviews.ClikatTextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class SuggestionListAdapter extends RealmRecyclerViewAdapter<SearchCategoryModel, RecyclerView.ViewHolder> {


    private Searchcallback mcallback;

    public SuggestionListAdapter(OrderedRealmCollection<SearchCategoryModel> data) {
        super(data, true);
        // Only set this if the model class has a primary key that is also a integer or long.
        // In that case, {@code getItemId(int)} must also be overridden to return the key.

        setHasStableIds(true);
    }

    public void settingcallback(Searchcallback searchcallback) {
        mcallback = searchcallback;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemSearchSuggestionBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_search_suggestion, parent, false);
        binding.setColor(Configurations.colors);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolder viewHolder = (ViewHolder) holder;
        final SearchCategoryModel model = getItem(position);

        viewHolder.tvSearchSuggestion.setText(model.getName());
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }


    public interface Searchcallback {

        void getSearch(String keyword);

        void clearHistory();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ClikatTextView tvSearchSuggestion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSearchSuggestion = itemView.findViewById(R.id.tv_search_suggestion);

            tvSearchSuggestion.setOnClickListener(v -> {
                SearchCategoryModel obj = getItem(getAdapterPosition());

                mcallback.getSearch(obj.getName());
            });
        }

    }
}
