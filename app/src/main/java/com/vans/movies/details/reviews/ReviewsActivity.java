package com.vans.movies.details.reviews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.vans.movies.R;
import com.vans.movies.entity.Review;

import java.util.List;

public class ReviewsActivity extends AppCompatActivity {

    private ReviewsPresenter presenter;
    private RecyclerView recycler;
    private SwipeRefreshLayout swipeLayout;
    private Snackbar snackbar;

    private ReviewsAdapter adapter;

    public static Intent create(Context context, String id) {
        Intent intent = new Intent(context, ReviewsActivity.class);
        intent.putExtra("id", id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Reviews");
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        recycler = (RecyclerView) findViewById(R.id.recycler);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);

        initView();

        String id = getIntent().getStringExtra("id");
        presenter = new ReviewsPresenter(this, id);
        presenter.init();
    }

    private void initView() {
        adapter = new ReviewsAdapter(this);
        recycler.setAdapter(adapter);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setLayoutManager(new LinearLayoutManager(this));
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getReviews();
            }
        });

        //pagination
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition()
                        >= (recyclerView.getAdapter().getItemCount() - 1)) {
                    presenter.nextPage();
                }
            }
        });
    }

    public void setData(List<Review> data) {
        showProgress(false);
        adapter.add(data);
    }

    public void clearData() {
        adapter.clear();
    }

    public void showError() {
        showProgress(false);
        if (snackbar == null || !snackbar.isShown()) {
            snackbar = Snackbar.make(recycler, "Couldn't get reviews", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swipeLayout.setRefreshing(true);
                    presenter.nextPage();
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }
    }

    public void hideError() {
        if (snackbar != null) snackbar.dismiss();
    }

    public void showProgress(boolean isProgress) {
        swipeLayout.setRefreshing(isProgress);
        hideError();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //fixme
        presenter.destroy();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
