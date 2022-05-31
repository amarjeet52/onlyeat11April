package com.codebrew.clikat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codebrew.clikat.R;
import com.codebrew.clikat.databinding.ItemOthersReviewBinding;
import com.codebrew.clikat.modal.AppGlobal;
import com.codebrew.clikat.data.model.api.supplier_detail.ReviewList;
import com.codebrew.clikat.utils.StaticFunction;
import com.codebrew.clikat.utils.configurations.Configurations;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/*
 * Created by Ankit Jindal on 19/4/16.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private Context mContext;
    private List<ReviewList> list;

    public ReviewsAdapter(Context context, List<ReviewList> data) {
        this.mContext = context;
        this.list = data;
    }

    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        ItemOthersReviewBinding other_review = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_others_review, parent, false);
        other_review.setColor(Configurations.colors);
        return new ViewHolder(other_review.getRoot());

    }


    @Override
    public void onBindViewHolder(ReviewsAdapter.ViewHolder holder, int position) {


        holder.tvName.setText(list.get(position).getFirstname());
        if (list.get(position).getUserImage() != null) {

            StaticFunction.INSTANCE.loadImage(list.get(position).getUserImage(),holder.sdvImage,false,null,null);

         /*   Glide.with(mContext)
                    .load(list.get(position).getUserImage())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                            .centerCrop().override(60, 60))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.sdvImage);*/
        }
        holder.tvReview.setText(list.get(position).getComment());
        holder.ratingBar.setRating(list.get(position).getRating());

    }

    @Override
    public int getItemCount() {

        return list.size();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvReview;
        CircleImageView sdvImage;
        RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName=itemView.findViewById(R.id.tvName);
            tvReview=itemView.findViewById(R.id.tvReview);
            sdvImage=itemView.findViewById(R.id.sdvImage);
            ratingBar=itemView.findViewById(R.id.ratingBar);


   /*         tvName.setTypeface(AppGlobal.regular);
            tvReview.setTypeface(AppGlobal.regular);*/

        }

    }
}
