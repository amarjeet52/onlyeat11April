package com.codebrew.clikat.adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codebrew.clikat.R;
import com.codebrew.clikat.activities.ImageSHow;
import com.codebrew.clikat.utils.StaticFunction;
import com.codebrew.clikat.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by cbl80 on 20/5/16.
 */
public class PagerImageAdapter extends PagerAdapter {
    private Context mContext;
    private List<String> images = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private ViewPager viewPager;
    private PopupWindow popupWindow;

    public PagerImageAdapter(Context context, List<String> images, ViewPager viewPager) {
        this.mContext = context;
        for (int i = 0; i < images.size(); i++) {
            if (!images.get(i).equals("")) {
                this.images.add(images.get(i));
            }
        }
        if (this.images.size() == 0)
        {
            this.images.add("");
        }
        this.viewPager = viewPager;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (images.size() <= 4)
            return images.size();
        else
            return 4;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final View itemView = mLayoutInflater.inflate(R.layout.pager_image, container, false);

        final ImageView sdvAds = itemView.findViewById(R.id.sdvAds);

        final String topBanner = images.get(position);


        StaticFunction.INSTANCE.loadImage(images.get(position),sdvAds,false,null,null);

     //   StaticFunction.INSTANCE.loadImage(images.get(position),sdvAds,false,null,null);

      /*  Glide.with(mContext).load(Uri.parse(images.get(position)))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.placeholder_product)).into(sdvAds);*/


        itemView.setOnClickListener(v -> {


            if (Build.VERSION.SDK_INT >= 21) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, sdvAds, "sdvAds");
                Intent intent = new Intent(mContext, ImageSHow.class);
                intent.putExtra("image", images.get(position));
                mContext.startActivity(intent, options.toBundle());
            } else {

                LayoutInflater layoutInflater
                        = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View popupView = layoutInflater.inflate(R.layout.pop_up, null);
                ImageView ivImg = popupView.findViewById(R.id.ivImg);

                StaticFunction.INSTANCE.loadImage(Uri.parse(topBanner).getPath(),ivImg,false,ivImg.getHeight(),ivImg.getWidth());


                ivImg.setOnTouchListener((v1, event) -> {
                    popupWindow.dismiss();
                    return true;
                });
                popupWindow = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindow.setAnimationStyle(R.style.Animation);
                popupWindow.setTouchable(true);
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);


                popupWindow.showAtLocation(itemView, Gravity.CENTER, 0, 0);

            }


        });
        container.addView(itemView);


      //  viewPager.setObjectForPosition(itemView, position);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout) object);

    }
}
