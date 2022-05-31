package com.codebrew.clikat.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codebrew.clikat.BuildConfig;
import com.codebrew.clikat.R;
import com.codebrew.clikat.utils.configurations.Configurations;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/*
 * Created by Ankit Jindal on 7/23/2015.
 */
public class GeneralFunctions {

    public static final String JPEG_FILE_PREFIX = "IMG_";
    public static final String JPEG_FILE_SUFFIX = ".jpg";


    public static String getFormattedDate(String dob) {
        try {
            return new SimpleDateFormat(
                    "MMM, d EEEE").format(new SimpleDateFormat("yyyy-MM-dd").parse(dob));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static long getmin(String dob) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = formatter.parse(dob);
        return date.getTime();
    }


    public static long getminWithoutTime(String dob) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse(dob);
        return date.getTime();
    }

    public static String getFormattedUTC(String dob) throws ParseException {


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = formatter.parse(dob);
//        new SimpleDateFormat("MM/dd/yyyy KK:mm:ss a Z").format(dateObj);
        try {
            return new SimpleDateFormat(
                    "MM/dd/yyyy hh:mm:ss a Z").format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getYear(String dob) throws ParseException {

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
            Date date = formatter.parse(dob);

            return new SimpleDateFormat(
                    "yyyy").format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFormattedDateSide(String dob) throws ParseException {


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = formatter.parse(dob);
//        new SimpleDateFormat("MM/dd/yyyy KK:mm:ss a Z").format(dateObj);
        try {
            return new SimpleDateFormat(
                    "MMM, d EE hh:mm a").format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String getFormattedDateSkip(String dob) throws ParseException {


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = formatter.parse(dob);
//        new SimpleDateFormat("MM/dd/yyyy KK:mm:ss a Z").format(dateObj);
        try {
            return new SimpleDateFormat(
                    "dd MMM HH:mm").format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static Date getDayOfweek(String dob) throws ParseException {
        try {
            return new SimpleDateFormat("yyyy-MM-d").parse(dob);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date getDayOfweek11(String dob) throws ParseException {
        try {
            return new SimpleDateFormat("yyyy-MM-d HH:mm").parse(dob);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFullName(String firstName, String lastName) {
        return lastName.isEmpty() ? firstName : firstName + " " + lastName;
    }

    public static void hideKeyboard(View view, Context context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void force_layout_to_RTL(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public static void force_layout_to_LTR(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                .matches();
    }


    public static int getScreenWidth(Context activity) {
        WindowManager w = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            w.getDefaultDisplay().getSize(size);
            return size.x;
        } else {
            return w.getDefaultDisplay().getWidth();
        }
    }

    public static int getScreenHeight(Context activity) {
        WindowManager w = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            w.getDefaultDisplay().getSize(size);
            return size.y;
        } else {
            return w.getDefaultDisplay().getHeight();
        }
    }

    public static void replaceFragment(FragmentManager fragmentManager,
                                       Fragment fragment, String tag, int containerId) {
        fragmentManager.beginTransaction().
                replace(containerId, fragment, tag).commit();
    }


    public static void addFragment(FragmentManager fragmentManager,
                                   Fragment fragment, String tag, int containerId) {
        try {
            fragmentManager.beginTransaction().
                    add(containerId, fragment, tag).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static TimeZone getTimezone() {
        return TimeZone.getDefault();
    }


    public static void shareApp(Context context,String shareBody) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_sub, context.getString(R.string.app_name)));

        context.startActivity(Intent.createChooser(sharingIntent, "Share using:"));
    }

    public static void shareData(Context context, String supplierId, String branchId, String categoryId
            , String supplierName) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        String name = context.getString(R.string.share_supplier);
        name = name.replace(context.getString(R.string.ankiy), supplierName);
        String sIos;
        sIos = "clikat://www.clikat.com/supplier?supplierId=" + supplierId + "&branchId=" + branchId + "&categoryId=" + categoryId;
        name = name + "\n" + sIos;
        share.putExtra(Intent.EXTRA_TEXT, name);
        context.startActivity(Intent.createChooser(share, context.getString(R.string.sup, Configurations.strings.supplier)));
    }


    public static File setUpImageFile(String directory) throws IOException {
        File imageFile = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            File storageDir = new File(directory);
            if (!storageDir.mkdirs()) {
                if (!storageDir.exists()) {
                    Log.d("CameraSample", "failed to create directory");
                    return null;
                }
            }

            imageFile = File.createTempFile(JPEG_FILE_PREFIX
                            + System.currentTimeMillis() + "_",
                    JPEG_FILE_SUFFIX, storageDir);
        }
        return imageFile;
    }

    public static void showSnackBar(View view, String contentMsg, Context mContext) {
        if (mContext != null && view != null) {
            Snackbar.make(view, contentMsg, Snackbar.LENGTH_LONG)
                    .setAction(mContext.getString(R.string.action_okay), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                    .show();
        }
    }

    public static String getFormattedTime(int minutes, Context context) {
        String s;
        if (minutes < 60 || minutes % 60 != 0) {
            s = minutes + " " + context.getString(R.string.min);
        } else {
            int hours = minutes / 60;

            if (hours >= 24) {
                int day = hours / 24;
                if (day > 1)
                    s = day + " " + context.getString(R.string.days);
                else
                    s = day + " " + context.getString(R.string.day);
            } else {
                if (hours <= 1)
                    s = hours + " " + context.getString(R.string.hour);
                else
                    s = hours + " " + context.getString(R.string.hours);
            }
        }
        return s;
    }

    public static long get12HrFormatedtimeMilii(String time) {
        if (time.trim().equals("")) {
            return 0;
        } else {
            Date dateObj = null;
            try {
                final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                dateObj = sdf.parse(time);
            } catch (final ParseException e) {
                e.printStackTrace();
            }
            return dateObj.getTime();
        }
    }

    public static String get12HrFormatedtime(String time) {
        if (time.trim().equals("")) {
            return "";
        } else {
            String result = "";
            try {
                final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                final Date dateObj = sdf.parse(time);
                result = new SimpleDateFormat("hh:mm a").format(dateObj);
            } catch (final ParseException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}