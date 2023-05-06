package com.merakiphi.idiotbox.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.NativeExpressAdView;
import com.merakiphi.idiotbox.R;
import com.merakiphi.idiotbox.activity.MovieDetailsActivity;
import com.merakiphi.idiotbox.model.SearchResults;

import java.util.List;

/**
 * Created by anuragmaravi on 30/01/17.
 */

public class GenreListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Context mContext;
    private List<Object> movieList = null;

    // A menu item view type.
    private static final int MENU_ITEM_VIEW_TYPE = 0;

    // The Native Express ad view type.
    private static final int NATIVE_EXPRESS_AD_VIEW_TYPE = 1;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case MENU_ITEM_VIEW_TYPE:
                View menuItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_genre_list, parent, false);
                return new GenreListAdapter.MyViewHolder(menuItemLayoutView);
            case NATIVE_EXPRESS_AD_VIEW_TYPE:
                // fall through
            default:
                View nativeExpressLayoutView = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.item_genre_list_native_ad,
                        parent, false);
                return new NativeExpressAdViewHolder(nativeExpressLayoutView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        int viewType = getItemViewType(position);
        switch (viewType) {
            case MENU_ITEM_VIEW_TYPE:
                final SearchResults movie = (SearchResults) movieList.get(position);
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                Glide.with(mContext).load(movie.getPosterPath()).into(myViewHolder.imageViewSearchPoster);
                myViewHolder.textViewName.setText(movie.getOriginalTitle());
                myViewHolder.textViewDate.setText(movie.getReleaseDate());
                myViewHolder.textViewVoteAverage.setText(movie.getVoteAverage());
                myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, MovieDetailsActivity.class);
                        intent.putExtra("movie_id", movie.getId());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);

                    }
                });
                break;
            case NATIVE_EXPRESS_AD_VIEW_TYPE:
                // fall through
            default:
                NativeExpressAdViewHolder nativeExpressHolder =
                        (NativeExpressAdViewHolder) holder;
                NativeExpressAdView adView =
                        (NativeExpressAdView) movieList.get(position);
                ViewGroup adCardView = (ViewGroup) nativeExpressHolder.itemView;
                // The NativeExpressAdViewHolder recycled by the RecyclerView may be a different
                // instance than the one used previously for this position. Clear the
                // NativeExpressAdViewHolder of any subviews in case it has a different
                // AdView associated with it, and make sure the AdView for this position doesn't
                // already have a parent of a different recycled NativeExpressAdViewHolder.
                if (adCardView.getChildCount() > 0) {
                    adCardView.removeAllViews();
                }
                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }

                // Add the Native Express ad to the native express ad view.
                adCardView.addView(adView);
        }
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public GenreListAdapter(Context mContext, List<Object> movieList) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewSearchPoster;
        public TextView textViewName, textViewDate, textViewVoteAverage;
        public MyViewHolder(View view) {
            super(view);
            imageViewSearchPoster = (ImageView) view.findViewById(R.id.imageViewSearchPoster);
            textViewName = (TextView) view.findViewById(R.id.textViewName);
            textViewDate = (TextView) view.findViewById(R.id.textViewDate);
            textViewVoteAverage = (TextView) view.findViewById(R.id.textViewVoteAverage);
        }
    }

    /**
     * The {@link NativeExpressAdViewHolder} class.
     */
    public class NativeExpressAdViewHolder extends RecyclerView.ViewHolder {

        NativeExpressAdViewHolder(View view) {
            super(view);
        }
    }

    /**
     * Determines the view type for the given position.
     */
    @Override
    public int getItemViewType(int position) {
        return (position % 8 == 0) ? NATIVE_EXPRESS_AD_VIEW_TYPE
                : MENU_ITEM_VIEW_TYPE;
    }
}
