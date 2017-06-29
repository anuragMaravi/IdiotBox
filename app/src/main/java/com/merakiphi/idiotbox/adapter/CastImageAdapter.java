package com.merakiphi.idiotbox.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.merakiphi.idiotbox.R;
import com.merakiphi.idiotbox.model.Movie;
import com.merakiphi.idiotbox.other.BasicImageDownloader;
import com.merakiphi.idiotbox.other.BasicImageDownloader.ImageError;
import com.merakiphi.idiotbox.other.BasicImageDownloader.OnImageLoaderListener;

import java.io.File;
import java.util.List;

import static com.merakiphi.idiotbox.other.Contract.API_IMAGE_BASE_URL;
import static com.merakiphi.idiotbox.other.Contract.API_IMAGE_SIZE_XXL;

/**
 * Created by anuragmaravi on 22/03/17.
 */

public class CastImageAdapter extends PagerAdapter {
    private Context context;
    private List<Movie> movieList = null;
    private LayoutInflater layoutInflater;


    public CastImageAdapter(Context context, List<Movie> movieList){
        this.context=context;
        this.movieList = movieList;
        this.layoutInflater = (LayoutInflater)this.context.getSystemService(this.context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view == ((View) object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        View view = this.layoutInflater.inflate(R.layout.pager_list_items, container, false);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.getIndeterminateDrawable().setColorFilter(container.getResources().getColor(R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
        ImageView imageView = (ImageView)view.findViewById(R.id.large_image);
        ImageView imageViewDownload = (ImageView)view.findViewById(R.id.imageViewDownload);
        final Movie movie = movieList.get(position);
        Glide.with(context).load(API_IMAGE_BASE_URL  + API_IMAGE_SIZE_XXL + "/" + movie.getCastingProfilePath()).into(imageView);
        imageViewDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final BasicImageDownloader downloader = new BasicImageDownloader(new OnImageLoaderListener() {
                    @Override
                    public void onError(ImageError error) {
                        Toast.makeText(context, "Error code " + error.getErrorCode() + ": " +
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }

                    @Override
                    public void onProgressChange(int percent) {

                    }

                    @Override
                    public void onComplete(Bitmap result) {
                        /* save the image - I'm gonna use JPEG */
                        final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
                        /* don't forget to include the extension into the file name */
                        final File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                File.separator + "Idiot Box/Celebrity Images" + File.separator + movie.getCastingId() + position + "." + mFormat.name().toLowerCase());
                        BasicImageDownloader.writeToDisk(myImageFile, result, new BasicImageDownloader.OnBitmapSaveListener() {
                            @Override
                            public void onBitmapSaved() {
                                Toast.makeText(context, "Image saved as: " + myImageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onBitmapSaveError(ImageError error) {
                                Toast.makeText(context, "Error code " + error.getErrorCode() + ": " +
                                        error.getMessage(), Toast.LENGTH_LONG).show();
                                error.printStackTrace();
                            }


                        }, mFormat, false);
                    }
                });
                downloader.download(API_IMAGE_BASE_URL  + API_IMAGE_SIZE_XXL + "/" + movie.getCastingProfilePath(), true);            }
        });
        progressBar.setVisibility(View.GONE);
        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}