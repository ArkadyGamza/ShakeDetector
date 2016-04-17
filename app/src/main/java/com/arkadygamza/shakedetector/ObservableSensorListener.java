package com.arkadygamza.shakedetector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.Log;

import rx.Observable;
import rx.android.MainThreadSubscription;

/**
 * Allows to treat sensor events as Observable
 */
public class ObservableSensorListener  {
    public static Observable<SensorEvent> create(@NonNull Sensor sensor, @NonNull SensorManager sensorManager){
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

                }
            };

            Log.d("!!!!", "registering listener");
            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);

            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    Log.d("!!!!", "UNregistering listener");
                    sensorManager.unregisterListener(listener);
                }
            });

        });


    }
}
