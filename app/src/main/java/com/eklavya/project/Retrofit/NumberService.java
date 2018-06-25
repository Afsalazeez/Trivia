package com.eklavya.project.Retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface NumberService {

    @GET()
    Call<String> getStringResponse(@Url String url);
}
