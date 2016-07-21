package com.arkadygamza.shakedetector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;

public class ShakeDetector {

    public static final int THRESHOLD = 13;
    public static final int SHAKES_COUNT = 3;
    public static final int SHAKES_PERIOD = 1;

    @NonNull
    public static Observable<?> create(@NonNull Context context) {
        return createAccelerationObservable(context)
            .filter(sensorEvent -> Math.abs(sensorEvent.values[0]) > THRESHOLD)
            .map(sensorEvent -> new XEvent(sensorEvent.timestamp, sensorEvent.values[0]))
            .buffer(2, 1)
            .filter(buf -> buf.get(0).x * buf.get(1).x < 0)
            .map(buf -> buf.get(1).timestamp / 1000000000f)
            .buffer(SHAKES_COUNT, 1)
            .filter(buf -> buf.get(SHAKES_COUNT - 1) - buf.get(0) < SHAKES_PERIOD)
            .throttleFirst(SHAKES_PERIOD, TimeUnit.SECONDS);
    }

    @NonNull
    private static Observable<SensorEvent> createAccelerationObservable(@NonNull Context context) {
        SensorManager mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);
        if (sensorList == null || sensorList.isEmpty()) {
            throw new IllegalStateException("Device has no linear acceleration sensor");
        }

        return SensorEventObservableFactory.createSensorEventObservable(sensorList.get(0), mSensorManager);
    }

    private static class XEvent {
        public final long timestamp;
        public final float x;

        private XEvent(long timestamp, float x) {
            this.timestamp = timestamp;
            this.x = x;
        }
    }
}
