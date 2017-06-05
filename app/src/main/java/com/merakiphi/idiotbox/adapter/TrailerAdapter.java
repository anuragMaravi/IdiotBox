package com.merakiphi.idiotbox.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.merakiphi.idiotbox.R;
import com.merakiphi.idiotbox.model.Movie;

import java.util.List;

import static com.merakiphi.idiotbox.other.Contract.YOUTUBE_BASE_THUMBNAIL;
import static com.merakiphi.idiotbox.other.Contract.YOUTUBE_QUALITY_THUMBNAIL_MQ;

/**
 * Created by anuragmaravi on 30/01/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MyViewHolder>{

    private Context context;
    List<Movie> trailersList;
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_videos, parent, false);
        return new TrailerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Movie movie = trailersList.get(position);
        String url = YOUTUBE_BASE_THUMBNAIL + movie.getVideoKey() + YOUTUBE_QUALITY_THUMBNAIL_MQ;
        holder.textViewVideoType.setText(movie.getVideoType());
        holder.textViewTrailerName.setText(movie.getVideoName());
        Glide.with(context).load(url).into(holder.imageViewVideo);
        holder.imageViewVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchYoutubeVideo(movie.getVideoKey());
            }
        });
    }

    @Override
    public int getItemCount() {
        return trailersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewVideo;
        public TextView textViewTrailerName, textViewVideoType;
        public MyViewHolder(View view) {
            super(view);
            imageViewVideo = (ImageView) view.findViewById(R.id.imageViewVideo);
            textViewTrailerName = (TextView) view.findViewById(R.id.textViewTrailerName);
            textViewVideoType = (TextView) view.findViewById(R.id.textViewVideoType);
        }
    }

    public TrailerAdapter(Context context, List<Movie> trailersList){
        this.context = context;
        this.trailersList = trailersList;
    }

    public void watchYoutubeVideo(String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(webIntent);

        }
    }

}
