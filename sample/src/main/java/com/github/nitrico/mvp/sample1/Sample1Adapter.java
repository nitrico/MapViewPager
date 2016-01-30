package com.github.nitrico.mvp.sample1;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.github.nitrico.mapviewpager.MapViewPager;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class Sample1Adapter extends MapViewPager.Adapter {

    public static final String[] TITLES = { "London", "Paris", "Barcelona", "Milan", "Brussels" };

    public static final CameraPosition[] POSITIONS = {
            CameraPosition.fromLatLngZoom(new LatLng(51.5287352,-0.381784), 6f),
            CameraPosition.fromLatLngZoom(new LatLng(48.859,2.2074722), 6f),
            CameraPosition.fromLatLngZoom(new LatLng(41.3948976,2.0787274), 6f),
            CameraPosition.fromLatLngZoom(new LatLng(45.4628329,9.107692), 6f),
            CameraPosition.fromLatLngZoom(new LatLng(50.8550625,4.3053499), 6f)
    };

    public Sample1Adapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        return Sample1Fragment.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public CameraPosition getCameraPosition(int position) {
        return POSITIONS[position];
    }

}
