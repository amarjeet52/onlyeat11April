package com.codebrew.clikat.retrofit;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.codebrew.clikat.BuildConfig;
import com.codebrew.clikat.data.constants.AppConstants;
import com.codebrew.clikat.preferences.DataNames;
import com.codebrew.clikat.preferences.Prefs;
import com.codebrew.clikat.utils.StaticFunction;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Rest client
 */
public class RestClient {
    private static Api apiServiceModel = null;
    private static Api apiAgentModel = null;

    /**
     * By using this method your response will be coming in  modal class object
     *
     * @return object of ApiService interface
     */
    public static Api getModalApiService(Context activity) {
        if (apiServiceModel == null) {
            OkHttpClient okHttpClient = new OkHttpClient();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient
                            .newBuilder()
                            .connectTimeout(90, TimeUnit.SECONDS)
                            .readTimeout(90, TimeUnit.SECONDS)
                            .writeTimeout(90, TimeUnit.SECONDS)
                            .addInterceptor(getHttpLog())
                            .addInterceptor(chain -> {

                                Request request;

                                if (!StaticFunction.INSTANCE.getAccesstoken(activity).isEmpty()) {
                                    request = chain.request()
                                            .newBuilder()
                                            .addHeader("secretDbKey", Config.Companion.getDB_SECRET_KEY())
                                            .addHeader("Authorization", StaticFunction.INSTANCE.getAccesstoken(activity))
                                            .addHeader("Accept-Language", AppConstants.Companion.getLANG_CODE())
                                            .build();
                                } else {
                                    request = chain.request()
                                            .newBuilder()
                                            .addHeader("secretDbKey",  Config.Companion.getDB_SECRET_KEY())
                                            .addHeader("Accept-Language",  AppConstants.Companion.getLANG_CODE())
                                            .build();
                                }

                                return chain.proceed(request);
                            }).build())
                    .build();

            apiServiceModel =
                    retrofit.create(Api.class);

//           // For object response which is default
//            RestAdapter restAdapter = new RestAdapter.Builder()
//                    .setEndpoint(Config.getBaseURL()).setErrorHandler(new ApiErrorHandler(activity))
//                    .build();

//            apiServiceModel = restAdapter.create(Api.class);
        }
        return apiServiceModel;
    }


    private static HttpLoggingInterceptor getHttpLog() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG)
        {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }
        return httpLoggingInterceptor;
    }



}