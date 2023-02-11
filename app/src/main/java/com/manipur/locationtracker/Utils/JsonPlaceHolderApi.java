package com.manipur.locationtracker.Utils;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHolderApi {

    @GET("timestamp")
    Call<Long> getTime();
}
