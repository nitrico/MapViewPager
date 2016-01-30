package com.github.nitrico.mvp.sample1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.github.nitrico.mapviewpager.MapViewPager;
import com.github.nitrico.mvp.R;

public class Sample1Activity extends AppCompatActivity implements MapViewPager.Callback {

    private MapViewPager mapViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_1);
        mapViewPager = (MapViewPager) findViewById(R.id.mapViewPager);

        Sample1Adapter adapter = new Sample1Adapter(getSupportFragmentManager());
        mapViewPager.start(this, adapter, this);
    }

    @Override
    public void onMapViewPagerReady() {
        /*mapViewPager.getMarker(1)
                .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
         mapViewPager.getViewPager().addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                for (int i=0; i<mapViewPager.getMarkers().size(); i++) {
                    Marker marker = mapViewPager.getMarker(i);
                    if (i == position) marker.setAlpha(1f);
                    else marker.setAlpha(0.5f);
                }
            }
        });*/
    }

}
