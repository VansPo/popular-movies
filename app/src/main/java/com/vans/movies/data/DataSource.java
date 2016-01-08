package com.vans.movies.data;

import com.vans.movies.entity.ListResponse;
import com.vans.movies.entity.Movie;
import com.vans.movies.entity.Review;
import com.vans.movies.entity.Trailer;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface DataSource {

    @GET("discover/movie")
    Observable<ListResponse<Movie>> getMovies(
            @Query("api_key") String key,
            @Query("sort_by") String sortBy,
            @Query("page") int page
    );

    @GET("movie/{id}/videos")
    Observable<ListResponse<Trailer>> getTrailers(@Path("id") String id, @Query("api_key") String key);

    @GET("movie/{id}/reviews")
    Observable<ListResponse<Review>> getReviews(
            @Path("id") String id,
            @Query("page") int page,
            @Query("api_key") String key
    );

}
