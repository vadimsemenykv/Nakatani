package com.vadim.nakatani.fragments.diagnostics_screen;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vadim.nakatani.NakataniApplication;
import com.vadim.nakatani.R;
import com.vadim.nakatani.RiodorakyDump;
import com.vadim.nakatani.entity.PatientEntity;
import com.vadim.nakatani.entity.ResultEntity;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EvaluationFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class EvaluationFragment extends Fragment {
    private TextView[][] textViewsArray;
    private String[] pointsName = {"H1", "H2", "H3", "H4", "H5", "H6", "F1", "F2", "F3", "F4", "F5", "F6"};
    //    private int[] eksValueList;/* = {13, 25, 34, 45, 53, 68, 14, 25, 34, 45, 53, 68, 24, 25, 34, 35, 23, 28, 24, 25, 34, 35, 23, 28 };*/
    private List<Integer> eksValueList;

    private TextView textViewPatientName;
    private TextView textViewResultCode;
    private TextView textViewDateTime;
    private TextView textViewMiddle;
    private TextView textViewCor;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EvaluationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EvaluationFragment newInstance(String param1, String param2) {
        EvaluationFragment fragment = new EvaluationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public EvaluationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_evaluation, container, false);

        NakataniApplication nakataniApplication = (NakataniApplication) getActivity().getApplicationContext();
        PatientEntity patientEntity = nakataniApplication.getPatientEntity();
        ResultEntity resultEntity = nakataniApplication.getResultEntity();
        eksValueList = resultEntity.getPointsValue();

        /*get Riodoraky array*/
        int[] eksValues = new int[resultEntity.getPointsValue().size()];
        for (int i = 0; i < resultEntity.getPointsValue().size(); i++) {
            eksValues[i] = resultEntity.getPointsValue().get(i);
        }
        RiodorakyDump riodorakyDump = new RiodorakyDump(eksValues);
        int[] riodoraky = riodorakyDump.getRiodoraky();

        textViewPatientName = (TextView) rootView.findViewById(R.id.textView_evaluation_patient_name);
        textViewPatientName.setText(patientEntity.getLastName() + " " + patientEntity.getFirstName() + " " + patientEntity.getMiddleName());

        textViewResultCode = (TextView) rootView.findViewById(R.id.textView_evaluation_result_code);
        textViewResultCode.setText(resultEntity.getCode());

        textViewDateTime = (TextView) rootView.findViewById(R.id.textView_evaluation_result_data);
        textViewDateTime.setText(resultEntity.getDate() + " " + resultEntity.getTime());

        textViewMiddle = (TextView) rootView.findViewById(R.id.textView_evaluation_middle);
        textViewMiddle.setText("" + riodorakyDump.getMiddleEKS() + " / " + riodorakyDump.getMiddleRiodoraky());

        textViewCor = (TextView) rootView.findViewById(R.id.textView_evaluation_cor);
        textViewCor.setText("" + riodorakyDump.getLowRiodoraky() + "..." + riodorakyDump.getHighRiodoraky());

        textViewsArray = new TextView[12][3];

        TableLayout tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout_evaluation);
        TableRow tableRow = (TableRow) tableLayout.findViewById(R.id.tableRow_evaluation_header);
        tableLayout.removeAllViews();
        tableLayout.addView(tableRow);

        for (int i = 0, j = 0; i < 12; i++, j++) {
            if (j == 6) j = 12;
            TableRow tableRowCopy = (TableRow) inflater.inflate(R.layout.fragment_evaluation_table_row, null);

            TextView textViewN = (TextView) tableRowCopy.findViewById(R.id.textView_evaluation_header_point_nameF);
            TextView textViewM = (TextView) tableRowCopy.findViewById(R.id.textView_evaluation_header_point_meridianF);
            TextView textViewRadioraky = (TextView) tableRowCopy.findViewById(R.id.textView_evaluation_header_point_riodorakyF);
            TextView textViewDeviation = (TextView) tableRowCopy.findViewById(R.id.textView_evaluation_header_point_deviationF);
            TextView textViewEKS = (TextView) tableRowCopy.findViewById(R.id.textView_evaluation_header_point_eksF);

            textViewN.setText(pointsName[i]);

            String riodorakyStr = "" + riodoraky[j] + " / " + riodoraky[j + 6];
            textViewRadioraky.setText(riodorakyStr);

            String devL = "0";
            if ((riodoraky[j] - riodorakyDump.getLowRiodoraky()) < 0) devL = "-" + (riodorakyDump.getLowRiodoraky() - riodoraky[j]);
            if ((riodorakyDump.getHighRiodoraky() - riodoraky[j]) < 0) devL = "+" + (riodoraky[j] - riodorakyDump.getHighRiodoraky());
            String devR = "0";
            if ((riodoraky[j + 6] - riodorakyDump.getLowRiodoraky()) < 0) devR = "-" + (riodorakyDump.getLowRiodoraky() - riodoraky[j]);
            if ((riodorakyDump.getHighRiodoraky() - riodoraky[j + 6]) < 0) devR = "+" + (riodoraky[j] - riodorakyDump.getHighRiodoraky());

            String deviationStr = devL + " / " + devR;
            textViewDeviation.setText(deviationStr);

            String eksStr = String.valueOf(eksValueList.get(j)) + " / " + String.valueOf(eksValueList.get(j + 6));
            textViewEKS.setText(eksStr);

//            textViewRadioraky.setText(String.valueOf(eksValueList.get(j)));
//            textViewEKS.setText(String.valueOf(eksValueList.get(j + 6)));

            textViewsArray[i][0] = textViewRadioraky;
            textViewsArray[i][1] = textViewDeviation;
            textViewsArray[i][2] = textViewEKS;

            tableLayout.addView(tableRowCopy);
        }

        WebView webView = (WebView) rootView.findViewById(R.id.webViewEvaluation);
        webView.loadUrl("file:///android_asset/textsections/ts_p1_s16.html");

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //first saving my state, so the bundle wont be empty.
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY",  "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
}
