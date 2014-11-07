package com.vadim.nakatani.fragments.home_screen;

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
public class MedicalHistoryFragment extends Fragment {

    public MedicalHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_medical_history, container,
                false);

        return rootView;
    }
}
