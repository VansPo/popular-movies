package com.vans.movies.entity;

import java.util.ArrayList;
import java.util.List;

public class ListResponse {

  public int page;
  public List<Movie> results = new ArrayList<>();
  public int totalPages;
  public int totalResults;
}
