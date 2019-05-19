package com.api;

import com.model.Heroes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface API {

    @POST("heroes")
    Call<Void> addHero(@Body Heroes heroes);

}
