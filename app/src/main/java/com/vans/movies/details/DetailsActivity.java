package com.vans.movies.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import com.vans.movies.R;
import com.vans.movies.entity.Movie;

public class DetailsActivity extends AppCompatActivity {

  public static Intent create(Context context, Movie movie) {
    Intent intent = new Intent(context, DetailsActivity.class);
    intent.putExtra("data", movie);
    return intent;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Movie movie = getIntent().getParcelableExtra("movie");
    setData(movie);
  }

  public void setData(@NonNull Movie data) {
    //todo
  }

}
