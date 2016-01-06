package com.vans.movies.main.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Movie item = items.get(position);
        //todo display year correctly
        String year = "(" + item.releaseDate.split("-")[0] + ")";
        holder.text.setText(item.title + " " + year);

        holder.rating.setRating((float) item.voteAverage / 2);
        String path = item.getPosterPath("http://image.tmdb.org/t/p/", "w185/");
        Picasso.with(context)
                .load(path)
                .into(holder.image);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                context.startActivity(DetailsActivity.create(context, item));
                DetailsActivity.startTransition(
                        (Activity) context,
                        DetailsActivity.create(context, item),
                        new Pair<View, String>(holder.image, DetailsActivity.VIEW_NAME_HEADER_IMAGE));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View root;
        ImageView image;
        TextView text;
        RatingBar rating;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            image = (ImageView) itemView.findViewById(R.id.image);
            text = (TextView) itemView.findViewById(R.id.title);
            rating = (RatingBar) itemView.findViewById(R.id.rating);
        }
    }

    public void replace(List<Movie> data) {
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }

    private void setStringAfterEllipsis(TextView textView, String original, String customEllipsis, int maxLines) {

        if (textView.getLayout().getEllipsisCount(maxLines - 1) == 0) {
            return; // Nothing to do
        }

        int start = textView.getLayout().getLineStart(0);
        int end = textView.getLayout().getLineEnd(textView.getLineCount() - 1);
        String displayed = textView.getText().toString().substring(start, end);
        int displayedWidth = getTextWidth(displayed, textView.getTextSize());

        String suffix = "\u2026" + customEllipsis;

        int textWidth;
        String newText = original;
        textWidth = getTextWidth(newText + suffix, textView.getTextSize());

        while (textWidth > displayedWidth) {
            newText = newText.substring(0, newText.length() - 1).trim();
            textWidth = getTextWidth(newText + suffix, textView.getTextSize());
        }

        textView.setText(newText + suffix);
    }

    private int getTextWidth(String text, float textSize) {
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), bounds);

        return (int) Math.ceil(bounds.width());
    }

}
