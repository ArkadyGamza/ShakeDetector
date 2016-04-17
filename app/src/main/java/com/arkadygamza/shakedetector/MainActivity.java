package com.arkadygamza.shakedetector;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jjoe64.graphview.GraphView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    public static final int THRESHOLD = 15;
    public static final int SHAKES_COUNT = 3;
    public static final int SHAKES_PERIOD = 1;
    private SensorManager mSensorManager;
    private Sensor mGravSensor;
    private SensorListener mGravListener;
    private SensorListener mAccListener;
    private Sensor mAccSensor;
    private SensorDeltaListener mDeltaListener;
    private Observable<SensorEvent> mAccelerationObservable;
    private CompositeSubscription mCompositeSubscription;
    private Observable<SensorEvent> mGravityObservable;

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

        mAccelerationObservable = ObservableSensorListener.create(mAccSensor, mSensorManager);
        mGravityObservable = ObservableSensorListener.create(mGravSensor, mSensorManager);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(mGravListener, mGravSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mAccListener, mAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mDeltaListener, mGravSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mDeltaListener, mAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mCompositeSubscription = new CompositeSubscription();

        Observable<Float> gravityObservable = mGravityObservable.map(sensorEvent -> sensorEvent.values[0]);

        Subscription subscription = mAccelerationObservable
            .map(sensorEvent -> sensorEvent.values[0])
            .withLatestFrom(gravityObservable, (acceleration, gravity) -> acceleration - gravity)
            .filter(shake -> shake > THRESHOLD || shake < -THRESHOLD)
            .buffer(2, 1)
            .filter(buf -> buf.get(0) * buf.get(1) < 0)
            .doOnNext(val -> Log.d("!!!!", "filtered by sign: " + val))
            .timestamp()
            .buffer(SHAKES_COUNT, 1)
            .filter(buffer -> buffer.get(buffer.size() - 1).getTimestampMillis() - buffer.get(0).getTimestampMillis() < SHAKES_PERIOD*1000)
            .throttleFirst(SHAKES_PERIOD, TimeUnit.SECONDS)
            .subscribe(shake -> {Log.d("!!!!", "BOOM " + shake); beep();});

        mCompositeSubscription.add(subscription);
    }

    private static void beep() {
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_SYSTEM, 100);
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mGravListener);
        mSensorManager.unregisterListener(mAccListener);
        mSensorManager.unregisterListener(mDeltaListener);

        mCompositeSubscription.unsubscribe();
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
