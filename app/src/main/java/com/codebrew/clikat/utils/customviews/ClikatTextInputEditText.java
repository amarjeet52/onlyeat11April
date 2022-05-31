package com.codebrew.clikat.utils.customviews;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

import com.codebrew.clikat.utils.configurations.Configurations;

import java.lang.reflect.Field;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.graphics.drawable.DrawableCompat;

public class ClikatTextInputEditText extends AppCompatEditText {

    private int primaryColor;

    public ClikatTextInputEditText(Context context) {
        super(context);
    }

    public ClikatTextInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomisations();
    }

    public ClikatTextInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomisations();
    }

    private void setCustomisations() {
        try {
            primaryColor = Color.parseColor(Configurations.colors.primaryColor);
        } catch (Exception e) {
            return;
        }
        setTextColor(Color.parseColor("#000000"));
        if (hasFocus()) {
            Drawable background = getBackground();
            DrawableCompat.setTint(background, primaryColor);
            setBackground(background);
        }
        changeCursorColor(primaryColor);
    }

    private void changeCursorColor(int primaryColor) {
        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(this);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(this);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);

            Drawable[] drawables = new Drawable[2];
            Resources res = getContext().getResources();
            drawables[0] = res.getDrawable(mCursorDrawableRes);
            drawables[1] = res.getDrawable(mCursorDrawableRes);
            drawables[0].setColorFilter(primaryColor, PorterDuff.Mode.SRC_OUT);
            drawables[1].setColorFilter(primaryColor, PorterDuff.Mode.SRC_OUT);
            fCursorDrawable.set(editor, drawables);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onFocusChanged(final boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (primaryColor == 0) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (gainFocus) {
                    Drawable background = getBackground();
                    DrawableCompat.setTint(background, primaryColor);
                    setBackground(background);
                } else {
                    Drawable background = getBackground();
                    DrawableCompat.setTint(background, Color.parseColor(Configurations.colors.tabUnSelected));
                    setBackground(background);
                }
            }
        }, 100);
    }
}
