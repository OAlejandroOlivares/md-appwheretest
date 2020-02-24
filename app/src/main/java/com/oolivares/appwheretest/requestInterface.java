package com.oolivares.appwheretest;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface requestInterface {

    @GET("/api/session/login")
    Call<ResponseBody> login(@Query("email") String email, @Query("password") String password);

    @GET("/get-merchants")
    Call<ResponseBody> getMerchants();



    @Headers({
            "Accept: application/json"
    })
    @POST("/register-merchant")
    Call<ResponseBody> register(@Body Merchants registration);
}
