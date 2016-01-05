package com.vans.movies;

import com.vans.movies.data.DataSourceImpl;

public class Global {

  public static final String POPULARITY_DESC = "popularity.desc";
  public static final String VOTE_DESC = "vote_average.desc";

  public DataSourceImpl api = new DataSourceImpl();

  private static Global INSTANCE;
  private Global() { }

  public static Global get() {
    if (INSTANCE == null) INSTANCE = new Global();
    return INSTANCE;
  }



}
