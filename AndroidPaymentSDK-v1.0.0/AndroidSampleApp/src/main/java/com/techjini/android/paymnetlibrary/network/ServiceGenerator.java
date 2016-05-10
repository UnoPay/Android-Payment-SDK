package com.techjini.android.paymnetlibrary.network;

import android.util.Base64;

import com.techjini.android.paymnetlibrary.BuildConfig;
import com.techjini.android.paymnetlibrary.constants.NetworkConstants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Nitin S.Mesta on 27/4/16.
 * // Copyright (c) 2016 Techjini Solutions. All rights reserved.
 */public class ServiceGenerator {

    private static OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();


    public static <S> S createService(Class<S> serviceClass, String baseUrl,boolean isProduction) {


        String creds = "";

        if(isProduction) {
            creds = String.format("%s:%s", "android-buyer-9Vdob38S", "z70fnZdoiJ4mrI-OwqBZ");
        }else {
            creds = String.format("%s:%s", "developers", "unopay123");
        }
        final String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);

        httpBuilder.connectTimeout(NetworkConstants.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        httpBuilder.readTimeout(NetworkConstants.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        httpBuilder.retryOnConnectionFailure(false);
        httpBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", auth);
                requestBuilder.header("Accept", "application/json");
                requestBuilder.header("Content-Type", "application/json; charset=utf8");
                requestBuilder.method(original.method(), original.body());

                Request request = requestBuilder.build();

                return chain.proceed(request);
            }
        });

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (isProduction) {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        }
        httpBuilder.addInterceptor(logging);

        OkHttpClient client = httpBuilder.build();
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }
}
