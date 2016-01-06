package com.vans.movies.main;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.vans.movies.Global;
import com.vans.movies.R;
import com.vans.movies.entity.Movie;
import com.vans.movies.main.adapter.MoviesAdapter;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private MainPresenter presenter;
  private RecyclerView recycler;
  private SwipeRefreshLayout swipeLayout;
  private Snackbar snackbar;

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
    recycler.setItemAnimator(new DefaultItemAnimator());
    recycler.setLayoutManager(
        new GridLayoutManager(this, getResources().getInteger(R.integer.grid_layout_span)));
    swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        presenter.getMovies();
      }
    });
  }

  public void setData(List<Movie> data) {
    showProgress(false);
    adapter.replace(data);
  }

  public void showError() {
    showProgress(false);
    snackbar = Snackbar.make(recycler, "Couldn't get movies list", Snackbar.LENGTH_INDEFINITE);
    snackbar.setAction("Retry", new View.OnClickListener() {
      @Override public void onClick(View v) {
        swipeLayout.setRefreshing(true);
        presenter.getMovies();
        snackbar.dismiss();
      }
    });
    snackbar.show();
  }

  public void hideError() {
    if (snackbar != null) snackbar.dismiss();
  }

  public void showProgress(boolean isProgress) {
    swipeLayout.setRefreshing(isProgress);
    hideError();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    //fixme
    presenter.destroy();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    MenuItem item = menu.findItem(R.id.spinner);
    Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
    ArrayAdapter<String> spinnerArrayAdapter =
        new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.order));
    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(spinnerArrayAdapter);
    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
          case 0:
            presenter.setSort(Global.POPULARITY_DESC);
            break;
          case 1:
            presenter.setSort(Global.VOTE_DESC);
            break;
        }
      }
      @Override public void onNothingSelected(AdapterView<?> parent) {}
    });
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.spinner) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
