package com.vadim.nakatani.fragments.home_screen;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vadim.nakatani.R;


/**
 *
 */
public class TestingFragment extends Fragment implements View.OnClickListener {
    private final String TAG = TestingFragment.class.getSimpleName();

    Button buttonTestPatient;
    Button buttonTestAnonymous;
    Button buttonCalibration;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CardFileFragment
     */
    public static TestingFragment newInstance() {
        TestingFragment fragment = new TestingFragment();
        return fragment;
    }

    public TestingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_testing, container, false);

        buttonTestPatient = (Button) rootView.findViewById(R.id.button_testing_test_for_patient);
        buttonTestPatient.setOnClickListener(this);
        buttonTestAnonymous = (Button) rootView.findViewById(R.id.button_testing_test_anonymous);
        buttonTestAnonymous.setOnClickListener(this);
        buttonCalibration = (Button) rootView.findViewById(R.id.button_testing_calibration);
        buttonCalibration.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        int buttonId = view.getId();
        switch (buttonId) {
            case R.id.button_testing_test_for_patient:
//                findUSBDevices();
                //TODO if patient not set switch to cardFile fragment
                Intent test = new Intent();
                test.setClass(getActivity() , com.vadim.nakatani.activitys.TestActivity.class);
                startActivity(test);
                break;
            case R.id.button_testing_test_anonymous:
//                connectToDevice();
                break;
            case R.id.button_testing_calibration:
                Intent calibration = new Intent();
                calibration.setClass(getActivity() , com.vadim.nakatani.activitys.CalibrationActivity.class);
                startActivity(calibration);
                break;
            default:
                break;
        }
    }
}
