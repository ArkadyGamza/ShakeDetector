package com.arkadygamza.shakedetector;

import android.media.AudioManager;
import android.media.ToneGenerator;

public class Utils {
    public static void beep() {
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_SYSTEM, 100);
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
    }
}
