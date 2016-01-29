package com.github.nitrico.mvp.sample2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.github.nitrico.mapviewpager.MapViewPager;
import com.github.nitrico.mvp.R;

public class Sample2Activity extends AppCompatActivity {

    private MapViewPager mapViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_2);
        mapViewPager = (MapViewPager) findViewById(R.id.mapViewPager);

        Sample2Adapter adapter = new Sample2Adapter(getSupportFragmentManager());
        mapViewPager.start(this, adapter);
    }

}
