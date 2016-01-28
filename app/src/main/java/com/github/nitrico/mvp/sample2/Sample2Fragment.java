package com.github.nitrico.mvp.sample2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.nitrico.mvp.R;

public class Sample2Fragment extends Fragment {

    private TextView title;
    private int index = 0;

    public Sample2Fragment() { }

    public static Sample2Fragment newInstance(int i) {
        Sample2Fragment f = new Sample2Fragment();
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
        View view = inflater.inflate(R.layout.fragment_sample_2, container, false);
        title = (TextView) view.findViewById(R.id.title);

        Bundle args = getArguments();
        if (args != null) index = args.getInt("INDEX", 0);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        title.setText(Sample2Adapter.PAGE_TITLES[index]);
    }

}
