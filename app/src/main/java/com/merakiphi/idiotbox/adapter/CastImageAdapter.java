package com.merakiphi.idiotbox.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.merakiphi.idiotbox.model.Movie;

import java.util.List;

import static com.merakiphi.idiotbox.other.Contract.API_IMAGE_BASE_URL;
import static com.merakiphi.idiotbox.other.Contract.API_IMAGE_SIZE_XXL;

/**
 * Created by anuragmaravi on 22/03/17.
 */

public class CastImageAdapter extends PagerAdapter {
    Context context;
    private List<Movie> movieList = null;

    public CastImageAdapter(Context context, List<Movie> movieList){
        this.context=context;
        this.movieList = movieList;
    }
    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        final Movie movie = movieList.get(position);
        Glide.with(context).load(API_IMAGE_BASE_URL  + API_IMAGE_SIZE_XXL + "/" + movie.getCastingProfilePath()).into(imageView);
        ((ViewPager) container).addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }
}