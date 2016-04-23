package com.arkadygamza.shakedetector;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;

import rx.Observable;
import rx.Subscription;

public class MarblesActivity extends AppCompatActivity {

    private MarblesPlotter mMarblesPlotter;
    private Observable mShakeObservable;
    private Subscription mShakeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marbles);

        mMarblesPlotter = new MarblesPlotter((GraphView) findViewById(R.id.graph1), 7);
        mShakeObservable = ShakeDetector.create(this, mMarblesPlotter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShakeSubscription = mShakeObservable.subscribe((object) -> Utils.beep());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mShakeSubscription.unsubscribe();
    }
}
