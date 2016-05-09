package com.arkadygamza.shakedetector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;

public class MainActivity extends AppCompatActivity {

    private final List<SensorPlotter> mPlotters = new ArrayList<>(3);

    private Observable<?> mShakeObservable;
    private Subscription mShakeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupPlotters();

        mShakeObservable = ShakeDetector.create(this);
    }

    private void setupPlotters() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> gravSensors = sensorManager.getSensorList(Sensor.TYPE_GRAVITY);
        List<Sensor> accSensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        List<Sensor> linearAccSensors = sensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);

        mPlotters.add(new SensorPlotter("GRAV", (GraphView) findViewById(R.id.graph1), SensorEventObservableFactory.createSensorEventObservable(gravSensors.get(0), sensorManager)));
        mPlotters.add(new SensorPlotter("ACC", (GraphView) findViewById(R.id.graph2), SensorEventObservableFactory.createSensorEventObservable(accSensors.get(0), sensorManager)));
        mPlotters.add(new SensorPlotter("LIN", (GraphView) findViewById(R.id.graph3), SensorEventObservableFactory.createSensorEventObservable(linearAccSensors.get(0), sensorManager)));
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
}
