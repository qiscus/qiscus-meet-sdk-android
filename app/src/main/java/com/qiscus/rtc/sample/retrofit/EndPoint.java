package com.qiscus.rtc.sample.retrofit;

import com.qiscus.rtc.sample.model.VersionResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Gustu Maulana Firmansyah on 21,June,2021  gustumaulanaf@gmail.com
 **/
public interface EndPoint {
    @GET("android")
    Call<VersionResponse> getVersion();
}
