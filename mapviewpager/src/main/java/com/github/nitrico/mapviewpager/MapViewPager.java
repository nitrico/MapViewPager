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

    private static abstract class AbsAdapter extends FragmentStatePagerAdapter {
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
    private static final int DEFAULT_OFFSET = 0;

    private float markersAlpha = DEFAULT_ALPHA;
    private int mapOffset = DEFAULT_OFFSET;
    private int mapGravity = 1;
    private int mapWeight = 1, pagerWeight = 1;
    private int mapPaddingLeft = 0, mapPaddingTop = 0, mapPaddingRight = 0, mapPaddingBottom = 0;

    private ViewPager viewPager;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private Callback callback;

    private CameraUpdate defaultPosition;
    private List<CameraUpdate> defaultPositions;

    private List<Marker> markers;
    private List<List<Marker>> allMarkers;

    private AbsAdapter adapter;
    private boolean multi = false;
    private boolean hidden = false;

    public MapViewPager(Context context) {
        super(context);
        // not calling initialize(context, null) to use it with Builder
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
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MapViewPager, 0, 0);
            try {
                markersAlpha = a.getFloat(R.styleable.MapViewPager_markersAlpha, DEFAULT_ALPHA);
                pagerWeight = a.getInteger(R.styleable.MapViewPager_viewPagerWeight, 1);
                mapWeight = a.getInteger(R.styleable.MapViewPager_mapWeight, 1);
                mapGravity = a.getInteger(R.styleable.MapViewPager_mapGravity, 1);
                mapOffset = a.getDimensionPixelSize(R.styleable.MapViewPager_mapOffset, DEFAULT_OFFSET);
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
            case 0: inflater.inflate(R.layout.mapviewpager_left, this); break;
            case 1: inflater.inflate(R.layout.mapviewpager_top, this); break;
            case 2: inflater.inflate(R.layout.mapviewpager_right, this); break;
            case 3: inflater.inflate(R.layout.mapviewpager_bottom, this); break;
        }
    }

    public void start(@NonNull FragmentActivity activity,
                      @NonNull AbsAdapter mapAdapter) {
        start(activity, mapAdapter, null);
    }

    public void start(@NonNull FragmentActivity activity,
                      @NonNull AbsAdapter mapAdapter,
                      @Nullable Callback callback) {
        this.callback = callback;
        adapter = mapAdapter;
        if (adapter instanceof MultiAdapter) multi = true;
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

    private void moveTo(int index, boolean animate) {
        if (multi) multiMoveTo(index, animate);
        else singleMoveTo((Adapter) adapter, index, animate);
    }

    private void multiMoveTo(int index, boolean animate) {
        CameraUpdate cu = defaultPositions.get(index);
        if (cu == null) cu = defaultPosition;

        if (animate) map.animateCamera(cu);
        else map.moveCamera(cu);

        hideInfoWindow();
        if (allMarkers.get(index).size() == 1) allMarkers.get(index).get(0).showInfoWindow();
        showMarkersForPage(index);
    }

    private void singleMoveTo(Adapter adapter, int index, boolean animate) {
        CameraPosition camPos = adapter.getCameraPosition(index);
        double lat = 0.0;
        double lng = 0.0;

        if (camPos != null) {
            lat = adapter.getCameraPosition(index).target.latitude;
            lng = adapter.getCameraPosition(index).target.longitude;
        }

        CameraUpdate cu;
        if (camPos != null && lat != 0.0 && lng != 0.0) {
            if (hidden) showMarkers();
            cu = CameraUpdateFactory.newCameraPosition(camPos);
            markers.get(index).showInfoWindow();
        }
        else {
            cu = defaultPosition;
            hideInfoWindow();
        }
        if (animate) map.animateCamera(cu);
        else map.moveCamera(cu);
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

    private void hideInfoWindow() {
        if (multi) multiHideInfoWindow();
        else singleHideInfoWindow();
    }

    private void singleHideInfoWindow() {
        hidden = true;
        for (Marker marker : markers) {
            if (marker != null) {
                marker.hideInfoWindow();
                marker.setAlpha(markersAlpha);
            }
        }
    }

    private void multiHideInfoWindow() {
        for (List<Marker> list : allMarkers) {
            for (Marker marker : list) {
                if (marker != null) marker.hideInfoWindow();
            }
        }
    }

    private GoogleMap.OnMarkerClickListener createMarkerClickListener() {
        if (multi) return createMultiMarkerClickListener((MultiAdapter) adapter);
        else return createSingleMarkerClickListener((Adapter) adapter);
    }

    private GoogleMap.OnMarkerClickListener createSingleMarkerClickListener(final Adapter adapter) {
        return new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    CameraPosition cp = adapter.getCameraPosition(i);
                    if (cp != null
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

    private GoogleMap.OnMarkerClickListener createMultiMarkerClickListener(final MultiAdapter adapter) {
        return new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (int page = 0; page < adapter.getCount(); page++) {
                    if (adapter.getCameraPositions(page) != null) {
                        for (int i = 0; i < adapter.getCameraPositions(page).size(); i++) {
                            CameraPosition cp = adapter.getCameraPositions(page).get(i);
                            if (cp != null
                                    && cp.target.latitude == marker.getPosition().latitude
                                    && cp.target.longitude == marker.getPosition().longitude) {
                                if (marker.isInfoWindowShown()) { // ESTO PARECE QUE NO VA
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

    private void populate() {
        if (multi) {
            populate((MultiAdapter) adapter);
            initDefaultPositions();
        }
        else {
            populate((Adapter) adapter);
            initDefaultPosition();
        }
    }

    private void populate(final Adapter adapter) {
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
        map.setOnMarkerClickListener(createSingleMarkerClickListener(adapter));
    }

    private void populate(final MultiAdapter adapter) {
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
        map.setOnMarkerClickListener(createMultiMarkerClickListener(adapter));
    }

    private MarkerOptions createMarkerOptions(CameraPosition cp, String title) {
        return new MarkerOptions()
                .position(new LatLng(cp.target.latitude, cp.target.longitude))
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
    }

    private void initDefaultPosition() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) if (marker != null) builder.include(marker.getPosition());
        LatLngBounds bounds = builder.build();
        defaultPosition = CameraUpdateFactory.newLatLngBounds(bounds, mapOffset);
    }

    private void initDefaultPositions() {
        // each page
        defaultPositions = new LinkedList<>();
        for (int i = 0; i<adapter.getCount(); i++) {
            defaultPositions.add(getDefaultPagePosition((MultiAdapter) adapter, i));
        }
        // global
        LinkedList<Marker> all = new LinkedList<>();
        for (List<Marker> list : allMarkers) all.addAll(list);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : all) if (marker != null) builder.include(marker.getPosition());
        LatLngBounds bounds = builder.build();
        defaultPosition = CameraUpdateFactory.newLatLngBounds(bounds, mapOffset);
    }

    private CameraUpdate getDefaultPagePosition(MultiAdapter adapter, int page) {
        if (allMarkers.get(page).size() == 0)
            return null;
        if (allMarkers.get(page).size() == 1)
            return CameraUpdateFactory.newCameraPosition(adapter.getCameraPositions(page).get(0));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : allMarkers.get(page)) if (marker != null) builder.include(marker.getPosition());
        LatLngBounds bounds = builder.build();
        return CameraUpdateFactory.newLatLngBounds(bounds, mapOffset);
    }

    private void setWeights() {
        LinearLayout.LayoutParams pagerParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, mapWeight);
        viewPager.setLayoutParams(pagerParams);

        View mapView = mapFragment.getView();
        if (mapView != null) {
            LinearLayout.LayoutParams mapParams = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, pagerWeight);
            mapView.setLayoutParams(mapParams);
        }
    }


    // GENERAL GETTERS

    public GoogleMap getMap() {
        return map;
    }

    public SupportMapFragment getMapFragment() {
        return mapFragment;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void setCurrentItem(int currentItem) {
        viewPager.setCurrentItem(currentItem);
    }

    public CameraUpdate getDefaultPosition() {
        return defaultPosition;
    }


    // SINGLE GETTERS

    public Marker getMarker(int position) {
        return markers.get(position);
    }

    public List<Marker> getMarkers() {
        return markers;
    }


    // MULTI GETTERS

    public Marker getMarker(int page, int position) {
        return allMarkers.get(page).get(position);
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



    // BUILDER

    public static class Builder {

        private Context context;
        private SupportMapFragment mapFragment;
        private ViewPager viewPager;
        private AbsAdapter adapter;
        private Callback callback;
        private float markersAlpha = DEFAULT_ALPHA;
        private int mapOffset = DEFAULT_OFFSET;
        private int mapPaddingLeft, mapPaddingTop, mapPaddingRight, mapPaddingBottom;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder(Context context,
                       SupportMapFragment mapFragment,
                       ViewPager viewPager,
                       AbsAdapter adapter) {
            this.context = context;
            this.mapFragment = mapFragment;
            this.viewPager = viewPager;
            this.adapter = adapter;
        }

        public Builder mapFragment(SupportMapFragment mapFragment) {
            this.mapFragment = mapFragment;
            return this;
        }

        public Builder viewPager(ViewPager viewPager) {
            this.viewPager = viewPager;
            return this;
        }

        public Builder adapter(AbsAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public Builder callback(Callback callback) {
            this.callback = callback;
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

    private MapViewPager(Builder builder, Context context) {
        super(context);
        mapFragment = builder.mapFragment;
        viewPager = builder.viewPager;
        adapter = builder.adapter;
        callback = builder.callback;
        markersAlpha = builder.markersAlpha;
        mapOffset = builder.mapOffset;
        mapPaddingLeft = builder.mapPaddingLeft;
        mapPaddingTop = builder.mapPaddingTop;
        mapPaddingRight = builder.mapPaddingRight;
        mapPaddingBottom = builder.mapPaddingBottom;
        // check that required fields are provided! (mapFragment, viewPager & adapter)

        // start
        if (adapter instanceof MultiAdapter) multi = true;
        mapFragment.getMapAsync(this);
    }


    private static int dp(Context context, int dp) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }

}
