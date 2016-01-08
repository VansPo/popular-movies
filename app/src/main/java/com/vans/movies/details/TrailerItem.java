package com.vans.movies.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vans.movies.R;
import com.vans.movies.entity.Trailer;

public class TrailerItem {

    public View view;
    public Context context;
    public Trailer item;

    private ImageView image;
    private TextView name;

    public TrailerItem(ViewGroup parent, Trailer t) {
        item = t;
        context = parent.getContext();
        view = ((Activity) context).getLayoutInflater().inflate(R.layout.trailer_item, parent, false);
        image = (ImageView) view.findViewById(R.id.image);
        name = (TextView) view.findViewById(R.id.name);

        setData(item);
    }

    public void setData(final Trailer t) {
        Picasso.with(context)
                .load("http://img.youtube.com/vi/"+t.key+"/mqdefault.jpg")
                .error(android.R.color.darker_gray)
                .into(image);
        name.setText(t.name);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + t.key))
                );
            }
        });
    }

}
