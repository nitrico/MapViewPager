package com.github.nitrico.mvp.sample2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.github.nitrico.mapviewpager.MapViewPager;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import java.util.LinkedList;
import java.util.List;

public class Sample2Adapter extends MapViewPager.MultiAdapter {

    public static final String[] PAGE_TITLES = { "England", "France", "Spain",
            "Portugal", "Italy", "Belgium" };

    public static final String[] ENGLAND_TITLES = { "London" };
    public static final String[] FRANCE_TITLES = { "Paris" };
    public static final String[] SPAIN_TITLES = { "Barcelona", "Madrid", "Valencia" };
    public static final String[] PORTUGAL_TITLES = { };
    public static final String[] ITALY_TITLES = { "Milan", "Rome" };
    public static final String[] BELGIUM_TITLES = { "Brussels" };

    public static final CameraPosition LONDON
            = CameraPosition.builder().target(new LatLng(51.5287352,-0.381784)).zoom(6f).build();
    public static final CameraPosition PARIS
            = CameraPosition.builder().target(new LatLng(48.859,2.2074722)).zoom(6f).build();
    public static final CameraPosition BARCELONA
            = CameraPosition.builder().target(new LatLng(41.3948976,2.0787274)).zoom(6f).build();
    public static final CameraPosition MADRID
            = CameraPosition.builder().target(new LatLng(40.4381311,-3.8196227)).zoom(6f).build();
    public static final CameraPosition VALENCIA
            = CameraPosition.builder().target(new LatLng(39.4079665,-0.5015975)).zoom(6f).build();
    public static final CameraPosition MILAN
            = CameraPosition.builder().target(new LatLng(45.4628329,9.107692)).zoom(6f).build();
    public static final CameraPosition ROME
            = CameraPosition.builder().target(new LatLng(41.9102415,12.3959121)).zoom(6f).build();
    public static final CameraPosition BRUSSELS
            = CameraPosition.builder().target(new LatLng(50.8550625,4.3053499)).zoom(6f).build();

    private LinkedList<CameraPosition> england;
    private LinkedList<CameraPosition> france;
    private LinkedList<CameraPosition> spain;
    private LinkedList<CameraPosition> portugal;
    private LinkedList<CameraPosition> italy;
    private LinkedList<CameraPosition> belgium;

    public Sample2Adapter(FragmentManager fm) {
        super(fm);
        // camera positions
        england = new LinkedList<>();
        england.add(LONDON);
        france = new LinkedList<>();
        france.add(PARIS);
        spain = new LinkedList<>();
        spain.add(BARCELONA);
        spain.add(MADRID);
        spain.add(VALENCIA);
        portugal = new LinkedList<>();
        italy = new LinkedList<>();
        italy.add(MILAN);
        italy.add(ROME);
        belgium = new LinkedList<>();
        belgium.add(BRUSSELS);
    }

    @Override
    public int getCount() {
        return PAGE_TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        return Sample2Fragment.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return PAGE_TITLES[position];
    }

    @Override
    public String getMarkerTitle(int page, int position) {
        switch (page) {
            case 0: return ENGLAND_TITLES[position];
            case 1: return FRANCE_TITLES[position];
            case 2: return SPAIN_TITLES[position];
            case 3: return PORTUGAL_TITLES[position];
            case 4: return ITALY_TITLES[position];
            case 5: return BELGIUM_TITLES[position];
        }
        return null;
    }

    @Override
    public List<CameraPosition> getCameraPositions(int page) {
        switch (page) {
            case 0: return england;
            case 1: return france;
            case 2: return spain;
            case 3: return portugal;
            case 4: return italy;
            case 5: return belgium;
        }
        return null;
    }

}
