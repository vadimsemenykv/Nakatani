package com.vadim.nakatani.fragments.home_screen;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.androidplot.ui.SeriesRenderer;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.vadim.nakatani.NakataniApplication;
import com.vadim.nakatani.R;
import com.vadim.nakatani.activitys.DiagnosticsActivity;
import com.vadim.nakatani.entity.PatientEntity;
import com.vadim.nakatani.entity.ResultEntity;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 *
 */
public class DiagnosticsFragment extends Fragment {
    private TextView textViewPatientName;
    private ListView listViewResults;

    private List<ResultEntity> resultEntityList;

    public static DiagnosticsFragment newInstance() {
        DiagnosticsFragment fragment = new DiagnosticsFragment();
        return fragment;
    }

    public DiagnosticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diagnostics, container, false);

        textViewPatientName = (TextView) rootView.findViewById(R.id.textView_diagnostics_patient_name);
        listViewResults = (ListView) rootView.findViewById(R.id.listView_diagnostics_results);

        NakataniApplication nakataniApplication = (NakataniApplication) getActivity().getApplicationContext();
        PatientEntity patientEntity = nakataniApplication.getPatientEntity();

        textViewPatientName.setText(patientEntity. getLastName() + " " + patientEntity.getFirstName() + " " + patientEntity.getMiddleName());

        List<String> mScreenTitles = new ArrayList<String>();
        resultEntityList = patientEntity.getResultEntityList();
        //TODO change this!!!!
        for (ResultEntity resultEntity : resultEntityList) {
            mScreenTitles.add(resultEntity.getCode());
            Log.d(this.getClass().getName(), resultEntity.getCode());
        }
        listViewResults.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.drawer_navigation_list_item, mScreenTitles));
        listViewResults.setOnItemClickListener(new DrawerItemClickListener());

        //TODO add click listener
        //TODO create another arrayAdapter
        //TODO create another list item - with button redact and delete

        return rootView;
    }

    /**
     * The click listener for ListView
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            NakataniApplication nakataniApplication = (NakataniApplication) getActivity().getApplicationContext();
            nakataniApplication.setResultEntity(resultEntityList.get(position));

            Log.e(this.getClass().getName(), resultEntityList.get(position).getCode());

            Intent diagnostics = new Intent();
            diagnostics.setClass(getActivity(), DiagnosticsActivity.class);
            startActivity(diagnostics);
            Log.d(this.getClass().getName(), "selected = " + position);
        }
    }
}

