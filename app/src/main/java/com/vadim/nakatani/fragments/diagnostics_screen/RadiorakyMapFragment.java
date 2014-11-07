package com.vadim.nakatani.fragments.diagnostics_screen;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.net.Uri;
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
import com.vadim.nakatani.R;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 *
 */
public class RadiorakyMapFragment extends Fragment {
//    private int[] eksValues = {50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50 };
    private int[] eksValues = {13, 25, 34, 45, 53, 68, 14, 25, 34, 45, 53, 68, 24, 25, 34, 35, 23, 28, 24, 25, 34, 35, 23, 28 };

    private XYPlot plot;
    private int highBorderValue = 50;

    public static RadiorakyMapFragment newInstance() {
        RadiorakyMapFragment fragment = new RadiorakyMapFragment();
        return fragment;
    }

    public RadiorakyMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_diagnostics, container, false);

//        Number[] radiorakyValuesForPlot =

        List<Integer> radiorakyValuesForPlot = new ArrayList<Integer>();

        for (int i = 0, j = 0, k = 0; i < 12; i++, j++, k++) {
            if (j == 6) j = 12;
            if (k == 0) radiorakyValuesForPlot.add(0);
            if (k == 2) k = 0;
            radiorakyValuesForPlot.add(eksValues[j]);
            radiorakyValuesForPlot.add(eksValues[j + 6]);
            if (k == i) radiorakyValuesForPlot.add(0);
        }

        plot = (XYPlot) rootView.findViewById(R.id.mySimpleXYPlot);

        // Create an array of y-values to plot:
        Number[] values = { 0, 38, 43, 0, 65, 32, 0, 36, 32, 0, 48, 50, 0, 59, 43, 0, 30, 48, 0 };
        Number[] valuesLowBorder = new Number[19];
        Number[] valuesMiddleBorder = new Number[19];
        Number[] valuesHighBorder = new Number[19];
        for (int i = 0; i < 19; i++) {
            valuesLowBorder[i] = 40;
            valuesMiddleBorder[i] = 45;
            valuesHighBorder[i] = 50;
        }
        Number[] values3 = {  38, 43, 65, 32, 36, 32, 48, 50, 59, 43, 30, 48};
        Number[] values4 = {  1, 2, 4, 5, 7, 8, 10, 11, 13, 14, 16, 17};
        Number[] valuesBarValues = new Number[24];
        for ( int i = 0, j = 0; i < 24; i += 2, j++ ) {
            Log.d("test", "i = " + i + " " + "j = " + j);
            valuesBarValues[i] = values4[j];
            valuesBarValues[i + 1] = values3[j];
        }

        // Turn the above array into XYSeries':
        XYSeries seriesRadorakyBar = new SimpleXYSeries(Arrays.asList(values), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        XYSeries seriesRadiorakyBarValue = new SimpleXYSeries(Arrays.asList(valuesBarValues), SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Series2");

        XYSeries seriesLowBorderLine = new SimpleXYSeries(Arrays.asList(valuesLowBorder), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series4");
        XYSeries seriesMiddleBorderLine = new SimpleXYSeries(Arrays.asList(valuesMiddleBorder), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series4");
        XYSeries seriesHighBorderLine = new SimpleXYSeries(Arrays.asList(valuesHighBorder), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series5");

        //remove legend
        plot.getLayoutManager().remove(plot.getLegendWidget());
        //change X axis labels
        plot.getGraphWidget().setDomainValueFormat(new GraphXLabelFormat());
        /**
         * set Y
         */
        plot.setRangeBoundaries(0, 77, BoundaryMode.FIXED);
        plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 5);

        /**
         * set X
         */
        plot.setDomainBoundaries(0, 36, BoundaryMode.FIXED);
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
        //renderer.setBarWidth(100);

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
            String labelString = xLabels[parsedInt];
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
            if(series.getY(index).intValue() < 40) {
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

