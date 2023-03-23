package com.angeloraso.plugins.audiotoggle;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import com.angeloraso.plugins.audiotoggle.android.BuildWrapper;
import com.angeloraso.plugins.audiotoggle.android.Logger;
import java.util.List;
import java.util.regex.Pattern;

public class AudioDeviceManager {

    private static final String TAG = "AudioDeviceManager";

    private final Context context;
    private final Logger logger;
    private final AudioManager audioManager;
    private final BuildWrapper build;
    private final Pattern samsungPattern = Pattern.compile("^SM-G(960|99)");

    public AudioDeviceManager(Context context, Logger logger, AudioManager audioManager) {
        this(context, logger, audioManager, new BuildWrapper());
    }

    public AudioDeviceManager(Context context, Logger logger, AudioManager audioManager, BuildWrapper build) {
        this.context = context;
        this.logger = logger;
        this.audioManager = audioManager;
        this.build = build;
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
        new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).build();
    }

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
                audioManager.setBluetoothScoOn(true);
            } else {
                audioManager.stopBluetoothSco();
                audioManager.setBluetoothScoOn(false);
            }
        }
    }

    public void enableSpeakerphone() {
        if (audioManager.isBluetoothScoOn()) {
            enableBluetoothSco(false);
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
        }

        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        audioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);

        if (isAndroid12OrNewer()) {
            AudioDeviceInfo speakerDevice = getAudioDevice(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER);
            boolean success = audioManager.setCommunicationDevice(speakerDevice);
            if (!success) {
                logger.d(TAG, "Speakerphone error");
            }
        } else {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
        }

        if (!audioManager.isSpeakerphoneOn() && samsungPattern.matcher(Build.MODEL).find()) {
            AudioDeviceInfo usbDevice = getAudioDevice(AudioDeviceInfo.TYPE_USB_HEADSET);
            if (usbDevice != null) {
                audioManager.setMode(AudioManager.MODE_NORMAL);
            }
        }
    }

    public void enableEarpiece() {
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        audioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
        if (isAndroid12OrNewer()) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.clearCommunicationDevice();
            AudioDeviceInfo earpieceDevice = getAudioDevice(AudioDeviceInfo.TYPE_BUILTIN_EARPIECE);
            boolean success = audioManager.setCommunicationDevice(earpieceDevice);
            if (!success) {
                logger.d(TAG, "Earpiece error");
            }
        } else {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.setSpeakerphoneOn(false);
        }
    }

    public void enableWired() {
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
        audioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);

        if (isAndroid12OrNewer()) {
            AudioDeviceInfo wiredHeadsetDevice = getAudioDevice(AudioDeviceInfo.TYPE_WIRED_HEADSET);
            AudioDeviceInfo wiredHeadphonesDevice = getAudioDevice(AudioDeviceInfo.TYPE_WIRED_HEADPHONES);
            boolean success;
            if (wiredHeadsetDevice != null) {
                success = audioManager.setCommunicationDevice(wiredHeadsetDevice);
            } else {
                success = audioManager.setCommunicationDevice(wiredHeadphonesDevice);
            }
            if (!success) {
                logger.d(TAG, "Earpiece error");
            }
        } else {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        }
    }

    public void enableRingtoneMode() {
        audioManager.setMode(AudioManager.MODE_RINGTONE);
        audioManager.setSpeakerphoneOn(false);
    }

    public void reset() {
        audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(false);
    }

    public void mute(boolean mute) {
        audioManager.setMicrophoneMute(mute);
    }

    private boolean isAndroid12OrNewer() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
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
