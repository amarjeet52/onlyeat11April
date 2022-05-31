package com.codebrew.clikat.utils;

import android.content.Context;
import android.graphics.PointF;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class LinearLayoutManagerWithSmoothScroller extends LinearLayoutManager {

    public LinearLayoutManagerWithSmoothScroller(Context context) {
        super(context, VERTICAL, false);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {

        RecyclerView.SmoothScroller smoothScroller = new OwnSmoothScroller(recyclerView.getContext());

        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    private class OwnSmoothScroller extends LinearSmoothScroller {
        private static final float MILLISECONDS_PER_INCH = 50f;

        OwnSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return LinearLayoutManagerWithSmoothScroller.this
                    .computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;
        }

        @Override
        protected int calculateTimeForScrolling(int dx) {
            return Math.min(super.calculateTimeForScrolling(dx), 300);
        }

        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
        }
    }
}
