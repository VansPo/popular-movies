package com.vans.movies.data;

import com.vans.movies.entity.ListResponse;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface DataSource {

    @GET("discover/movie")
    Observable<ListResponse> getMovies(@Query("api_key") String key, @Query("sort_by") String sortBy);
}
