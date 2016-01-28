package com.github.nitrico.mvp.sample3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import com.github.nitrico.mapviewpager.MapViewPager;
import com.github.nitrico.mvp.R;
import com.github.nitrico.mvp.Utils;
import com.google.android.gms.maps.SupportMapFragment;

public class Sample3Activity extends AppCompatActivity implements MapViewPager.Callback {

    private ViewPager viewPager;
    private MapViewPager mvp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_3);

        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setPageMargin(Utils.dp(this, 16));

        mvp = new MapViewPager.Builder(this)
                .mapFragment(map)
                .viewPager(viewPager)
                .adapter(new Sample3Adapter(getSupportFragmentManager()))
                //.markersAlpha(0.2f)
                //.mapPadding(0, dp(32), 0, getScreenHeight()/2)
                .mapOffset(Utils.dp(this, 32))
                .callback(this)
                .build();
    }

    @Override
    public void onMapViewPagerReady() {
        mvp.getMap().setPadding(0, Utils.dp(this, 32), 0, viewPager.getHeight());
    }

}
