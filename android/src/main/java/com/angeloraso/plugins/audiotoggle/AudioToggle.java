package com.angeloraso.plugins.audiotoggle;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.media.AudioManager;
import com.angeloraso.plugins.audiotoggle.AudioDevice.BluetoothHeadset;
import com.angeloraso.plugins.audiotoggle.AudioDevice.Earpiece;
import com.angeloraso.plugins.audiotoggle.AudioDevice.Speakerphone;
import com.angeloraso.plugins.audiotoggle.AudioDevice.WiredHeadset;
import com.angeloraso.plugins.audiotoggle.android.Logger;
import com.angeloraso.plugins.audiotoggle.android.ProductionLogger;
import com.angeloraso.plugins.audiotoggle.bluetooth.BluetoothHeadsetConnectionListener;
import com.angeloraso.plugins.audiotoggle.bluetooth.BluetoothHeadsetManager;
import com.angeloraso.plugins.audiotoggle.wired.WiredDeviceConnectionListener;
import com.angeloraso.plugins.audiotoggle.wired.WiredHeadsetReceiver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AudioToggle {

    private static final String TAG = "AudioToggle";
    private final Logger logger;
    private AudioDeviceManager audioDeviceManager;
    private WiredHeadsetReceiver wiredHeadsetReceiver;
    private AudioDeviceChangeListener audioDeviceChangeListener = null;
    private AudioDevice selectedDevice = null;
    private AudioDevice userSelectedDevice = null;
    private boolean wiredHeadsetAvailable = false;
    private ArrayList<AudioDevice> mutableAudioDevices = new ArrayList<>();
    private BluetoothHeadsetManager bluetoothHeadsetManager = null;
    private String bluetoothHeadsetConnected = null;
    private List<Class<? extends AudioDevice>> preferredDeviceList;

    enum State {
        STARTED,
        ACTIVATED,
        STOPPED
    }

    private State state = State.STOPPED;

    private BluetoothHeadsetConnectionListener bluetoothDeviceConnectionListener = new BluetoothHeadsetConnectionListener() {
        @Override
        public void onBluetoothHeadsetStateChanged(String headsetName) {
            if (!Objects.equals(bluetoothHeadsetConnected, headsetName)) {
                bluetoothHeadsetConnected = headsetName;
                if (headsetName != null) {
                    userSelectedDevice = new BluetoothHeadset();
                }
                enumerateDevices();
            }
        }

        @Override
        public void onBluetoothConnected(boolean connected) {
            if (state == State.ACTIVATED) {
                if (connected) {
                    userSelectedDevice = new BluetoothHeadset();
                }
            }
            enumerateDevices();
        }

        @Override
        public void onBluetoothHeadsetActivationError() {
            enumerateDevices();
        }
    };

    private WiredDeviceConnectionListener wiredDeviceConnectionListener = new WiredDeviceConnectionListener() {
        @Override
        public void onDeviceConnected() {
            wiredHeadsetAvailable = true;
            userSelectedDevice = new AudioDevice.WiredHeadset();
            enumerateDevices();
        }

        @Override
        public void onDeviceDisconnected() {
            if (wiredHeadsetAvailable) {
                wiredHeadsetAvailable = false;
                userSelectedDevice = selectedDevice;
                enumerateDevices();
            }
        }
    };

    public boolean isLoggingEnabled() {
        return logger.isLoggingEnabled();
    }

    AudioDevice selectedAudioDevice = selectedDevice;
    List<AudioDevice> availableAudioDevices = mutableAudioDevices;

    public AudioToggle(final Context context, boolean log) {
        this.logger = new ProductionLogger(log);

        this.audioDeviceManager =
            new AudioDeviceManager(context, this.logger, (AudioManager) context.getSystemService(Context.AUDIO_SERVICE));
        this.wiredHeadsetReceiver = new WiredHeadsetReceiver(context, this.logger);
        this.bluetoothHeadsetManager =
            BluetoothHeadsetManager.newInstance(context, logger, BluetoothAdapter.getDefaultAdapter(), audioDeviceManager);
        this.preferredDeviceList = this.getPreferredDeviceList(defaultPreferredDeviceList);
        this.logger.d(
                TAG,
                "Preferred device list = " + this.preferredDeviceList.stream().map(Class::getSimpleName).collect(Collectors.toList())
            );
    }

    public void start() {
        switch (state) {
            case STOPPED:
                state = State.STARTED;
                enumerateDevices();
                startBluetoothListener();
                startWiredConnectionListener();
                break;
            default:
                logger.d(TAG, "Redundant start() invocation while already in the started or activated state");
        }
    }

    public void startBluetoothListener() {
        if (bluetoothHeadsetManager != null) {
            bluetoothHeadsetManager.start(bluetoothDeviceConnectionListener);
        }
    }

    public void startWiredConnectionListener() {
        wiredHeadsetReceiver.start(wiredDeviceConnectionListener);
    }

    public void setAudioToggleEventListener(AudioDeviceChangeListener listener) {
        audioDeviceChangeListener = listener;
    }

    public void stop() {
        switch (state) {
            case ACTIVATED:
                deactivate();
                closeListeners();
                break;
            case STARTED:
                closeListeners();
                break;
            case STOPPED:
                logger.d(TAG, "Redundant stop() invocation while already in the stopped state");
                break;
        }
    }

    public void activate() {
        switch (state) {
            case STARTED:
                state = State.ACTIVATED;
                // Always set mute to false for WebRTC
                audioDeviceManager.mute(false);
                audioDeviceManager.setAudioFocus();
                enumerateDevices();
                break;
            case ACTIVATED:
                if (selectedDevice != null) {
                    activate(selectedDevice);
                }
                break;
            case STOPPED:
                throw new IllegalStateException();
        }
    }

    public void deactivate() {
        switch (state) {
            case ACTIVATED:
                state = State.STARTED;
                if (bluetoothHeadsetManager != null) {
                    bluetoothHeadsetManager.deactivate();
                }

                audioDeviceManager.reset();
                break;
            case STARTED:
            case STOPPED:
                break;
        }
    }

    public void selectDevice(String deviceName) {
        AudioDevice audioDevice;

        Optional<AudioDevice> result = mutableAudioDevices.stream().filter(_device -> _device.getName().equals(deviceName)).findFirst();

        if (result.isPresent()) {
            audioDevice = result.get();
        } else {
            audioDevice = selectedDevice;
        }

        logger.d(TAG, "Selected AudioDevice = " + audioDevice);
        userSelectedDevice = audioDevice;
        if (state == State.STARTED) {
            activate();
        } else {
            enumerateDevices();
        }
    }

    public boolean isBluetoothEnabled() {
        return bluetoothHeadsetManager.isEnabled();
    }

    private List<Class<? extends AudioDevice>> getPreferredDeviceList(List<Class<? extends AudioDevice>> preferredDeviceList) {
        if (!hasNoDuplicates(preferredDeviceList)) {
            throw new IllegalArgumentException("preferredDeviceList has duplicates");
        }

        if (preferredDeviceList.isEmpty() || preferredDeviceList.equals(defaultPreferredDeviceList)) {
            return defaultPreferredDeviceList;
        } else {
            List<Class<? extends AudioDevice>> result = new ArrayList<>(defaultPreferredDeviceList);
            result.removeAll(preferredDeviceList);
            for (int i = 0; i < preferredDeviceList.size(); i++) {
                Class<? extends AudioDevice> device = preferredDeviceList.get(i);
                result.add(i, device);
            }
            return result;
        }
    }

    private boolean hasNoDuplicates(List<Class<? extends AudioDevice>> list) {
        return list
            .stream()
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet()
            .stream()
            .noneMatch(entry -> entry.getValue() > 1);
    }

    public void setRingtoneMode() {
        audioDeviceManager.enableRingtoneMode();
    }

    private void activate(AudioDevice audioDevice) {
        if (audioDevice instanceof BluetoothHeadset) {
            if (bluetoothHeadsetManager != null) {
                bluetoothHeadsetManager.activate();
            }
        } else if (audioDevice instanceof Earpiece) {
            if (bluetoothHeadsetManager != null) {
                bluetoothHeadsetManager.deactivate();
            }
            audioDeviceManager.enableEarpiece();
        } else if (audioDevice instanceof WiredHeadset) {
            if (bluetoothHeadsetManager != null) {
                bluetoothHeadsetManager.deactivate();
            }
            audioDeviceManager.enableWired();
        } else if (audioDevice instanceof Speakerphone) {
            if (bluetoothHeadsetManager != null) {
                bluetoothHeadsetManager.deactivate();
            }
            audioDeviceManager.enableSpeakerphone();
        }
    }

    public class AudioDeviceState {

        private List<AudioDevice> audioDeviceList;
        private AudioDevice selectedAudioDevice;

        AudioDeviceState(List<AudioDevice> audioDeviceList, AudioDevice selectedAudioDevice) {
            this.audioDeviceList = audioDeviceList;
            this.selectedAudioDevice = selectedAudioDevice;
        }
    }

    private void enumerateDevices() {
        // save off the old state and 'semi'-deep copy the list of audio devices
        AudioDeviceState oldAudioDeviceState = new AudioDeviceState(new ArrayList<>(mutableAudioDevices), selectedDevice);
        mutableAudioDevices.clear();

        // update audio device list and selected device
        mutableAudioDevices = getAvailableAudioDevices();

        if (!userSelectedDevicePresent(mutableAudioDevices)) {
            userSelectedDevice = null;
        }

        // Select the audio device
        logger.d(TAG, "Current user selected AudioDevice = " + userSelectedDevice);
        if (userSelectedDevice != null) {
            selectedDevice = userSelectedDevice;
        } else if (mutableAudioDevices.size() > 0) {
            AudioDevice firstAudioDevice = mutableAudioDevices.get(0);
            /*
             * If there was an error starting bluetooth sco, then the selected AudioDevice should
             * be the next valid device in the list.
             */
            if (
                firstAudioDevice instanceof BluetoothHeadset &&
                bluetoothHeadsetManager != null &&
                bluetoothHeadsetManager.hasActivationError()
            ) {
                selectedDevice = mutableAudioDevices.get(1);
            } else {
                selectedDevice = firstAudioDevice;
            }
        } else {
            selectedDevice = null;
        }

        // Activate the device if in the active state
        if (state == State.ACTIVATED) {
            activate(selectedDevice);
        }

        // trigger audio device change listener if there has been a change
        AudioDeviceState newAudioDeviceState = new AudioDeviceState(mutableAudioDevices, selectedDevice);
        if (!newAudioDeviceState.equals(oldAudioDeviceState)) {
            if (audioDeviceChangeListener != null) {
                audioDeviceChangeListener.onAudioDevicesChanged(mutableAudioDevices, selectedDevice);
            }
        }
    }

    private ArrayList<AudioDevice> getAvailableAudioDevices() {
        ArrayList<AudioDevice> devices = new ArrayList<>();
        for (Class<? extends AudioDevice> audioDevice : preferredDeviceList) {
            if (audioDevice.equals(BluetoothHeadset.class)) {
                BluetoothHeadset bluetoothHeadset = bluetoothHeadsetManager != null ? bluetoothHeadsetManager.getHeadset() : null;
                if (bluetoothHeadset != null) {
                    devices.add(bluetoothHeadset);
                }
            } else if (audioDevice.equals(WiredHeadset.class)) {
                if (wiredHeadsetAvailable) {
                    devices.add(new WiredHeadset());
                }
            } else if (audioDevice.equals(Earpiece.class)) {
                if (audioDeviceManager.hasEarpiece() && !wiredHeadsetAvailable) {
                    devices.add(new Earpiece());
                }
            } else if (audioDevice.equals(Speakerphone.class)) {
                if (audioDeviceManager.hasSpeakerphone()) {
                    devices.add(new Speakerphone());
                }
            }
        }

        logger.d(TAG, "Available AudioDevice list updated: " + devices);
        return devices;
    }

    private boolean userSelectedDevicePresent(List<AudioDevice> audioDevices) {
        return Optional
            .ofNullable(userSelectedDevice)
            .map(
                selectedDevice -> {
                    if (selectedDevice instanceof BluetoothHeadset) {
                        Optional<AudioDevice> newHeadset = audioDevices
                            .stream()
                            .filter(device -> device instanceof BluetoothHeadset)
                            .findFirst();
                        newHeadset.ifPresent(newHeadsetDevice -> userSelectedDevice = newHeadsetDevice);
                        return newHeadset.isPresent();
                    } else {
                        List<AudioDevice> devices = audioDevices
                            .stream()
                            .filter(device -> device.getName().equals(selectedDevice.getName()))
                            .collect(Collectors.toList());
                        return devices.size() != 0;
                    }
                }
            )
            .orElse(false);
    }

    private void closeListeners() {
        state = State.STOPPED;
        if (bluetoothHeadsetManager != null) {
            bluetoothHeadsetManager.stop();
        }
        if (wiredHeadsetReceiver != null) {
            wiredHeadsetReceiver.stop();
        }
        audioDeviceChangeListener = null;
    }

    public static final List<Class<? extends AudioDevice>> defaultPreferredDeviceList = Arrays.asList(
        BluetoothHeadset.class,
        WiredHeadset.class,
        Earpiece.class,
        Speakerphone.class
    );
}
