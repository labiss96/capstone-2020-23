package com.capstone.moayo.util.retrofit;

import com.capstone.moayo.entity.Model.ModelForm;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ShareAPI {
    @POST("/getDogam")
    Call<ModelForm> requestDogam(@Query("dogamId") int dogamId);

    @POST("/createDogam")
    Call<Void> requestCreate(@Body ModelForm form);

    @DELETE("/deleteDogam")
    Call<Void> requsetDelete(@Body ModelForm form);
}