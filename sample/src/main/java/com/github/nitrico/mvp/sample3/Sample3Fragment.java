package com.github.nitrico.mvp.sample3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.nitrico.mvp.R;

public class Sample3Fragment extends Fragment {

    private ImageView image;
    private TextView title;

    private int index = 0;

    public Sample3Fragment() { }

    public static Sample3Fragment newInstance(int i) {
        Sample3Fragment f = new Sample3Fragment();
        Bundle args = new Bundle();
        args.putInt("INDEX", i);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sample_3, container, false);
        image = (ImageView) view.findViewById(R.id.image);
        title = (TextView) view.findViewById(R.id.title);

        Bundle args = getArguments();
        if (args != null) index = args.getInt("INDEX", 0);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        title.setText(Sample3Adapter.PAGE_TITLES[index]);
        ViewCompat.setElevation(getView(), 16f);
    }

}
