package com.vans.movies.details;

import com.vans.movies.entity.Trailer;

import java.util.List;

public interface DetailsView {

    void showTrailers(List<Trailer> trailers);
}
