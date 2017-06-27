package com.merakiphi.idiotbox.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.merakiphi.idiotbox.R;
import com.merakiphi.idiotbox.fragment.MoviesFragment;
import com.merakiphi.idiotbox.fragment.TvShowsFragment;

import org.jsoup.Jsoup;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

        private Toolbar toolbar;
    private TextView toolbar_title;
    private AdView mAdView;


    BottomNavigationView navigation;
    private static final String TAG = MainActivity.class.getSimpleName();
    private Fragment fragment,fragme;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Check for the update
//        String version = null;
//        try {
//            PackageManager manager = getPackageManager();
//            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
//            version = String.valueOf(info.versionName);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        VersionChecker versionChecker = new VersionChecker();
//        try {
//            String latestVersion = versionChecker.execute().get();
//            if(!latestVersion.equals(version)) {
//                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
//                alertDialog.setCancelable(false);
//                alertDialog.setTitle("Update Required");
//                alertDialog.setMessage("A new update is available for Idiot Box. Please update the app to continue.\n\nNew Version: " + latestVersion);
//
//                alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        final String appPackageName = getPackageName();
//                        try {
//                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.kkings.cinematics" + appPackageName)));
//                        } catch (android.content.ActivityNotFoundException anfe) {
//                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//                        }
//                        dialog.dismiss();
//                    }
//                });
//
//                alertDialog.setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//                });
//                alertDialog.show();
//            } else {
                setContentView(R.layout.activity_main);
                toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle(null);

                //Initialising AdMob
                MobileAds.initialize(this, "ca-app-pub-3259009684379327~5979085895");

                // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
                // values/strings.xml.
                mAdView = (AdView) findViewById(R.id.ad_view);

                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .build();

                // Start loading the ad in the background.
                mAdView.loadAd(adRequest);


                navigation = (BottomNavigationView) findViewById(R.id.navigation);
                navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
                fragmentManager = getSupportFragmentManager();
                fragme = new MoviesFragment();
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, fragme).commit();
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_documents:
                    toolbar_title =(TextView) findViewById(R.id.toolbar_title);
                    toolbar_title.setText("MOVIES");
                    toolbar_title.setTextColor(getResources().getColor(R.color.colorAccent));
                    fragment = new MoviesFragment();

                    navigation.getMenu().findItem(R.id.navigation_groups).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                    navigation.setItemTextColor(new ColorStateList(new int[][] {
                            new int[] {android.R.attr.state_checked}, // unchecked
                            new int[] {-android.R.attr.state_checked} // unchecked
                    }, new int[] {
                            getResources().getColor(R.color.colorAccent),
                            Color.WHITE
                    }));
                    break;
                case R.id.navigation_groups:
                    toolbar_title =(TextView) findViewById(R.id.toolbar_title);
                    toolbar_title.setText("TV SHOWS");
                    toolbar_title.setTextColor(getResources().getColor(R.color.tv_show_accent));
                    fragment = new TvShowsFragment();
                    navigation.getMenu().findItem(R.id.navigation_groups).getIcon().setColorFilter(getResources().getColor(R.color.tv_show_accent), PorterDuff.Mode.SRC_IN);
                    navigation.setItemTextColor(new ColorStateList(new int[][] {
                            new int[] {android.R.attr.state_checked}, // unchecked
                            new int[] {-android.R.attr.state_checked} // unchecked
                    }, new int[] {
                            getResources().getColor(R.color.tv_show_accent),
                            Color.WHITE
                    }));
                    break;
                default:
                    fragment = new MoviesFragment();
                    break;
            }
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            Log.i(TAG, "onNavigationItemSelected: " + getFragmentManager().getBackStackEntryCount());
            transaction.replace(R.id.container, fragment).commit();
            return true;
        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.search) {
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        startActivity(intent);}

        if (id == R.id.action_settings) {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);}

//        if (id == R.id.action_about_us) {
//        Intent intent = new Intent(getApplicationContext(), AboutUsActivity.class);
//        startActivity(intent);}

        return super.onOptionsItemSelected(item);
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    public class VersionChecker extends AsyncTask<String, String, String> {

        String newVersion;

        @Override
        protected String doInBackground(String... params) {

            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + "com.merakiphi.idiotbox")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return newVersion;
        }
    }
}

