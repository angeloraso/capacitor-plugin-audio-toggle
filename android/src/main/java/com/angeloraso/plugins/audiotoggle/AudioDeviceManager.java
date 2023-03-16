package com.angeloraso.plugins.audiotoggle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Build;
import com.angeloraso.plugins.audiotoggle.android.BuildWrapper;
import com.angeloraso.plugins.audiotoggle.android.Logger;
import java.util.List;

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
            return getAudioDevice(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) != null;
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
        if (!isAndroid12OrNewer()) {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        }
    }

    @SuppressLint("NewApi")
    public void enableBluetoothSco(boolean enable) {
        if (isAndroid12OrNewer()) {
            if (enable) {
                AudioDeviceInfo bluetoothDevice = getAudioDevice(AudioDeviceInfo.TYPE_BLUETOOTH_SCO);
                audioManager.setCommunicationDevice(bluetoothDevice);
            } else {
                audioManager.clearCommunicationDevice();
            }
        } else {
            if (enable) {
                audioManager.startBluetoothSco();
            } else {
                audioManager.stopBluetoothSco();
            }
        }
    }

    @SuppressLint("NewApi")
    public void enableSpeakerphone(boolean enable) {
        if (isAndroid12OrNewer()) {
            audioManager.clearCommunicationDevice();
            if (enable) {
                AudioDeviceInfo speakerDevice = getAudioDevice(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER);
                boolean result = audioManager.setCommunicationDevice(speakerDevice);
                if (!result) {
                    logger.d(TAG, "Speakerphone error");
                }
            } else {
                AudioDeviceInfo earpieceDevice = getAudioDevice(AudioDeviceInfo.TYPE_BUILTIN_EARPIECE);
                audioManager.setMode(AudioManager.MODE_NORMAL);
                boolean result = audioManager.setCommunicationDevice(earpieceDevice);
                if (!result) {
                    logger.d(TAG, "Earpiece error");
                }
            }
        } else {
            audioManager.setSpeakerphoneOn(enable);
        }
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

    private boolean isAndroid12OrNewer() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
    }

    private AudioDeviceInfo getAudioDevice(Integer type) {
        AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        for (AudioDeviceInfo device : devices) {
            if (type == device.getType()) return device;
        }

        return null;
    }
}
