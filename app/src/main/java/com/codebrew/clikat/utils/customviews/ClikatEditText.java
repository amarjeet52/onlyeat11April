package com.codebrew.clikat.utils.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.codebrew.clikat.R;
import com.codebrew.clikat.utils.configurations.Configurations;

import java.lang.reflect.Field;

public class ClikatEditText extends AppCompatEditText {
    public ClikatEditText(Context context) {
        super(context);
    }

    public ClikatEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomisations(getType(attrs));
    }

    public ClikatEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomisations(getType(attrs));
    }

    private void setCustomisations(String type) {

        switch (type)
        {
            case "0":
            {
         /*       changeEditextColor(Configurations.colors.appBackground);
                setHintTextColor(Color.parseColor(Configurations.colors.textSubhead));
                setTextColor(Color.parseColor(Configurations.colors.textHead));
                setBackgroundColor(Color.parseColor(Configurations.colors.search_background));*/

                setTextColor(Color.parseColor(Configurations.colors.textHead));
                setHintTextColor(Color.parseColor(Configurations.colors.textSubhead));
                changeEditextColor(Configurations.colors.primaryColor);
                setBackgroundColor(Color.parseColor(Configurations.colors.search_background));
                break;
            }

            case "1":
            {
                setTextColor(Color.parseColor(Configurations.colors.textHead));
                changeEditextColor(Configurations.colors.primaryColor);
                break;
            }
        }
    }


    private void changeEditextColor(String color)
    {

        if (hasFocus()) {
            Drawable background = getBackground();
            DrawableCompat.setTint(background, Color.parseColor(color));
            setBackground(background);
        }
        changeCursorColor(color);
    }


    private void changeCursorColor(String color) {
        try {
          /*  Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
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
            drawables[0].setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN);
            drawables[1].setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);*/

            // Get the cursor resource id
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(this);

            // Get the editor
            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(this);

            // Get the drawable and set a color filter
            Drawable drawable = ContextCompat.getDrawable(this.getContext(), drawableResId);
            drawable.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN);
            Drawable[] drawables = {drawable, drawable};

            // Set the drawables
            field = editor.getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);



        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private String getType(AttributeSet attrs) {
        try {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ClikatEditText);
            String type = String.valueOf(a.getInt(R.styleable.ClikatEditText_type, 0));
            a.recycle();
            return type;
        } catch (Exception e) {
            return "-1";
        }
    }


//    @Override
//    protected void onFocusChanged(final boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
//        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (gainFocus) {
//                    Drawable background = getBackground();
//                    DrawableCompat.setTint(background, Color.parseColor("#4455ff"));
//                    setBackground(background);
//                } else {
//                    Drawable background = getBackground();
//                    DrawableCompat.setTint(background, Color.parseColor("#ff22b2"));
//                    setBackground(background);
//                }
//            }
//        }, 100);
//    }
}
