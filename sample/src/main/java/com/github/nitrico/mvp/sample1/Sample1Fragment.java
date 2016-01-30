package com.github.nitrico.mvp.sample1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.nitrico.mvp.R;

public class Sample1Fragment extends android.support.v4.app.Fragment {

    private TextView title;
    private int index;

    public Sample1Fragment() { }

    public static Sample1Fragment newInstance(int i) {
        Sample1Fragment f = new Sample1Fragment();
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
        View view = inflater.inflate(R.layout.fragment_sample_1, container, false);
        title = (TextView) view.findViewById(R.id.title);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) index = args.getInt("INDEX", 0);

        title.setText(Sample1Adapter.TITLES[index]);
    }

}
