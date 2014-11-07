package com.vadim.nakatani.fragments.home_screen;

import android.app.Activity;
import android.app.Fragment;
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
import java.util.Arrays;


/**
 *
 */
public class DiagnosticsFragment extends Fragment {
    private XYPlot plot;
    int[] intensities;

    public static DiagnosticsFragment newInstance() {
        DiagnosticsFragment fragment = new DiagnosticsFragment();
        return fragment;
    }

    public DiagnosticsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_diagnostics, container, false);

        plot = (XYPlot) rootView.findViewById(R.id.mySimpleXYPlot);

        // Create an array of y-values to plot:
        Number[] values = { 0, 38, 43, 0, 65, 32, 0, 36, 32, 0, 48, 50, 0, 59, 43, 0, 30, 48, 0 };
        Number[] values1 = new Number[19];
        Number[] values2 = new Number[19];
        for (int i = 0; i < 19; i++) {
            values1[i] = 40;
            values2[i] = 50;
        }
        Number[] values3 = {  38, 43, 65, 32, 36, 32, 48, 50, 59, 43, 30, 48};
        Number[] values4 = {  1, 2, 4, 5, 7, 8, 10, 11, 13, 14, 16, 17};
        Number[] values5 = new Number[24];
        for ( int i = 0, j = 0; i < 24; i += 2, j++ ) {
            Log.d("test", "i = " + i + " " + "j = " + j);
            values5[i] = values4[j];
            values5[i + 1] = values3[j];
        }

        // Turn the above array into XYSeries':
        XYSeries series1 = new SimpleXYSeries(Arrays.asList(values), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        XYSeries series2 = new SimpleXYSeries(Arrays.asList(values1), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        XYSeries series3 = new SimpleXYSeries(Arrays.asList(values2), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        XYSeries series4 = new SimpleXYSeries(Arrays.asList(values5), SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Series1");


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
        plot.setDomainBoundaries(0, 18, BoundaryMode.FIXED);
        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);

        plot.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
        plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);

        plot.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
        plot.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);

        plot.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLACK);
        plot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        plot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);




        // Add a new series to the xyplot:
        MyBarFormatter formatter1 = new MyBarFormatter(Color.argb(200, 100, 150, 100), Color.LTGRAY);
        plot.addSeries(series1, formatter1);

        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.CYAN, 0x00000000, 0x00000000, null);
        plot.addSeries(series2, series1Format);

        LineAndPointFormatter series1Format2 = new LineAndPointFormatter(Color.RED, 0x00000000, 0x00000000, null);
        plot.addSeries(series3, series1Format2);

        LineAndPointFormatter series1Format3 = new LineAndPointFormatter(0x00000000, 0x00000000, 0x00000000, new PointLabelFormatter(Color.BLACK));
        plot.addSeries(series4, series1Format3);

        // Give each bar gap
        MyBarRenderer renderer = ((MyBarRenderer) plot.getRenderer(MyBarRenderer.class));
        renderer.setBarWidthStyle(BarRenderer.BarWidthStyle.VARIABLE_WIDTH);
        //renderer.setBarWidth(100);

        renderer.setBarGap(0f);

        return rootView;
    }
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
        if(series.getY(index).intValue() > 50) {
            return new MyBarFormatter(Color.RED, Color.TRANSPARENT);
        } else {
            return new MyBarFormatter(Color.GREEN, Color.TRANSPARENT);
        }
    }
}
