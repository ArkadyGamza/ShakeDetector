package com.arkadygamza.shakedetector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

import rx.Observable;
import rx.android.MainThreadSubscription;

/**
 * Allows to treat sensor events as Observable
 */
public class ObservableSensorListener {
    public static Observable<SensorEvent> create(@NonNull Sensor sensor, @NonNull SensorManager sensorManager) {
        return Observable.create(subscriber -> {
            SensorEventListener listener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (subscriber.isUnsubscribed()) {
                        return;
                    }

                    subscriber.onNext(event);
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    // NO-OP
                }
            };

            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);

            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    sensorManager.unregisterListener(listener);
                }
            });
        });
    }
}
