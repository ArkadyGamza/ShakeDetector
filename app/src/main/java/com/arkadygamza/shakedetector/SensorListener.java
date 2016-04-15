package com.arkadygamza.shakedetector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by Arkady Gamza on 15/04/2016.
 */
public class SensorListener implements SensorEventListener {
    public static final int MAX_DATA_POINTS = 50;
    public static final int VIEWPORT_SECONDS = 10;
    public static final int FPS = 10;

    @NonNull
    private final String mName;

    private final long mStart = System.currentTimeMillis();

    private final GraphView mGraphView;
    protected final LineGraphSeries<DataPoint> mSeries0;
    protected final LineGraphSeries<DataPoint> mSeries1;
    protected final LineGraphSeries<DataPoint> mSeries2;
    private long mLastUpdated = mStart;


    public SensorListener(@NonNull String name, GraphView graph, int colors0, int color1, int color2) {
        mName = name;
        mGraphView = graph;

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(VIEWPORT_SECONDS * 1000);//number of ms in viewport

        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);

        mSeries0 = new LineGraphSeries<>();
        mSeries1 = new LineGraphSeries<>();
        mSeries2 = new LineGraphSeries<>();
        mSeries0.setColor(colors0);
        mSeries1.setColor(color1);
        mSeries2.setColor(color2);

        graph.addSeries(mSeries0);
        graph.addSeries(mSeries1);
        graph.addSeries(mSeries2);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(!canUpdateUi()){
            return;
        }

//        Log.d("!!!", "event " + mName + " " + Arrays.toString(event.values));
        appendData(mSeries0, event.values[0]);
        appendData(mSeries1, event.values[1]);
        appendData(mSeries2, event.values[2]);
    }

    protected boolean canUpdateUi(){
        long now = System.currentTimeMillis();
        if (now - mLastUpdated < 1000 / FPS) {
            return false;
        }
        mLastUpdated = now;
        return true;
    }

    protected void appendData(LineGraphSeries<DataPoint> series, double value) {
        series.appendData(new DataPoint(getX(), value), true, MAX_DATA_POINTS);
    }

    private long getX() {
        return System.currentTimeMillis() - mStart;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
