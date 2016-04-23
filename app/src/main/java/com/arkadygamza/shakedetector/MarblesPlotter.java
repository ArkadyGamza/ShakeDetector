package com.arkadygamza.shakedetector;

import android.graphics.Color;
import android.support.annotation.NonNull;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

public class MarblesPlotter {

    public static final int MAX_DATA_POINTS = 100;
    public static final int VIEWPORT_SECONDS = 2;

    private final long mStart = System.currentTimeMillis();

    private final PointsGraphSeries<DataPoint> mSeriesRed = new PointsGraphSeries<>();
    private final PointsGraphSeries<DataPoint> mSeriesBlue = new PointsGraphSeries<>();


    public MarblesPlotter(@NonNull GraphView graphView, int levelNum) {
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.VERTICAL);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(VIEWPORT_SECONDS * 1000); // number of ms in viewport
        graphView.getViewport().setMaxY(0);
        graphView.getViewport().setMinY(-levelNum + 1);
        mSeriesRed.setColor(Color.RED);
        mSeriesBlue.setColor(Color.BLUE);
        mSeriesRed.setShape(PointsGraphSeries.Shape.TRIANGLE);
        mSeriesBlue.setShape(PointsGraphSeries.Shape.TRIANGLE);
        graphView.addSeries(mSeriesRed);
        graphView.addSeries(mSeriesBlue);
    }

    public void addMarble(int level, boolean isRed) {
        DataPoint dataPoint = new DataPoint(getX(), -level);
        if (isRed) {
            mSeriesRed.appendData(dataPoint, true, MAX_DATA_POINTS);
        }
        else {
            mSeriesBlue.appendData(dataPoint, true, MAX_DATA_POINTS);
        }
    }

    private long getX() {
        return System.currentTimeMillis() - mStart;
    }

}
