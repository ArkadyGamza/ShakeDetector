package com.arkadygamza.shakedetector;

public class ShakeDetector {
    interface ShakeCallback{
        void onShake();
    }

    private ShakeCallback mShakeCallback;

    public void setShakeCallback(ShakeCallback shakeCallback) {
        mShakeCallback = shakeCallback;
    }

    public void reset(){}
    public void onGravEvent(float [] gravData){}
    public void onAccEven(float [] accData){}
}
