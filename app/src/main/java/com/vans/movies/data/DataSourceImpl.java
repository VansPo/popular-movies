package com.vans.movies.data;

import com.vans.movies.BuildConfig;
import com.vans.movies.entity.ListResponse;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;

public class DataSourceImpl {

  private static final String endpoint ="http://api.themoviedb.org/3/";

  private DataSource source;

  public DataSourceImpl() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(endpoint)
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    source = retrofit.create(DataSource.class);
  }

  public Observable<ListResponse> getMovies(String sortBy) {
    return source.getMovies(BuildConfig.API_KEY, sortBy);
  }

}
