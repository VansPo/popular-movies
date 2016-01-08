package com.vans.movies.data;

import com.vans.movies.BuildConfig;
import com.vans.movies.entity.ListResponse;
import com.vans.movies.entity.Movie;
import com.vans.movies.entity.Review;
import com.vans.movies.entity.Trailer;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;

public class DataSourceImpl {

    private static final String endpoint = "http://api.themoviedb.org/3/";

    private DataSource source;

    public DataSourceImpl() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(endpoint)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        source = retrofit.create(DataSource.class);
    }

    public Observable<ListResponse<Movie>> getMovies(String sortBy, int page) {
        return source.getMovies(BuildConfig.API_KEY, sortBy, page);
    }

    public Observable<ListResponse<Trailer>> getTrailers(String id) {
        return source.getTrailers(id, BuildConfig.API_KEY);
    }

    public Observable<ListResponse<Review>> getReviews(String id, int page) {
        return source.getReviews(id, page, BuildConfig.API_KEY);
    }

}
