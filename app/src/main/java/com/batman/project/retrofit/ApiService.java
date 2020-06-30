package com.batman.project.retrofit;



import com.batman.project.models.Details;
import com.batman.project.models.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("?apikey=3e974fca&s=batman")
    Call<MovieResponse> getBatmanMovie();

    @GET("?apikey=3e974fca&i")
    Call<Details> getDetails(@Query("i") String id);

}

