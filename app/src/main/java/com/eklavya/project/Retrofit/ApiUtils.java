package com.eklavya.project.Retrofit;

public class ApiUtils {

    public static final String BASE_URL = "https://opentdb.com/";
    public static final String NUMBER_URL = "http://numbersapi.com/";

    public static ApiService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(ApiService.class);
    }

    public static NumberService getNumberService(){
        return NumberClient.getClient(NUMBER_URL).create(NumberService.class);
    }
}