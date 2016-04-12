package com.vans.movies.details;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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

public class DetailsFragment extends Fragment implements DetailsView {

    public static final String EXTRA_MOVIE = "extra_movie";

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

    private Movie item;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(EXTRA_MOVIE)) {
            item = getArguments().getParcelable(EXTRA_MOVIE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        image = (ImageView) rootView.findViewById(R.id.image);
        backgroundImage = (ImageView) rootView.findViewById(R.id.background_image);
        description = (TextView) rootView.findViewById(R.id.description);
        title = (TextView) rootView.findViewById(R.id.original_title);
        language = (TextView) rootView.findViewById(R.id.language);
        releaseDate = (TextView) rootView.findViewById(R.id.release_date);
        rating = (TextView) rootView.findViewById(R.id.rating);
        trailersPb = (ProgressBar) rootView.findViewById(R.id.trailers_pb);
        trailersContainer = (ViewGroup) rootView.findViewById(R.id.trailers_container);
        reviews = (ViewGroup) rootView.findViewById(R.id.reviews_layout);

//        ViewCompat.setTransitionName(image, VIEW_NAME_HEADER_IMAGE);

//        Movie movie = getIntent().getParcelableExtra("data");

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (item != null) {
            setData(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (item != null) {
            presenter = new DetailsPresenter(this, String.valueOf(item.id));
            presenter.init();
        }
    }

    public void setData(@NonNull final Movie data) {
        hackTitle(data.title);
        title.setText(title.getText() + " " + data.originalTitle);
        language.setText(language.getText() + " " + data.originalLanguage);
        releaseDate.setText(releaseDate.getText() + " " + data.releaseDate);
        rating.setText(rating.getText() + " " + String.valueOf(data.voteAverage) + "/10" + " (" + data.voteCount + ")");
        description.setText(data.overview);
        Picasso.with(getActivity())
                .load(data.getSmallPosterPath())
                .noFade()
                .error(android.R.color.white)
                .into(image);

        Picasso.with(getActivity())
                .load(data.getMediumBackdropPath())
                .error(android.R.color.transparent)
                .into(backgroundImage);

        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ReviewsActivity.create(getActivity(), String.valueOf(data.id)));
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

    private void hackTitle(final String title) {
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) getView().findViewById(R.id.toolbar_layout);
        if (collapsingToolbarLayout != null) {
            AppBarLayout appBarLayout = (AppBarLayout) getView().findViewById(R.id.app_bar);
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
                    } else if (isShow) {
                        collapsingToolbarLayout.setTitle("");
                        isShow = false;
                    }
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.destroy();
    }
}
