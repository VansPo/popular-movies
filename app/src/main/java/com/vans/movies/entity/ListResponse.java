package com.vans.movies.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListResponse<T> {

    public int id;
    public int page;
    public List<T> results;
    public @SerializedName("total_pages") int totalPages;
    public @SerializedName("total_results") int totalResults;
}
