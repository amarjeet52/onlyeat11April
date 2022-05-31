package com.codebrew.clikat.utils.customviews;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.graphics.drawable.DrawableCompat;

import android.util.AttributeSet;

import com.codebrew.clikat.utils.configurations.Configurations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClickatTextInputLayout extends TextInputLayout {
    public ClickatTextInputLayout(Context context) {
        super(context);
        setUpperHintColor();
    }

    public ClickatTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpperHintColor();
    }

    public ClickatTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUpperHintColor();
    }

    private void setUpperHintColor() {
        /* Changing the floating text color */
        try {
            Field field = getClass().getSuperclass().getDeclaredField("focusedTextColor");
            field.setAccessible(true);
            int[][] states = new int[][]{
                    new int[]{}
            };
            int[] colors = new int[]{
                    Color.parseColor(Configurations.colors.primaryColor)
            };
            ColorStateList myList = new ColorStateList(states, colors);
            field.set(this, myList);

            Method method = getClass().getDeclaredMethod("updateLabelState", boolean.class);
            method.setAccessible(true);
            method.invoke(this, true);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*    Change bottom line color */

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            Drawable background = getBackground();
            DrawableCompat.setTint(background, Color.parseColor(Configurations.colors.primaryColor));
            setBackground(background);
        } else {
            Drawable background = getBackground();
            DrawableCompat.setTint(background, Color.parseColor("#928156")); // Default grey
            setBackground(background);
        }
    }
}
