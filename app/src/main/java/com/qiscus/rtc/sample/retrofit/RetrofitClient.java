package com.qiscus.rtc.sample.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Gustu Maulana Firmansyah on 21,June,2021  gustumaulanaf@gmail.com
 **/
public class RetrofitClient {
    public static Retrofit retrofit = null;
    public static String BASE_URL = "https://meetstage.qiscus.com:9090/app-platforms/";
    public static EndPoint get() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(EndPoint.class);
    }
}
