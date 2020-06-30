package com.batman.project.retrofit;

public class ApiUtils {

    private ApiUtils() {}

    public static final String BASE_URL = "http://www.omdbapi.com/";
    public static ApiService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(ApiService.class);
    }
}