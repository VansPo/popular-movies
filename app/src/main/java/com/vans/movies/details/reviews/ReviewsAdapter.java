package com.vans.movies.details.reviews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vans.movies.R;
import com.vans.movies.entity.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private final Context context;
    private List<Review> items = new ArrayList<>();

    public ReviewsAdapter(Context ctx) {
        context = ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.review_item, parent, false), true);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Review item = items.get(position);
        holder.author.setText(item.author);
        holder.comment.setText(item.content);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View root;
        TextView author;
        TextView comment;

        public ViewHolder(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                root = itemView;
                author = (TextView) itemView.findViewById(R.id.author);
                comment = (TextView) itemView.findViewById(R.id.comment);
            }
        }
    }

    public void replace(List<Review> data) {
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void add(List<Review> data) {
        items.addAll(data);
        notifyDataSetChanged();
    }

}

