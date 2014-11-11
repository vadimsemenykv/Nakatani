package com.vadim.nakatani.fragments.diagnostics_screen;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.vadim.nakatani.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EvaluationFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class EvaluationFragment extends Fragment {
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

        String htmlString = "\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                "<meta name=\"generator\" content=\"SautinSoft.RtfToHtml.dll\">\n" +
                "<title>Document title</title>\n" +
                "<style type=\"text/css\">\n" +
                ".st1{font-family:Arial;font-size:12pt;color:#008000;font-weight:bold}\n" +
                ".st2{font-family:Arial;font-size:10pt;color:#000000}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div>\n" +
                "<div style=\"margin:4.5pt 0pt 0pt 0pt;line-height:normal;\"><span class=\"st1\">&#1042;&#1077;&#1088;&#1086;&#1103;&#1090;&#1085;&#1099;&#1077; &#1085;&#1072;&#1088;&#1091;&#1096;&#1077;&#1085;&#1080;&#1103;:</span></div>\n" +
                "<div style=\"margin:0pt 0pt 0pt 0pt;line-height:normal;\"><span class=\"st2\">&#1078;&#1077;&#1083;&#1091;&#1076;&#1082;&#1072; &#1080; &#1087;&#1086;&#1076;&#1078;&#1077;&#1083;&#1091;&#1076;&#1086;&#1095;&#1085;&#1086;&#1081; &#1078;&#1077;&#1083;&#1077;&#1079;&#1099;; &#1092;&#1091;&#1085;&#1082;&#1094;&#1080;&#1080; &#1090;&#1088;&#1072;&#1085;&#1089;&#1087;&#1086;&#1088;&#1090;&#1080;&#1088;&#1086;&#1074;&#1082;&#1080; &#1080; &#1090;&#1088;&#1072;&#1085;&#1089;&#1092;&#1086;&#1088;&#1084;&#1072;&#1094;&#1080;&#1080; &#1087;&#1080;&#1097;&#1080; &#1080; &#1074;&#1086;&#1076;&#1099;; &#1074;&#1086;&#1089;&#1087;&#1088;&#1080;&#1103;&#1090;&#1080;&#1077; &#1074;&#1082;&#1091;&#1089;&#1086;&#1074;&#1099;&#1093; &#1086;&#1097;&#1091;&#1097;&#1077;&#1085;&#1080;&#1081;; &#1092;&#1091;&#1085;&#1082;&#1094;&#1080;&#1080; &#1082;&#1088;&#1086;&#1074;&#1086;&#1089;&#1085;&#1072;&#1073;&#1078;&#1077;&#1085;&#1080;&#1103; (&#1086;&#1095;&#1080;&#1089;&#1090;&#1082;&#1072; &#1080; &#1089;&#1086;&#1089;&#1090;&#1072;&#1074; &#1082;&#1088;&#1086;&#1074;&#1080;); &#1082;&#1086;&#1085;&#1090;&#1088;&#1086;&#1083;&#1103; &#1085;&#1072;&#1076; &#1084;&#1099;&#1096;&#1094;&#1072;&#1084;&#1080;. </span></div>\n" +
                "</div><div style=\"text-align:center;\">The trial version of RTF-to-HTML DLL .Net can convert up to 10000 symbols.<br><a href=\"http://www.sautinsoft.com/convert-rtf-to-html/order.php\">Get the full featured version!</a></div>\n" +
                "</body>\n" +
                "</html>";

        WebView webView = (WebView) rootView.findViewById(R.id.webView1);
        webView.loadData(htmlString,"text/html", null);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //first saving my state, so the bundle wont be empty.
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY",  "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
}
