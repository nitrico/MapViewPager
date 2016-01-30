/*
 * Copyright (C) 2016 Miguel Ángel Moreno
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.nitrico.mapviewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.LinkedList;
import java.util.List;

public class MapViewPager extends FrameLayout implements OnMapReadyCallback {

    public interface Callback {
        void onMapViewPagerReady();
    }

    public static abstract class AbsAdapter extends FragmentStatePagerAdapter {
        public AbsAdapter(FragmentManager fm) {
            super(fm);
        }
    }

    public static abstract class Adapter extends AbsAdapter {
        public Adapter(FragmentManager fm) {
            super(fm);
        }
        public abstract CameraPosition getCameraPosition(int position);
    }

    public static abstract class MultiAdapter extends AbsAdapter {
        public MultiAdapter(FragmentManager fm) {
            super(fm);
        }
        public abstract List<CameraPosition> getCameraPositions(int page);
        public abstract String getMarkerTitle(int page, int position);
    }


    private static final float DEFAULT_ALPHA = 0.4f;
    private float markersAlpha = DEFAULT_ALPHA;
    private int mapGravity = 1;
    private int mapWeight = 1, pagerWeight = 1;
    private int mapPaddingLeft, mapPaddingTop, mapPaddingRight, mapPaddingBottom;
    private int mapOffset;

    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private ViewPager viewPager;
    private AbsAdapter adapter;
    private Callback callback;

    protected CameraUpdate defaultPosition;
    private List<CameraUpdate> defaultPositions;
    protected List<Marker> markers;
    private List<List<Marker>> allMarkers;

    private int initialPosition;
    private boolean hidden = false;


    public MapViewPager(Context context) {
        super(context); // not calling initialize(context) to use it with Builder
    }

    public MapViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public MapViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        mapOffset = dp(32);
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MapViewPager, 0, 0);
            try {
                markersAlpha = a.getFloat(R.styleable.MapViewPager_markersAlpha, DEFAULT_ALPHA);
                pagerWeight = a.getInteger(R.styleable.MapViewPager_viewPagerWeight, 1);
                mapWeight = a.getInteger(R.styleable.MapViewPager_mapWeight, 1);
                mapGravity = a.getInteger(R.styleable.MapViewPager_mapGravity, 1);
                mapOffset = a.getDimensionPixelSize(R.styleable.MapViewPager_mapOffset, mapOffset);
                mapPaddingLeft = a.getDimensionPixelSize(R.styleable.MapViewPager_mapPaddingLeft, 0);
                mapPaddingTop = a.getDimensionPixelSize(R.styleable.MapViewPager_mapPaddingTop, 0);
                mapPaddingRight = a.getDimensionPixelSize(R.styleable.MapViewPager_mapPaddingRight, 0);
                mapPaddingBottom = a.getDimensionPixelSize(R.styleable.MapViewPager_mapPaddingBottom, 0);
            } finally {
                a.recycle();
            }
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (mapGravity) {
            case 0: inflater.inflate(R.layout.mapviewpager_left,   this); break;
            case 1: inflater.inflate(R.layout.mapviewpager_top,    this); break;
            case 2: inflater.inflate(R.layout.mapviewpager_right,  this); break;
            case 3: inflater.inflate(R.layout.mapviewpager_bottom, this); break;
        }
    }

    public void start(@NonNull FragmentActivity activity,
                      @NonNull AbsAdapter mapAdapter) {
        start(activity, mapAdapter, 0, null);
    }

    public void start(@NonNull FragmentActivity activity,
                      @NonNull AbsAdapter mapAdapter,
                      @Nullable Callback callback) {
        start(activity, mapAdapter, 0, callback);
    }

    public void start(@NonNull FragmentActivity activity,
                      @NonNull AbsAdapter mapAdapter,
                      int initialPosition) {
        start(activity, mapAdapter, initialPosition, null);
    }

    public void start(@NonNull FragmentActivity activity,
                      @NonNull AbsAdapter mapAdapter,
                      int initialPosition,
                      @Nullable Callback callback) {
        this.initialPosition = initialPosition;
        this.callback = callback;
        adapter = mapAdapter;
        mapFragment = (SupportMapFragment) activity.getSupportFragmentManager().findFragmentById(R.id.map);
        viewPager = (ViewPager) findViewById(R.id.pager);
        setWeights();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setPadding(mapPaddingLeft, mapPaddingTop, mapPaddingRight, mapPaddingBottom);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(initialPosition);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                moveTo(position, true);
            }
        });
        populate();
        if (callback != null) callback.onMapViewPagerReady();
        moveTo(viewPager.getCurrentItem(), false);
        moveTo(viewPager.getCurrentItem(), true);
    }


    private void populate() {
        if (adapter instanceof MultiAdapter) populateMulti((MultiAdapter) adapter);
        else populateSingle((Adapter) adapter);
    }

    private void populateSingle(Adapter adapter) {
        map.clear();
        markers = new LinkedList<>();
        for (int i = 0; i < adapter.getCount(); i++) {
            CameraPosition cp = adapter.getCameraPosition(i);
            if (cp != null) {
                MarkerOptions mo = createMarkerOptions(cp, adapter.getPageTitle(i).toString());
                markers.add(map.addMarker(mo));
            }
            else markers.add(null);
        }
        map.setOnMarkerClickListener(createMarkerClickListenerSingle(adapter));
        initDefaultPosition();
    }

    private void populateMulti(final MultiAdapter adapter) {
        map.clear();
        allMarkers = new LinkedList<>();
        for (int page = 0; page < adapter.getCount(); page++) {
            LinkedList<Marker> pageMarkers = new LinkedList<>();
            if (adapter.getCameraPositions(page) != null) {
                for (int i = 0; i < adapter.getCameraPositions(page).size(); i++) {
                    CameraPosition cp = adapter.getCameraPositions(page).get(i);
                    if (cp != null) {
                        MarkerOptions mo = createMarkerOptions(cp, adapter.getMarkerTitle(page, i));
                        pageMarkers.add(map.addMarker(mo));
                    }
                    else pageMarkers.add(null);
                }
            }
            allMarkers.add(pageMarkers);
        }
        map.setOnMarkerClickListener(createMarkerClickListenerMulti(adapter));
        initDefaultPositions(adapter);
    }

    private void moveTo(int page, boolean animate) {
        if (adapter instanceof MultiAdapter) moveToMulti(page, animate);
        else moveToSingle((Adapter) adapter, page, animate);
    }

    private void moveToSingle(Adapter adapter, int index, boolean animate) {
        CameraPosition cp = adapter.getCameraPosition(index);
        CameraUpdate cu;
        if (cp != null && cp.target != null
                && cp.target.latitude != 0.0
                && cp.target.longitude != 0.0) {
            cu = CameraUpdateFactory.newCameraPosition(cp);
            if (hidden) showMarkers();
            if (markers.get(index) != null) markers.get(index).showInfoWindow();
        }
        else {
            cu = defaultPosition;
            hideInfoWindowSingle();
        }
        if (animate) map.animateCamera(cu);
        else map.moveCamera(cu);
    }

    private void moveToMulti(int page, boolean animate) {
        CameraUpdate cu = defaultPositions.get(page);
        if (cu == null) cu = defaultPosition;
        if (animate) map.animateCamera(cu);
        else map.moveCamera(cu);
        hideInfoWindowMulti();
        if (allMarkers.get(page) != null
                && allMarkers.get(page).size() == 1
                && allMarkers.get(page).get(0) != null)  { // this page has only one marker
            allMarkers.get(page).get(0).showInfoWindow();
        }
        showMarkersForPage(page);
    }

    private void initDefaultPosition() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) if (marker != null) builder.include(marker.getPosition());
        LatLngBounds bounds = builder.build();
        defaultPosition = CameraUpdateFactory.newLatLngBounds(bounds, mapOffset);
    }

    private void initDefaultPositions(final MultiAdapter adapter) {
        // each page
        defaultPositions = new LinkedList<>();
        for (int i = 0; i < adapter.getCount() ; i++) {
            defaultPositions.add(getDefaultPagePosition(adapter, i));
        }
        // global
        LinkedList<Marker> all = new LinkedList<>();
        for (List<Marker> list : allMarkers) if (list != null) all.addAll(list);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : all) if (marker != null) builder.include(marker.getPosition());
        LatLngBounds bounds = builder.build();
        defaultPosition = CameraUpdateFactory.newLatLngBounds(bounds, mapOffset);
    }

    private CameraUpdate getDefaultPagePosition(final MultiAdapter adapter, int page) {
        if (allMarkers.get(page).size() == 0)
            return null;
        if (allMarkers.get(page).size() == 1)
            return CameraUpdateFactory.newCameraPosition(adapter.getCameraPositions(page).get(0));
        // more than 1 marker on this page
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : allMarkers.get(page)) if (marker != null) builder.include(marker.getPosition());
        LatLngBounds bounds = builder.build();
        return CameraUpdateFactory.newLatLngBounds(bounds, mapOffset);
    }

    private GoogleMap.OnMarkerClickListener createMarkerClickListenerSingle(final Adapter adapter) {
        return new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    CameraPosition cp = adapter.getCameraPosition(i);
                    if (cp != null && cp.target != null
                            && cp.target.latitude == marker.getPosition().latitude
                            && cp.target.longitude == marker.getPosition().longitude) {
                        viewPager.setCurrentItem(i);
                        marker.showInfoWindow();
                        return true;
                    }
                }
                return false;
            }
        };
    }

    private GoogleMap.OnMarkerClickListener createMarkerClickListenerMulti(final MultiAdapter adapter) {
        return new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (int page = 0; page < adapter.getCount(); page++) {
                    if (adapter.getCameraPositions(page) != null) {
                        for (int i = 0; i < adapter.getCameraPositions(page).size(); i++) {
                            CameraPosition cp = adapter.getCameraPositions(page).get(i);
                            if (cp != null && cp.target != null
                                    && cp.target.latitude == marker.getPosition().latitude
                                    && cp.target.longitude == marker.getPosition().longitude) {
                                if (marker.isInfoWindowShown()) { // this doesn't seem to work !!
                                    viewPager.setCurrentItem(page);
                                    return true;
                                }
                                else {
                                    viewPager.setCurrentItem(page);
                                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
                                    marker.showInfoWindow();
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            }
        };
    }

    private MarkerOptions createMarkerOptions(CameraPosition cp, String title) {
        if (cp == null || cp.target == null) return null;
        return new MarkerOptions()
                .position(new LatLng(cp.target.latitude, cp.target.longitude))
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
    }

    private void showMarkers() {
        for (Marker marker : markers) if (marker != null) marker.setAlpha(1.0f);
    }

    private void showMarkersForPage(int page) {
        // make all translucent
        for (List<Marker> list : allMarkers) {
            for (Marker marker : list) {
                if (marker != null) marker.setAlpha(markersAlpha);
            }
        }
        // make this page ones opaque
        for (Marker marker : allMarkers.get(page)) {
            if (marker != null) marker.setAlpha(1.0f);
        }
    }

    private void hideInfoWindowMulti() {
        for (List<Marker> list : allMarkers) {
            if (list != null) {
                for (Marker marker : list) {
                    if (marker != null) marker.hideInfoWindow();
                }
            }
        }
    }

    private void hideInfoWindowSingle() {
        hidden = true;
        for (Marker marker : markers) {
            if (marker != null) {
                marker.hideInfoWindow();
                marker.setAlpha(markersAlpha);
            }
        }
    }

    private void setWeights() {
        // viewPager
        LinearLayout.LayoutParams pagerParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, mapWeight);
        viewPager.setLayoutParams(pagerParams);
        // mapFragment
        View mapView = mapFragment.getView();
        if (mapView != null) {
            LinearLayout.LayoutParams mapParams = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, pagerWeight);
            mapView.setLayoutParams(mapParams);
        }
    }

    private int dp(int dp) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }



    // general getters

    public GoogleMap getMap() {
        return map;
    }

    public SupportMapFragment getMapFragment() {
        return mapFragment;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public CameraUpdate getDefaultPosition() {
        return defaultPosition;
    }


    // single getters

    public Marker getMarker(int position) {
        return markers.get(position);
    }

    public List<Marker> getMarkers() {
        return markers;
    }


    // multi getters

    public Marker getMarker(int page, int position) {
        if (allMarkers.get(page) != null) return allMarkers.get(page).get(position);
        return null;
    }

    public List<Marker> getMarkers(int page) {
        return allMarkers.get(page);
    }

    public List<List<Marker>> getAllMarkers() {
        return allMarkers;
    }

    public CameraUpdate getDefaultPosition(int page) {
        return defaultPositions.get(page);
    }

    public List<CameraUpdate> getDefaultPositions() {
        return defaultPositions;
    }



    // Builder

    private MapViewPager(Builder builder, Context context) {
        super(context);
        // check that requited fields are provided
        if (context == null) throw new IllegalArgumentException("context can't be null");
        if (builder.mapFragment == null) throw new IllegalArgumentException("mapFragment can't be null");
        if (builder.viewPager == null) throw new IllegalArgumentException("viewPager can't be null");
        if (builder.adapter == null) throw new IllegalArgumentException("adapter can't be null");
        mapFragment = builder.mapFragment;
        viewPager = builder.viewPager;
        adapter = builder.adapter;
        callback = builder.callback;
        initialPosition = builder.position;
        markersAlpha = builder.markersAlpha;
        mapPaddingLeft = builder.mapPaddingLeft;
        mapPaddingTop = builder.mapPaddingTop;
        mapPaddingRight = builder.mapPaddingRight;
        mapPaddingBottom = builder.mapPaddingBottom;
        mapOffset = builder.mapOffset != 0 ? builder.mapOffset : dp(32);
        mapFragment.getMapAsync(this);
    }

    public static class Builder {

        private Context context;
        private SupportMapFragment mapFragment;
        private ViewPager viewPager;
        private AbsAdapter adapter;
        private Callback callback;
        private int position;
        private float markersAlpha = DEFAULT_ALPHA;
        private int mapOffset;
        private int mapPaddingLeft, mapPaddingTop, mapPaddingRight, mapPaddingBottom;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        public Builder mapFragment(@NonNull SupportMapFragment mapFragment) {
            this.mapFragment = mapFragment;
            return this;
        }

        public Builder viewPager(@NonNull ViewPager viewPager) {
            this.viewPager = viewPager;
            return this;
        }

        public Builder adapter(@NonNull AbsAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public Builder callback(@Nullable Callback callback) {
            this.callback = callback;
            return this;
        }

        public Builder position(int position) {
            this.position = position;
            return this;
        }

        public Builder markersAlpha(float alpha) {
            this.markersAlpha = alpha;
            return this;
        }

        public Builder mapOffset(int mapOffset) {
            this.mapOffset = mapOffset;
            return this;
        }

        public Builder mapPadding(int mapPaddingLeft,
                                  int mapPaddingTop,
                                  int mapPaddingRight,
                                  int mapPaddingBottom) {
            this.mapPaddingLeft = mapPaddingLeft;
            this.mapPaddingTop = mapPaddingTop;
            this.mapPaddingRight = mapPaddingRight;
            this.mapPaddingBottom = mapPaddingBottom;
            return this;
        }

        public MapViewPager build() {
            return new MapViewPager(this, context);
        }

    }

}
