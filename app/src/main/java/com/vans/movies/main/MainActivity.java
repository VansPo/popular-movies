package com.vans.movies.main;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.vans.movies.R;
import com.vans.movies.entity.Movie;
import com.vans.movies.main.adapter.MoviesAdapter;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private MainPresenter presenter;
  private RecyclerView recycler;
  private SwipeRefreshLayout swipeLayout;

  private MoviesAdapter adapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    recycler = (RecyclerView) findViewById(R.id.recycler);
    swipeLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);

    initView();

    presenter = new MainPresenter(this);
    presenter.init();
  }

  private void initView() {
    adapter = new MoviesAdapter(this);
    recycler.setAdapter(adapter);
    recycler.setLayoutManager(
        new GridLayoutManager(this, getResources().getInteger(R.integer.grid_layout_span)));
    swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        presenter.getMovies();
      }
    });
  }

  public void setData(List<Movie> data) {
    swipeLayout.setRefreshing(false);
    adapter.replace(data);
  }

  public void showError() {
    swipeLayout.setRefreshing(false);
    final Snackbar snackbar = Snackbar.make(recycler, "Couldn't get movies list", Snackbar.LENGTH_INDEFINITE);
    snackbar.setAction("Retry", new View.OnClickListener() {
      @Override public void onClick(View v) {
        swipeLayout.setRefreshing(true);
        presenter.getMovies();
        snackbar.dismiss();
      }
    });
    snackbar.show();
  }

  @Override protected void onPause() {
    super.onPause();
    //fixme
    presenter.destroy();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
