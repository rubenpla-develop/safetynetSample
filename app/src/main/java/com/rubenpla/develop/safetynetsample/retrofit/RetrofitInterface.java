package com.rubenpla.develop.safetynetsample.retrofit;

import com.rubenpla.develop.safetynetsample.model.JWSRequest;
import com.rubenpla.develop.safetynetsample.model.JWSResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by alten on 21/3/17.
 */

public interface RetrofitInterface {

    @POST("verify")
    Call<JWSResponse> getResult(@Body JWSRequest request, @Query("key") String apiKey);
}
