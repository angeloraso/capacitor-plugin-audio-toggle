package com.angeloraso.plugins.audiotoggle;

import static android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import com.angeloraso.plugins.audiotoggle.android.BuildWrapper;
import com.angeloraso.plugins.audiotoggle.android.Logger;
import java.util.List;

public class AudioDeviceManager {

    private static final String TAG = "AudioDeviceManager";

    private final Context context;

    private final AppCompatActivity appCompatActivity;
    private final Logger logger;
    private final AudioManager audioManager;
    private final BuildWrapper build;
    private AudioFocusRequest audioRequest = null;
    private AudioFocusRequestWrapper audioFocusRequest = new AudioFocusRequestWrapper();
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener;
    private boolean savedSpeakerphone = false;
    private int savedMode;
    public boolean isBluetoothConnected = false;

    public AudioDeviceManager(AppCompatActivity appCompatActivity, Context context, Logger logger, AudioManager audioManager) {
        this(appCompatActivity, context, logger, audioManager, new BuildWrapper());
    }

    public AudioDeviceManager(
        AppCompatActivity appCompatActivity,
        Context context,
        Logger logger,
        AudioManager audioManager,
        BuildWrapper build
    ) {
        this.appCompatActivity = appCompatActivity;
        this.context = context;
        this.logger = logger;
        this.audioManager = audioManager;
        this.build = build;
        this.audioFocusChangeListener =
            focusChange -> {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        logger.d(TAG, "AUDIO FOCUS GAIN");
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        logger.d(TAG, "AUDIO FOCUS LOSS");
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        logger.d(TAG, "AUDIO FOCUS LOSS TRANSIENT");
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        logger.d(TAG, "AUDIO FOCUS LOSS TRANSIENT CAN DUCK");
                        break;
                }
            };
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
        try {
            // Delay for Android 10/11
            if (!isAndroid12OrNewer() || isBluetoothConnected) {
                Thread.sleep(1250);
            }

            audioRequest = audioFocusRequest.buildRequest(audioFocusChangeListener);
            if (audioRequest != null) {
                int res = audioManager.requestAudioFocus(audioRequest);
                if (res == AUDIOFOCUS_REQUEST_GRANTED) {
                    appCompatActivity.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
                    savedSpeakerphone = audioManager.isSpeakerphoneOn();
                    savedMode = audioManager.getMode();
                    if (isAndroid13OrNewer()) {
                        audioManager.setMode(AudioManager.MODE_COMMUNICATION_REDIRECT);
                    } else {
                        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                    }

                    if (isAndroid12OrNewer()) {
                        AudioDeviceInfo deviceInfo = audioManager.getCommunicationDevice();
                        if (
                            deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                            deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET ||
                            deviceInfo.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO ||
                            isBluetoothConnected
                        ) {
                            enableSpeakerphone();
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            logger.d(TAG, "Set focus error");
            e.printStackTrace();
        }
    }

    public void setBluetoothConnected(boolean connected) {
        this.isBluetoothConnected = connected;
    }

    public void enableBluetoothSco() {
        if (isAndroid12OrNewer()) {
            audioManager.clearCommunicationDevice();
        } else {
            audioManager.setSpeakerphoneOn(false);
        }

        audioManager.startBluetoothSco();
        audioManager.setBluetoothScoOn(true);
    }

    public void disableBluetoothSco() {
        audioManager.stopBluetoothSco();
        audioManager.setBluetoothScoOn(false);
    }

    public void enableSpeakerphone() {
        disableBluetoothSco();
        if (isAndroid12OrNewer()) {
            AudioDeviceInfo deviceInfo = audioManager.getCommunicationDevice();
            if (deviceInfo.getType() == AudioDeviceInfo.TYPE_BUILTIN_EARPIECE && !isBluetoothConnected) {
                audioManager.clearCommunicationDevice();
            } else if (
                deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET ||
                deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                deviceInfo.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO ||
                isBluetoothConnected
            ) {
                AudioDeviceInfo speakerphoneDevice = getAudioDevice(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER);
                boolean success = audioManager.setCommunicationDevice(speakerphoneDevice);
                if (success) {
                    audioManager.setSpeakerphoneOn(true);
                } else {
                    logger.d(TAG, "Speakerphone error");
                }
            }
        } else {
            audioManager.setSpeakerphoneOn(true);
        }

        audioManager.setMode(AudioManager.MODE_NORMAL);
    }

    public void enableEarpiece() {
        if (isAndroid12OrNewer()) {
            AudioDeviceInfo earpieceDevice = getAudioDevice(AudioDeviceInfo.TYPE_BUILTIN_EARPIECE);
            boolean success = audioManager.setCommunicationDevice(earpieceDevice);
            if (!success) {
                logger.d(TAG, "Earpiece error");
            }
        } else {
            audioManager.setSpeakerphoneOn(false);
        }
    }

    public void enableWired() {
        if (isAndroid12OrNewer()) {
            audioManager.clearCommunicationDevice();
        } else {
            audioManager.setSpeakerphoneOn(false);
        }
    }

    public void enableRingtoneMode() {
        audioManager.setMode(AudioManager.MODE_RINGTONE);
        appCompatActivity.setVolumeControlStream(AudioManager.STREAM_RING);
        audioManager.setSpeakerphoneOn(false);
    }

    public void reset() {
        appCompatActivity.setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        audioManager.setMode(savedMode);
        audioManager.setSpeakerphoneOn(savedSpeakerphone);
        if (isAndroid12OrNewer()) {
            audioManager.clearCommunicationDevice();
        }
        disableBluetoothSco();
        if (audioRequest != null) {
            int res = audioManager.abandonAudioFocusRequest(audioRequest);
            if (res != AUDIOFOCUS_REQUEST_GRANTED) {
                logger.d(TAG, "Abandon audio focus request error");
            }
        }
    }

    public void mute(boolean mute) {
        audioManager.setMicrophoneMute(mute);
    }

    private boolean isAndroid12OrNewer() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
    }

    private boolean isAndroid13OrNewer() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }

    private AudioDeviceInfo getAudioDevice(Integer type) {
        if (isAndroid12OrNewer()) {
            List<AudioDeviceInfo> devices = audioManager.getAvailableCommunicationDevices();
            for (AudioDeviceInfo device : devices) {
                if (type == device.getType()) return device;
            }
        } else {
            AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for (AudioDeviceInfo device : devices) {
                if (type == device.getType()) return device;
            }
        }

        return null;
    }
}
