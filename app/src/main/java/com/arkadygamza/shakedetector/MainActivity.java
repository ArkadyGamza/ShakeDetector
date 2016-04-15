package com.arkadygamza.shakedetector;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private Sensor mGravSensor;
    private SensorListener mGravListener;
    private SensorListener mAccListener;
    private Sensor mAccSensor;
    private SensorDeltaListener mDeltaListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> gravSensors = mSensorManager.getSensorList(Sensor.TYPE_GRAVITY);
        List<Sensor> accSensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        mGravSensor = gravSensors.get(0);
        mAccSensor = accSensors.get(0);

        dumpSensorInfo(gravSensors);
        dumpSensorInfo(accSensors);

        mGravListener = new SensorListener("GRAV", (GraphView) findViewById(R.id.graph1), Color.RED, Color.GREEN, Color.BLUE);
        mAccListener = new SensorListener("ACC", (GraphView) findViewById(R.id.graph2), Color.RED, Color.GREEN, Color.BLUE);
        mDeltaListener = new SensorDeltaListener("DELTA", (GraphView) findViewById(R.id.graph3), Color.RED, Color.GREEN, Color.BLUE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(mGravListener, mGravSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mAccListener, mAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mDeltaListener, mGravSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mDeltaListener, mAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mGravListener);
        mSensorManager.unregisterListener(mAccListener);
        mSensorManager.unregisterListener(mDeltaListener);
    }

    private void dumpSensorInfo(List<Sensor> gravSensors) {
        for (Sensor sensor : gravSensors) {
            StringBuilder sb = new StringBuilder();
            sb.append("Gravity sensor: " + sensor.getName());
            sb.append("\n resolution: " + sensor.getResolution());
            sb.append("\n max range: " + sensor.getMaximumRange());
            sb.append("\n vendor: " + sensor.getVendor());
            sb.append("\n version: " + sensor.getVersion());
            Log.d("!!!", sb.toString());
        }
    }
}
