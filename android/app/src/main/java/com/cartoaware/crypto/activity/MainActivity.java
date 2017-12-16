package com.cartoaware.crypto.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.cartoaware.crypto.R;
import com.cartoaware.crypto.api.Api;
import com.cartoaware.crypto.fragment.ATMMapFragment;
import com.cartoaware.crypto.fragment.ExtrasFragment;
import com.cartoaware.crypto.fragment.WebViewFragment;
import com.cartoaware.crypto.utils.Constants;
import com.cartoaware.crypto.utils.SwipingViewPager;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationParams;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesWithFallbackProvider;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    @Bind(R.id.vp)
    SwipingViewPager viewPager;
    @Bind(R.id.page_ind)
    NavigationTabStrip navigationTabStrip;
    @Bind(R.id.loading)
    LottieAnimationView loading;

    private Context mContext;
    private ArrayList<String> mtitles;
    private ArrayList<Fragment> mFragments;
    private ClassAdapter attractionsAdapter;

    public Location lastLoc = null;

    private ViewPager.OnPageChangeListener classOPCL = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;

        getSupportActionBar().setElevation(0);

        Api.fetchAllATMs(mContext, null,
                new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            if (objects.size() > 0) {
                                try {
                                    ParseObject.pinAll(objects);
                                    MainActivityPermissionsDispatcher.requestPermWithCheck(MainActivity.this);
                                } catch (Exception e1) {
                                    Log.e("ATM", e1.toString());
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    public void requestPerm() {
        setupViews();
    }

    @OnShowRationale({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void showRationaleForLocation(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("Allowing permissions enable us to connect with more devices!")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void showDeniedForLocation() {
        setupViews();
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void showNeverAskForLocation() {
        setupViews();
    }

    private void setupViews() {
        SmartLocation.with(mContext)
                .location(new LocationGooglePlayServicesWithFallbackProvider(mContext))
                .config(LocationParams.BEST_EFFORT)
                .continuous()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        if (lastLoc == null) {
                            lastLoc = location;
                            setupPager();
                        } else {
                            lastLoc = location;
                        }
                    }
                });
    }

    private void setupPager() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mFragments = new ArrayList<>();
        mtitles = new ArrayList<>();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_URL, "https://coinranking.com/");
        mFragments.add(WebViewFragment.getInstance(bundle));
        mtitles.add("Markets");

        mFragments.add(new ATMMapFragment());
        mtitles.add("ATMs");

        mFragments.add(new ExtrasFragment());
        mtitles.add("Extras");

        attractionsAdapter = new ClassAdapter(mContext, fragmentManager, mtitles, mFragments);

        viewPager.setAdapter(attractionsAdapter);
        viewPager.setCurrentItem(1);
//        viewPager.setOffscreenPageLimit(3);
        navigationTabStrip.setViewPager(viewPager);
        navigationTabStrip.setOnPageChangeListener(classOPCL);
//        viewPager.setCurrentItem(1);
//        navigationTabStrip.onPageSelected(1);
//        navigationTabStrip.setOnTabStripSelectedIndexListener(new NavigationTabStrip.OnTabStripSelectedIndexListener() {
//            @Override
//            public void onStartTabSelected(String title, int index) {
//
//            }
//
//            @Override
//            public void onEndTabSelected(String title, int index) {
//
//            }
//        });
        loading.setVisibility(View.GONE);
    }

    public class ClassAdapter extends FragmentPagerAdapter {
        Context context;
        private ArrayList<String> titles;
        private ArrayList<Fragment> mFragments;

        public ClassAdapter(Context context, FragmentManager fragmentManager, ArrayList<String> strings, ArrayList<Fragment> fragments) {
            super(fragmentManager);
            this.context = context;
            this.titles = strings;
            this.mFragments = fragments;
        }

        @Override
        public int getCount() {
            return this.titles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public Fragment getItem(int i) {
            return mFragments.get(i);
        }
    }
}
