package com.vadim.nakatani.fragments.diagnostics_screen;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vadim.nakatani.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EKSValueFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class EKSValueFragment extends Fragment {
    private TextView[][] textViewsArray;
    private String[] pointsName = {"H1", "H2", "H3", "H4", "H5", "H6", "F1", "F2", "F3", "F4", "F5", "F6"};
    private int[] eksValues = {13, 25, 34, 45, 53, 68, 14, 25, 34, 45, 53, 68, 24, 25, 34, 35, 23, 28, 24, 25, 34, 35, 23, 28 };

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
     * @return A new instance of fragment EKSValueFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EKSValueFragment newInstance(String param1, String param2) {
        EKSValueFragment fragment = new EKSValueFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public EKSValueFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_eksvalue, container, false);

        textViewsArray = new TextView[12][2];

        TableLayout tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout_eks_fragment);
        TableRow tableRow = (TableRow) tableLayout.findViewById(R.id.tableRow_eks_header);
        tableLayout.removeAllViews();
        tableLayout.addView(tableRow);

        for (int i = 0, j = 0; i < 12; i++, j++) {
            if (j == 6) j = 12;
            TableRow tableRowCopy = (TableRow) inflater.inflate(R.layout.fragment_eksvalue_table_row, null);

            TextView textViewN = (TextView) tableRowCopy.findViewById(R.id.textView_eks_header_point_nameF);
            TextView textViewM = (TextView) tableRowCopy.findViewById(R.id.textView_eks_header_point_meridianF);
            TextView textViewVL = (TextView) tableRowCopy.findViewById(R.id.textView_eks_header_point_value_leftF);
            TextView textViewVR = (TextView) tableRowCopy.findViewById(R.id.textView_eks_header_point_value_rightF);

            textViewN.setText(pointsName[i]);
            textViewVL.setText(String.valueOf(eksValues[j]));
            textViewVR.setText(String.valueOf(eksValues[j + 6]));

            textViewsArray[i][0] = textViewVL;
            textViewsArray[i][1] = textViewVR;

            tableLayout.addView(tableRowCopy);
        }
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //first saving my state, so the bundle wont be empty.
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY",  "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
}
