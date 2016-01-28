package com.github.nitrico.mvp.sample4;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.nitrico.mvp.R;

public class Sample4Fragment extends Fragment {

    private TextView title;
    private int index = 0;

    public Sample4Fragment() { }

    public static Sample4Fragment newInstance(int i) {
        Sample4Fragment f = new Sample4Fragment();
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
        View view = inflater.inflate(R.layout.fragment_sample_4, container, false);
        title = (TextView) view.findViewById(R.id.title);

        Bundle args = getArguments();
        if (args != null) index = args.getInt("INDEX", 0);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        title.setText(Sample4Adapter.TITLES[index]);
        ViewCompat.setElevation(getView(), 16f);
    }

}
