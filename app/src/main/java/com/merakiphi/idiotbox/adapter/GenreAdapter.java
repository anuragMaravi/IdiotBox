package com.merakiphi.idiotbox.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.merakiphi.idiotbox.R;
import com.merakiphi.idiotbox.activity.GenreListActivity;
import com.merakiphi.idiotbox.model.Movie;

import java.util.List;

/**
 * Created by anuragmaravi on 30/01/17.
 */

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.MyViewHolder>  {

    private Context mContext;
    private List<Movie> movieList = null;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_genre, parent, false);
        return new GenreAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Movie movie = movieList.get(position);
        holder.buttonGenre.setText(movie.getGenreName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GenreListActivity.class);
                intent.putExtra("genre_id", movie.getGenreId());
                intent.putExtra("genre_name", movie.getGenreName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public GenreAdapter(Context mContext, List<Movie> movieList) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public Button buttonGenre;
        public MyViewHolder(View view) {
            super(view);
            buttonGenre = (Button) view.findViewById(R.id.buttonGenre);
        }
    }
}
