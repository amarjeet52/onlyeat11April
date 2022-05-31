package com.codebrew.clikat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.codebrew.clikat.R;
import com.codebrew.clikat.modal.other.ProductDataBean;
import com.codebrew.clikat.modal.other.RateMyProductModel;
import com.codebrew.clikat.modal.other.ScheduleListModel;
import com.codebrew.clikat.modal.other.TrackOrderList;
import com.codebrew.clikat.utils.GeneralFunctions;
import com.codebrew.clikat.utils.StaticFunction;

import java.util.ArrayList;
import java.util.List;


/*
 * Created by cbl80 on 20/4/16.
 */
public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.View_holder> {

    private Context mContext;
    private List<ProductDataBean> list;
    private List<TrackOrderList.DataBean.OrderListBean.ProductBean> track_productList;
    private ArrayList<RateMyProductModel.DataBean.OrderListBean.ProductBean> rate_productList;

    public ImagesAdapter(Context context, List<ProductDataBean> list) {
        this.mContext = context;
        this.list = list;
    }

    public ImagesAdapter(List<TrackOrderList.DataBean.OrderListBean.ProductBean> track_productList, Context context) {
        this.mContext = context;
        this.track_productList = track_productList;
    }

    public ImagesAdapter(ArrayList<RateMyProductModel.DataBean.OrderListBean.ProductBean> rate_productList, Context context) {
        this.mContext = context;
        this.rate_productList = rate_productList;
    }

    @Override
    public View_holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_image_square, parent, false);
        return new View_holder(view);
    }

    @Override
    public void onBindViewHolder(View_holder holder, int position) {
        int dimen = (int) (GeneralFunctions.getScreenWidth(mContext) / 3.3);
        holder.itemView.getLayoutParams().width = dimen;
        holder.itemView.getLayoutParams().height = dimen;


        if (list != null) {

            StaticFunction.INSTANCE.loadImage(list.get(position).getImage_path().toString(),holder.sdvProduct,false,null,null);

      /*      Glide.with(mContext)
                    .load(list.get(position).getImage_path())
                    .apply(new RequestOptions()
                            .fitCenter().override(160, 160))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.sdvProduct);*/
        } else if (rate_productList!=null) {


            StaticFunction.INSTANCE.loadImage(rate_productList.get(position).getImage_path(),holder.sdvProduct,false,null,null);

          /*  Glide.with(mContext)
                    .load(rate_productList.get(position).getImage_path())
                    .apply(new RequestOptions()
                            .fitCenter().override(160, 160))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.sdvProduct);*/
        } else {

            StaticFunction.INSTANCE.loadImage(track_productList.get(position).getImage_path(),holder.sdvProduct,false,null,null);

      /*      Glide.with(mContext)
                    .load(track_productList.get(position).getImage_path())
                    .apply(new RequestOptions()
                            .fitCenter().override(160, 160))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.sdvProduct);*/
        }
    }

    @Override
    public int getItemCount() {

        if (list != null) {
            return list.size();
        } else if (rate_productList != null) {
            return rate_productList.size();
        } else {
            return track_productList.size();
        }
    }

    public class View_holder extends RecyclerView.ViewHolder {
        ImageView sdvProduct;

        public View_holder(View itemView) {
            super(itemView);
            sdvProduct=itemView.findViewById(R.id.sdvProduct);
        }
    }
}
