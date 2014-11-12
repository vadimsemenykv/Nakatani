package com.vadim.nakatani.fragments.diagnostics_screen;

import android.app.Fragment;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;

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
import com.vadim.nakatani.RiodorakyDump;
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
public class RadiorakyMapFragment extends Fragment {
//    private int[] eksValues = {21, 22, 23, 24, 25, 26,  31, 32, 33, 34, 35, 36,  41, 42, 43, 44, 45, 46,  51, 52, 53, 54, 55, 56 };

//    private final String[] xLabelsLR= {"", "       P9", "P9", "", "MC7", "MC7", "", "C7", "C7", "", "IG4", "IG4", "", "TR4", "TR4", "", "GI5", "GI5", "", "RP3", "RP3", "", "F3", "F3", "", "R3", "R3", "", "V65", "V65", "", "VB40", "VB40", "", "E42", "E42", ""};

    private final String[] xLabelsLR = {"", "        P9", "", "", "        MC7", "", "", "        C7", "", "", "        IG4", "", "", "        TR4", "", "", "        GI5", "", "", "        RP3", "", "", "        F3", "", "", "        R3", "", "", "        V65", "", "", "        VB40", "", "", "        E42", "", ""};

    private XYPlot plot;
    private int lowBorderValue;
    private int highBorderValue;

    public static RadiorakyMapFragment newInstance() {
        RadiorakyMapFragment fragment = new RadiorakyMapFragment();
        return fragment;
    }

    public RadiorakyMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_radioraky_map, container, false);

        NakataniApplication nakataniApplication = (NakataniApplication) getActivity().getApplicationContext();
        ResultEntity resultEntity = nakataniApplication.getResultEntity();
        int[] eksValues = new int[resultEntity.getPointsValue().size()];
        for (int i = 0; i < resultEntity.getPointsValue().size(); i++) {
            eksValues[i] = resultEntity.getPointsValue().get(i);
        }

