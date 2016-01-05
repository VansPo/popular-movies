package com.vans.movies.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.vans.movies.R;
import com.vans.movies.details.DetailsActivity;
import com.vans.movies.entity.Movie;
import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

  private final Context context;
  private List<Movie> items = new ArrayList<>();

  public MoviesAdapter(Context ctx) {
    context = ctx;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false));
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    final Movie item = items.get(position);
    holder.text.setText(item.title);
    String path = item.getPosterPath("http://image.tmdb.org/t/p/", "w185/");
    Picasso.with(context)
        .load(path)
        .into(holder.image);

    holder.root.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        context.startActivity(DetailsActivity.create(context, item));
      }
    });
  }

  @Override public int getItemCount() {
    return items.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    View root;
    ImageView image;
    TextView text;

    public ViewHolder(View itemView) {
      super(itemView);
      root = itemView;
      image = (ImageView) itemView.findViewById(R.id.image);
      text = (TextView) itemView.findViewById(R.id.title);
    }
  }

  public void replace(List<Movie> data) {
    items.clear();
    items.addAll(data);
    notifyDataSetChanged();
  }
}
