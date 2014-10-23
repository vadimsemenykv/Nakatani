package com.vadim.nakatani.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vadim.nakatani.R;


/**
 *
 */
public class Diagnostics extends Fragment {

    public Diagnostics() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_diagnostics, container,
                false);

        return rootView;
    }
}