//        int[] eksValues = new int[24];
//        for (int i = 0; i < 24; i++) {
//            eksValues[i] = 50;
//        }
        RiodorakyDump riodorakyDump = new RiodorakyDump(eksValues);
        int[] radioraky = riodorakyDump.getRiodoraky();

        lowBorderValue = riodorakyDump.getLowRiodoraky();
        highBorderValue = riodorakyDump.getHighRiodoraky();

        List<Number> radiorakyValuesForBarSeries = new ArrayList<Number>();

        for (int i = 0, j = 0, k = 0; i < 12; i++, j++, k++) {
            if (j == 6) j = 12;
            if (i == 0) radiorakyValuesForBarSeries.add(0);
            radiorakyValuesForBarSeries.add(radioraky[j]);
            radiorakyValuesForBarSeries.add(radioraky[j + 6]);
            radiorakyValuesForBarSeries.add(0);
        }

        List<Number> radiorakyValuesForBarValueSeries = new ArrayList<Number>();
        for (int i = 0, j = 0, k = 1; i < 12; i++, j++, k += 3) {
            if (j == 6) j = 12;
            radiorakyValuesForBarValueSeries.add(k);
            radiorakyValuesForBarValueSeries.add(radioraky[j]);
            radiorakyValuesForBarValueSeries.add(k + 1);
            radiorakyValuesForBarValueSeries.add(radioraky[j + 6]);
        }

        Number[] valuesLowBorder = new Number[4];
        valuesLowBorder[0] = 0;
        valuesLowBorder[1] = lowBorderValue;
        valuesLowBorder[2] = radiorakyValuesForBarSeries.size() - 1;
        valuesLowBorder[3] = lowBorderValue;
        Number[] valuesMiddleBorder = new Number[4];
        valuesMiddleBorder[0] = 0;
        valuesMiddleBorder[1] = riodorakyDump.getMiddleRiodoraky();
        valuesMiddleBorder[2] = radiorakyValuesForBarSeries.size() - 1;
        valuesMiddleBorder[3] = riodorakyDump.getMiddleRiodoraky();
        Number[] valuesHighBorder = new Number[4];
        valuesHighBorder[0] = 0;
        valuesHighBorder[1] = highBorderValue;
        valuesHighBorder[2] = radiorakyValuesForBarSeries.size() - 1;
        valuesHighBorder[3] = highBorderValue;

        plot = (XYPlot) rootView.findViewById(R.id.mySimpleXYPlot);

        // Turn the above array into XYSeries':
        XYSeries seriesRadorakyBar = new SimpleXYSeries(radiorakyValuesForBarSeries, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        XYSeries seriesRadiorakyBarValue = new SimpleXYSeries(radiorakyValuesForBarValueSeries, SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Series2");

        XYSeries seriesLowBorderLine = new SimpleXYSeries(Arrays.asList(valuesLowBorder), SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Series4");
        XYSeries seriesMiddleBorderLine = new SimpleXYSeries(Arrays.asList(valuesMiddleBorder), SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Series4");
        XYSeries seriesHighBorderLine = new SimpleXYSeries(Arrays.asList(valuesHighBorder), SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Series5");

        //remove legend
        plot.getLayoutManager().remove(plot.getLegendWidget());
        //change X axis labels
        plot.getGraphWidget().setDomainValueFormat(new GraphXLabelFormat());
        /**
         * set Y
         */
        int max = 0;
        for (int i : radioraky) {
            max = (max < i)?i:max;
        }
        if (max < 90) max += 10;
        plot.setRangeBoundaries(0, max, BoundaryMode.FIXED);
        plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 5);

        /**
         * set X
         */
        plot.setDomainBoundaries(0, radiorakyValuesForBarSeries.size() - 1, BoundaryMode.FIXED);
        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);

        plot.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
        plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);

        plot.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
        plot.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);

        plot.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLACK);
        plot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        plot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);

        // Add a new series to the plot:
        MyBarFormatter formatterBar = new MyBarFormatter(Color.argb(200, 100, 150, 100), Color.LTGRAY);
        plot.addSeries(seriesRadorakyBar, formatterBar);

        LineAndPointFormatter formatterBarValue = new LineAndPointFormatter(0x00000000, 0x00000000, 0x00000000, new PointLabelFormatter(Color.BLACK));
        plot.addSeries(seriesRadiorakyBarValue, formatterBarValue);

        LineAndPointFormatter formaterLowLine = new LineAndPointFormatter(0xFF1A0600, 0x00000000, 0x00000000, null);
        plot.addSeries(seriesLowBorderLine, formaterLowLine);

        Paint dashPaint = new Paint();
        dashPaint.setColor(0xFF1A0600);
        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setStrokeWidth(3);
        dashPaint.setPathEffect(new DashPathEffect(new float[] {10,7}, 0));

        LineAndPointFormatter formatterMiddleLine = new LineAndPointFormatter(0xFF1A0600, 0x00000000, 0x00000000, null);
        formatterMiddleLine.setLinePaint(dashPaint);
        plot.addSeries(seriesMiddleBorderLine, formatterMiddleLine);

        LineAndPointFormatter formaterHighLine = new LineAndPointFormatter(0xFF1A0600, 0x00000000, 0x00000000, null);
        plot.addSeries(seriesHighBorderLine, formaterHighLine);

        // Give each bar gap
        MyBarRenderer renderer = ((MyBarRenderer) plot.getRenderer(MyBarRenderer.class));
        renderer.setBarWidthStyle(BarRenderer.BarWidthStyle.VARIABLE_WIDTH);
        renderer.setBarGap(0f);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //first saving my state, so the bundle wont be empty.
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY",  "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    class GraphXLabelFormat extends Format {
        final String[] xLabels = {"", "H1 левая", "правая", "", "H2 левая", "правая", "", "Aug", "Sept", "", "Nov", "Dec", "", "Mar", "Apr", "", "Jun", "Jul", ""};

        @Override
        public StringBuffer format(Object arg0, StringBuffer arg1, FieldPosition arg2) {
            // TODO Auto-generated method stub

            int parsedInt = Math.round(Float.parseFloat(arg0.toString()));
//        parsedInt = parsedInt / 2;
            Log.d("test", parsedInt + " " + arg1 + " " + arg2);
            String labelString = xLabelsLR[parsedInt];
            arg1.append(labelString);
            return arg1;
        }

        @Override
        public Object parseObject(String arg0, ParsePosition arg1) {
            // TODO Auto-generated method stub
            return java.util.Arrays.asList(xLabels).indexOf(arg0);
        }
    }

    class MyBarFormatter extends BarFormatter {

        public MyBarFormatter(int fillColor, int borderColor) {
            super(fillColor, borderColor);
        }

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return MyBarRenderer.class;
        }

        @Override
        public SeriesRenderer getRendererInstance(XYPlot plot) {
            return new MyBarRenderer(plot);
        }
    }

    class MyBarRenderer extends BarRenderer<MyBarFormatter> {
        public MyBarRenderer(XYPlot plot) {
            super(plot);
        }

        public MyBarFormatter getFormatter(int index, XYSeries series) {
            // return getFormatter(series);
            if(series.getY(index).intValue() < lowBorderValue) {
                return new MyBarFormatter(Color.BLUE, Color.TRANSPARENT);
            }
            if(series.getY(index).intValue() > highBorderValue) {
                return new MyBarFormatter(Color.RED, Color.TRANSPARENT);
            } else {
                return new MyBarFormatter(Color.GREEN, Color.TRANSPARENT);
            }
        }
    }
}

