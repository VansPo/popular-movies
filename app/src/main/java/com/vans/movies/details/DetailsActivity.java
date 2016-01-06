package com.vans.movies.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vans.movies.R;
import com.vans.movies.entity.Movie;

public class DetailsActivity extends AppCompatActivity {

    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";

    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_TITLE = "detail:header:title";

    private ImageView image;
    private ImageView backgroundImage;
    private TextView description;
    private Toolbar toolbar;

    public static void startTransition(Activity activity, Intent intent, Pair<View, String>... pairs) {
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                pairs
        );

        // Now we can start the Activity, providing the activity options as a bundle
        ActivityCompat.startActivity(activity, intent, activityOptions.toBundle());
    }

    public static Intent create(Context context, Movie movie) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra("data", movie);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        image = (ImageView) findViewById(R.id.image);
        backgroundImage = (ImageView) findViewById(R.id.background_image);
        description = (TextView) findViewById(R.id.description);

        ViewCompat.setTransitionName(image, VIEW_NAME_HEADER_IMAGE);
//        ViewCompat.setTransitionName(toolbar, VIEW_NAME_HEADER_TITLE);


        Movie movie = getIntent().getParcelableExtra("data");
        setData(movie);
    }

    public void setData(@NonNull Movie data) {
        //todo
        setTitle(data.title);
        description.setText(data.overview);
        Picasso.with(this)
                .load(data.getSmallPosterPath())
                .noFade()
                .into(image);

        Picasso.with(this)
                .load(data.getMediumBackdropPath())
                .noFade()
                .into(backgroundImage);
    }

}
