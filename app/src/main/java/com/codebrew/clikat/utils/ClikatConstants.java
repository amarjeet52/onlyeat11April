package com.codebrew.clikat.utils;

import android.os.Environment;

/*
 * Created by Ankit Jindal on 18/4/16.
 */
public class ClikatConstants {
    public static final int ITEM_PROGRESS = 5;
    //language type constants
    public static  int LANGUAGE_OTHER=15;
    public static  int LANGUAGE_ENGLISH=14;

    public static String ENGLISH_FULL="english";
    public static String ENGLISH_SHORT="en";

    public static  String LANGUAGE_AR="ar";
    public static  String LANGUAGE_ES="es";
    public static  String LANGUAGE_EN ="en";

    public static int STATUS_SUCCESS=200;
    public static int STATUS_INVALID_TOKEN=401;
    // view type constants
    public static final int ITEM_VIEW_TYPE_AD = 0;
    public static final int ITEM_VIEW_TYPE_SERVICES = 1;
    public static final int ITEM_VIEW_TYPE_RECOMENDED = 2;
    public static final int ITEM_VIEW_TYPE_OFFERS = 3;
    public static final int ITEM_VIEW_TYPE_SEARCH = 4;

    public static final int GALLERY_REQUEST = 1;
    public static final int CAMERA_REQUEST = 1888;
    public static final String LOCAL_FILE_PREFIX = "file://";
    public static final String LOCAL_STORAGE_BASE_PATH_FOR_USER_PHOTOS =
            Environment.getExternalStorageDirectory() + "/Clikat" + "/User/Photos/";


    public static final String CLICKET_FACEBOOK="https://www.facebook.com/codebrewlabs";
    public static final String CLICKET_TWITTER="https://twitter.com/CodeBrewLabs";
    public static final String CLICKET_INSTAGRAM="https://www.instagram.com/codebrewlabs/";
    public static final String CLICKET_YOUTUBE="https://www.youtube.com/channel/UCh6EaKNcFhtxgF27KUQGw1w/videos";


}
