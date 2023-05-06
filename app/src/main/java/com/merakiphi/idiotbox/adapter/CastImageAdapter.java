package com.merakiphi.idiotbox.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import androidx.viewpager.widget.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.merakiphi.idiotbox.R;
import com.merakiphi.idiotbox.model.Movie;
import com.merakiphi.idiotbox.other.BasicImageDownloader;
import com.merakiphi.idiotbox.other.BasicImageDownloader.ImageError;
import com.merakiphi.idiotbox.other.BasicImageDownloader.OnImageLoaderListener;

import java.io.File;
import java.util.List;

import static android.content.ContentValues.TAG;
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
        ImageView imageView = (ImageView)view.findViewById(R.id.large_image);
        ImageView imageViewDownload = (ImageView)view.findViewById(R.id.imageViewDownload);
        ImageView imageViewShare = (ImageView)view.findViewById(R.id.imageViewShare);
        TextView textViewIndex = (TextView) view.findViewById(R.id.textViewIndex);
        final Movie movie = movieList.get(position);
        final String imageUrl = API_IMAGE_BASE_URL  + API_IMAGE_SIZE_XXL + "/" + movie.getCastingProfilePath();
        Glide.with(context).load(imageUrl).into(imageView);
        textViewIndex.setText(String.valueOf(position + 1) + " of " + movieList.size());
        //Button to download image
        imageViewDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage(imageUrl, movie.getCastingId() + position );
            }
        });

        //Button to share image
        imageViewShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File ff = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                File.separator + "Idiot Box/Celebrity Images" + File.separator + movie.getCastingId() + position + ".jpeg");
                if(ff.exists()) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpeg");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() +
                            File.separator + "Idiot Box/Celebrity Images" + File.separator + movie.getCastingId() + position + ".jpeg"));
                    context.startActivity(Intent.createChooser(share, "Share Image"));
                }
                else {
                    downloadImage(imageUrl, movie.getCastingId() + position);
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpeg");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() +
                            File.separator + "Idiot Box/Celebrity Images" + File.separator + movie.getCastingId() + position + ".jpeg"));
                    context.startActivity(Intent.createChooser(share, "Share Image"));
                }

            }
        });

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void downloadImage(String url, final String name) {
        final BasicImageDownloader downloader = new BasicImageDownloader(new OnImageLoaderListener() {
            @Override
            public void onError(ImageError error) {
                Toast.makeText(context, "Error code " + error.getErrorCode() + ": " +
                        error.getMessage() +  ". This app requires storage permission. Give permission to download and share images.", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }

            @Override
            public void onProgressChange(int percent) {

            }

            @Override
            public void onComplete(Bitmap result) {
                final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
                        /* don't forget to include the extension into the file name */
                final File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "Idiot Box/Celebrity Images" + File.separator + name + "." + mFormat.name().toLowerCase());
                BasicImageDownloader.writeToDisk(myImageFile, result, new BasicImageDownloader.OnBitmapSaveListener() {
                    @Override
                    public void onBitmapSaved() {
                        Toast.makeText(context, "Image saved as: " + myImageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + myImageFile.getAbsolutePath())));
                    }

                    @Override
                    public void onBitmapSaveError(ImageError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onBitmapSaveError: " + "Error code " + error.getErrorCode() + ": " +
                                error.getMessage());
                        error.printStackTrace();
                    }


                }, mFormat, false);
            }
        });
        downloader.download(url, true);
    }
}