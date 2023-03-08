package com.angeloraso.plugins.audiotoggle;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Build;
import com.angeloraso.plugins.audiotoggle.android.BuildWrapper;
import com.angeloraso.plugins.audiotoggle.android.Logger;

public class AudioDeviceManager {

    private static final String TAG = "AudioDeviceManager";

    private final Context context;
    private final Logger logger;
    private final AudioManager audioManager;
    private final BuildWrapper build;
    private final AudioFocusRequestWrapper audioFocusRequest;
    private final OnAudioFocusChangeListener audioFocusChangeListener;

    private int savedAudioMode = 0;
    private boolean savedIsMicrophoneMuted = false;
    private boolean savedSpeakerphoneEnabled = false;
    private AudioFocusRequest audioRequest = null;

    public AudioDeviceManager(
        Context context,
        Logger logger,
        AudioManager audioManager,
        OnAudioFocusChangeListener audioFocusChangeListener
    ) {
        this(context, logger, audioManager, new BuildWrapper(), new AudioFocusRequestWrapper(), audioFocusChangeListener);
    }

    public AudioDeviceManager(
        Context context,
        Logger logger,
        AudioManager audioManager,
        BuildWrapper build,
        AudioFocusRequestWrapper audioFocusRequest,
        OnAudioFocusChangeListener audioFocusChangeListener
    ) {
        this.context = context;
        this.logger = logger;
        this.audioManager = audioManager;
        this.build = build;
        this.audioFocusRequest = audioFocusRequest;
        this.audioFocusChangeListener = audioFocusChangeListener;
    }

    public boolean hasEarpiece() {
        boolean hasEarpiece = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
        if (hasEarpiece) {
            logger.d(TAG, "Earpiece available");
        }
        return hasEarpiece;
    }

    public boolean hasSpeakerphone() {
        if (
            build.getVersion() >= Build.VERSION_CODES.M && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)
        ) {
            AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for (AudioDeviceInfo device : devices) {
                if (device.getType() == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {
                    logger.d(TAG, "Speakerphone available");
                    return true;
                }
            }
            return false;
        } else {
            logger.d(TAG, "Speakerphone available");
            return true;
        }
    }

    public void setAudioFocus() {
        // Request audio focus before making any device switch.
        audioRequest = audioFocusRequest.buildRequest(audioFocusChangeListener);
        if (audioRequest != null) {
            audioManager.requestAudioFocus(audioRequest);
        }
        /*
         * Start by setting MODE_IN_COMMUNICATION as default audio mode. It is
         * required to be in this mode when playout and/or recording starts for
         * best possible VoIP performance. Some devices have difficulties with speaker mode
         * if this is not set.
         */
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    public void enableBluetoothSco(boolean enable) {
        if (enable) {
            audioManager.startBluetoothSco();
        } else {
            audioManager.stopBluetoothSco();
        }
    }

    public void enableSpeakerphone(boolean enable) {
        audioManager.setSpeakerphoneOn(enable);
    }

    public void mute(boolean mute) {
        audioManager.setMicrophoneMute(mute);
    }

    // TODO Consider persisting audio state in the event of process death
    public void cacheAudioState() {
        savedAudioMode = audioManager.getMode();
        savedIsMicrophoneMuted = audioManager.isMicrophoneMute();
        savedSpeakerphoneEnabled = audioManager.isSpeakerphoneOn();
    }

    public void restoreAudioState() {
        audioManager.setMode(savedAudioMode);
        mute(savedIsMicrophoneMuted);
        enableSpeakerphone(savedSpeakerphoneEnabled);
        if (audioRequest != null) {
            audioManager.abandonAudioFocusRequest(audioRequest);
        }
    }
}
