package com.vans.movies.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Movie implements Parcelable {

    public int id;
    public
    @SerializedName("backdrop_path")
    String backdropPath;
    public
    @SerializedName("original_language")
    String originalLanguage;
    public
    @SerializedName("original_title")
    String originalTitle;
    public String overview;
    public
    @SerializedName("release_date")
    String releaseDate;
    public
    @SerializedName("poster_path")
    String posterPath;
    public String title;
    public double popularity;
    public
    @SerializedName("vote_average")
    double voteAverage;
    public
    @SerializedName("vote_count")
    long voteCount;

    String imgEndpoint = "http://image.tmdb.org/t/p/";

    public String getPosterPath(String endpoint, String size) {
        return endpoint + size + posterPath;
    }

    public String getSmallPosterPath() {
        return imgEndpoint + "w185/" + posterPath;
    }

    public String getMediumBackdropPath() {
        return imgEndpoint + "w500/" + backdropPath;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        backdropPath = in.readString();
        originalLanguage = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        title = in.readString();
        popularity = in.readDouble();
        voteAverage = in.readDouble();
        voteCount = in.readLong();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(backdropPath);
        dest.writeString(originalLanguage);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(posterPath);
        dest.writeString(title);
        dest.writeDouble(popularity);
        dest.writeDouble(voteAverage);
        dest.writeLong(voteCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
