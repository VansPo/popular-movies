package com.vans.movies.entity;

import com.google.gson.annotations.SerializedName;

public class Trailer {

    public String id;
    public @SerializedName("iso_639_1") String iso;
    public String key;
    public String name;
    public String site;
    public String size;
    public String type;

}