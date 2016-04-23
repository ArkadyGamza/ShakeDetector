package com.arkadygamza.shakedetector;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;

public class MainActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private final List<SensorPlotter> mPlotters = new ArrayList<>(3);

    private Observable mShakeObservable;
    private Subscription mShakeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> gravSensors = mSensorManager.getSensorList(Sensor.TYPE_GRAVITY);
        List<Sensor> accSensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        List<Sensor> linearAccSensors = mSensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);

        dumpSensorInfo(gravSensors);
        dumpSensorInfo(accSensors);
        dumpSensorInfo(linearAccSensors);

        mPlotters.add(new SensorPlotter("GRAV", (GraphView) findViewById(R.id.graph1), ObservableSensorListener.create(gravSensors.get(0), mSensorManager)));
        mPlotters.add(new SensorPlotter("ACC", (GraphView) findViewById(R.id.graph2), ObservableSensorListener.create(accSensors.get(0), mSensorManager)));
        mPlotters.add(new SensorPlotter("LIN", (GraphView) findViewById(R.id.graph3), ObservableSensorListener.create(linearAccSensors.get(0), mSensorManager)));

        mShakeObservable = ShakeDetector.create(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Observable.from(mPlotters).subscribe(SensorPlotter::onResume);
        mShakeSubscription = mShakeObservable.subscribe((object) -> Utils.beep());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Observable.from(mPlotters).subscribe(SensorPlotter::onPause);
        mShakeSubscription.unsubscribe();
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

    public void onClick(View view) {
        startActivity(new Intent(this, MarblesActivity.class));
    }
}
