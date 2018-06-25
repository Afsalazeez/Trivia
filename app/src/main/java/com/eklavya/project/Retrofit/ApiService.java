package com.eklavya.project.Retrofit;

import com.eklavya.project.Data.TriviaList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("/api.php")
    Call<TriviaList> getQuestion(@Query("amount") String amount, @Query("category") String category, @Query("difficulty") String difficulty, @Query("type") String type);

}