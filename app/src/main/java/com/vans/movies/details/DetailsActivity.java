package com.vans.movies.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.vans.movies.R;
import com.vans.movies.entity.Movie;

public class DetailsActivity extends AppCompatActivity {

    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";

    private ImageView image;
    private Toolbar toolbar;

    public static void startTransition(Activity activity, Intent intent, Pair<View, String>... pairs) {
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                pairs
        );
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
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        ViewCompat.setTransitionName(image, VIEW_NAME_HEADER_IMAGE);

        Movie movie = getIntent().getParcelableExtra("data");
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailsFragment.EXTRA_MOVIE, movie);
            DetailsFragment fragment = new DetailsFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.details_container, fragment)
                    .commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
