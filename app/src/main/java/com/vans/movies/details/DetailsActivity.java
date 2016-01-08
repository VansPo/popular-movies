package com.vans.movies.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vans.movies.R;
import com.vans.movies.details.reviews.ReviewsActivity;
import com.vans.movies.entity.Movie;
import com.vans.movies.entity.Trailer;

import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";

    private ImageView image;
    private ImageView backgroundImage;
    private TextView description;
    private TextView title;
    private TextView language;
    private TextView releaseDate;
    private TextView rating;
    private ProgressBar trailersPb;
    private ViewGroup trailersContainer;
    private ViewGroup reviews;

    private Toolbar toolbar;

    private DetailsPresenter presenter;

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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        image = (ImageView) findViewById(R.id.image);
        backgroundImage = (ImageView) findViewById(R.id.background_image);
        description = (TextView) findViewById(R.id.description);
        title = (TextView) findViewById(R.id.original_title);
        language = (TextView) findViewById(R.id.language);
        releaseDate = (TextView) findViewById(R.id.release_date);
        rating = (TextView) findViewById(R.id.rating);
        trailersPb = (ProgressBar) findViewById(R.id.trailers_pb);
        trailersContainer = (ViewGroup) findViewById(R.id.trailers_container);
        reviews = (ViewGroup) findViewById(R.id.reviews_layout);

        ViewCompat.setTransitionName(image, VIEW_NAME_HEADER_IMAGE);

        Movie movie = getIntent().getParcelableExtra("data");
        setData(movie);

        presenter = new DetailsPresenter(this, String.valueOf(movie.id));
        presenter.init();
    }

    public void setData(@NonNull final Movie data) {
        hackTitle(data.title);
        title.setText(title.getText() + " " + data.originalTitle);
        language.setText(language.getText() + " " + data.originalLanguage);
        releaseDate.setText(releaseDate.getText() + " " + data.releaseDate);
        rating.setText(rating.getText() + " " + String.valueOf(data.voteAverage) + "/10" + " (" + data.voteCount + ")");
        description.setText(data.overview);
        Picasso.with(this)
                .load(data.getSmallPosterPath())
                .noFade()
                .error(android.R.color.white)
                .into(image);

        Picasso.with(this)
                .load(data.getMediumBackdropPath())
                .error(android.R.color.transparent)
                .into(backgroundImage);

        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ReviewsActivity.create(DetailsActivity.this, String.valueOf(data.id)));
            }
        });
    }

    public void showTrailers(List<Trailer> trailers) {
        trailersPb.setVisibility(View.GONE);
        if (!trailers.isEmpty()) {
            trailersContainer.removeAllViews();
            trailersContainer.setVisibility(View.VISIBLE);

            for (Trailer t : trailers) {
                trailersContainer.addView(new TrailerItem(trailersContainer, t).view);
            }
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

    private void hackTitle(final String title) {
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(title);
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }
}
