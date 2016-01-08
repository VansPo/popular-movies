package com.vans.movies.entity;

import java.util.List;

public class ListResponse<T> {

    public int id;
    public int page;
    public List<T> results;
    public int totalPages;
    public int totalResults;
}
