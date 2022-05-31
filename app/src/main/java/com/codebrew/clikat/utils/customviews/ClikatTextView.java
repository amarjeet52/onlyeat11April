package com.codebrew.clikat.utils.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

import com.codebrew.clikat.R;
import com.codebrew.clikat.utils.ColorTransparentUtils;
import com.codebrew.clikat.utils.configurations.Configurations;

public class ClikatTextView extends androidx.appcompat.widget.AppCompatTextView {
    public ClikatTextView(Context context) {
        super(context);
    }

    public ClikatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomization(getType(attrs));
    }

    public ClikatTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomization(getType(attrs));
    }

    private String getType(AttributeSet attrs) {
        try {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ClikatTextView);
            String type = String.valueOf(a.getInt(R.styleable.ClikatTextView_categoryType, 0));
            a.recycle();
            return type;
        } catch (Exception e) {
            return "-1";
        }
    }

    private void setCustomization(String type) {
        switch (type) {
            case "0": {
              //  setCustomTextColor(Configurations.colors.textAppTitle);

                break;
            }
            case "1": {
//                setTextColor(Configurations.colors.textHead);
                break;
            }
            case "2": {
//                setTextColor(Configurations.colors.textHead);
                break;
            }
            case "3": {
              //  setCustomTextColor(Configurations.colors.textHead);
                break;
            }
            case "4": {
              //  setCustomTextColor(Configurations.colors.textSubhead);
                break;
            }
            case "5": {
              //  setCustomTextColor(Configurations.colors.textBody);
                break;
            }
            case "6": {
              //  setCustomTextColor(Configurations.colors.textListHead);
                break;
            }
            case "7": {
               // setCustomTextColor(Configurations.colors.textListSubhead);
                break;
            }
            case "8": {
                //setCustomTextColor(Configurations.colors.textListBody);
                break;
            }
            case "9": {
               // setCustomTextColor(Configurations.colors.navTextColor);
                break;
            }
            case "10": {
               // setCustomTextColor(Configurations.colors.navSubTextColor);
                break;
            }
            case "11": {
               // setCustomTextColor(Configurations.colors.navTextColor);
                break;
            }
            case "12": {
              //  setCustomTextColor(Configurations.colors.appBackground);
                //setCustomTextBackground(Configurations.colors.primaryColor);
               // setBackgroundColor(Color.parseColor(Configurations.colors.textSubhead));
                break;
            }
            case "14": {
              //  setCustomTextColor(Configurations.colors.primaryColor);
                break;
            }
            case "15": {
               // setCustomTextColor(Configurations.colors.appBackground);
               // setRoundBackgroundColor(Configurations.colors.primaryColor, 10);
            }
            case "16": {
               // setCustomTextColor(Configurations.colors.primaryColor);
              //  setRoundBackgroundColor(Configurations.colors.primaryColor, 10);
            }
            case "17": {
               // setRoundBackgroundColor(Configurations.colors.primaryColor, 3);
            }
            case "18": {
              //  setRoundBackgroundColor(Configurations.colors.search_background, 1);
            }
            case "19": {
              //  setCustomTextColor(Configurations.colors.appBackground);
               // setBackgroundColor(Color.parseColor(Configurations.colors.transparent_color));
            }
            case "20": {
              //  setCustomTextColor(Configurations.colors.load_more_text);
               // setBackgroundColor(Color.parseColor(Configurations.colors.load_more_bg));
            }
        }
    }

    private void setCustomTextBackground(String primaryColor) {
        setBackgroundColor(Color.parseColor(ColorTransparentUtils.transparentColor20(Color.parseColor(primaryColor))));
    }

    private void setCustomTextColor(String color) {
        setTextColor(Color.parseColor(color));
    }

    private void setRoundBackgroundColor(String color,int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(color));
        gd.setCornerRadius(radius);
/*        gd.setStroke(2, Color.WHITE);*/

        setBackground(gd);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text != null)
            super.setText(text, type);
    }

    public void setText(CharSequence text, CharSequence defaultText) {
        if (text == null)
            setText(defaultText);
        else
            setText(text);
    }

}
