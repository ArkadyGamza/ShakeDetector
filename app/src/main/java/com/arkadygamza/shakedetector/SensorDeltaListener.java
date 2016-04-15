package com.arkadygamza.shakedetector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.annotation.NonNull;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by Arkady Gamza on 15/04/2016.
 */
public class SensorDeltaListener extends SensorListener {

    float [] mLastGravValues = {0,0,0};

    public SensorDeltaListener(@NonNull String name, GraphView graph, int colors0, int color1, int color2) {
        super(name, graph, colors0, color1, color2);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_GRAVITY){
            mLastGravValues = event.values;
            return;
        }

        if(!canUpdateUi()){
            return;
        }

        appendData(mSeries0, event.values[0] - mLastGravValues[0]);
        appendData(mSeries1, event.values[1] - mLastGravValues[1]);
        appendData(mSeries2, event.values[2] - mLastGravValues[2]);
    }

}
