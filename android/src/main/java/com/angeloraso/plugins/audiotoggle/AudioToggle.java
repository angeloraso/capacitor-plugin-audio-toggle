package com.angeloraso.plugins.audiotoggle;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Build;
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
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AudioToggle {

    private static final String TAG = "AudioToggle";
    private final Context context;
    private final Logger logger;
    private AudioDeviceManager audioDeviceManager;
    private WiredHeadsetReceiver wiredHeadsetReceiver;
    private AudioDeviceChangeListener audioDeviceChangeListener = null;
    private AudioDevice selectedDevice = null;
    private AudioDevice userSelectedDevice = null;
    private boolean wiredHeadsetAvailable = false;
    private ArrayList<AudioDevice> mutableAudioDevices = new ArrayList<>();
    private BluetoothHeadsetManager bluetoothHeadsetManager = null;
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
            enumerateDevices(headsetName);
        }

        @Override
        public void onBluetoothHeadsetActivationError() {
            if (userSelectedDevice instanceof BluetoothHeadset) {
                userSelectedDevice = null;
            }
            enumerateDevices(null);
        }
    };

    private WiredDeviceConnectionListener wiredDeviceConnectionListener = new WiredDeviceConnectionListener() {
        @Override
        public void onDeviceConnected() {
            wiredHeadsetAvailable = true;
            enumerateDevices(null);
        }

        @Override
        public void onDeviceDisconnected() {
            wiredHeadsetAvailable = false;
            enumerateDevices(null);
        }
    };

    public boolean isLoggingEnabled() {
        return logger.isLoggingEnabled();
    }

    public void setLoggingEnabled(boolean value) {
        logger.setLoggingEnabled(value);
    }

    AudioDevice selectedAudioDevice = selectedDevice;
    List<AudioDevice> availableAudioDevices = mutableAudioDevices;

    public AudioToggle(final Context context, boolean log) {
        this.context = context;
        this.logger = new ProductionLogger(log);

        this.audioDeviceManager =
            new AudioDeviceManager(context, this.logger, (AudioManager) context.getSystemService(Context.AUDIO_SERVICE), focusChange -> {});
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
                enumerateDevices(null);
                if (bluetoothHeadsetManager != null) {
                    bluetoothHeadsetManager.start(bluetoothDeviceConnectionListener);
                }
                wiredHeadsetReceiver.start(wiredDeviceConnectionListener);
                state = State.STARTED;
                break;
            default:
                logger.d(TAG, "Redundant start() invocation while already in the started or activated state");
        }
    }

    /**
     * Starts listening for audio device changes and calls the provided listener upon each change.
     * Note that when audio device listening is no longer needed, AudioToggle.stop() should be called
     * in order to prevent a memory leak.
     *
     * @param listener the listener to call upon each audio device change
     */
    public void setAudioToggleEventListener(AudioDeviceChangeListener listener) {
        audioDeviceChangeListener = listener;
    }

    /**
     * Stops listening for audio device changes if AudioToggle.start() has already been invoked.
     * AudioToggle.deactivate() will also get called if a device has been activated with
     * AudioToggle.activate().
     */
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

    /**
     * Performs audio routing and unmuting on the selected device from
     * [AudioToggle.selectDevice]. Audio focus is also acquired for the client application.
     * **Note:** [AudioToggle.deactivate] should be invoked to restore the prior audio
     * state.
     */
    public void activate() {
        switch (state) {
            case STARTED:
                audioDeviceManager.cacheAudioState();

                // Always set mute to false for WebRTC
                audioDeviceManager.mute(false);
                audioDeviceManager.setAudioFocus();
                if (selectedDevice != null) {
                    activate(selectedDevice);
                }
                state = State.ACTIVATED;
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

    /**
     * Restores the audio state prior to calling [AudioToggle.activate] and removes
     * audio focus from the client application.
     */
    public void deactivate() {
        switch (state) {
            case ACTIVATED:
                if (bluetoothHeadsetManager != null) {
                    bluetoothHeadsetManager.deactivate();
                }

                // Restore stored audio state
                audioDeviceManager.restoreAudioState();
                state = State.STARTED;
                break;
            case STARTED:
            case STOPPED:
                break;
        }
    }

    /**
     * Selects the desired [audioDevice]. If the provided [AudioDevice] is not
     * available, no changes are made. If the provided device is null, one is chosen based on the
     * specified preferred device list or the following default list:
     * [BluetoothHeadset], [WiredHeadset], [Earpiece], [Speakerphone].
     */
    public void selectDevice(String deviceName) {
        AudioDevice audioDevice;

        List<AudioDevice> devices = mutableAudioDevices
            .stream()
            .filter(device -> device.getName().equals(deviceName))
            .collect(Collectors.toList());

        if (devices.size() != 0) {
            audioDevice = devices.get(0);
        } else {
            audioDevice = selectedDevice;
        }

        if (selectedDevice != audioDevice) {
            logger.d(TAG, "Selected AudioDevice = " + audioDevice);
            userSelectedDevice = audioDevice;
            enumerateDevices(null);
        }
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

    private void activate(AudioDevice audioDevice) {
        if (audioDevice instanceof BluetoothHeadset) {
            audioDeviceManager.enableSpeakerphone(false);
            if (bluetoothHeadsetManager != null) {
                bluetoothHeadsetManager.activate();
            }
        } else if (audioDevice instanceof Earpiece || audioDevice instanceof WiredHeadset) {
            audioDeviceManager.enableSpeakerphone(false);
            if (bluetoothHeadsetManager != null) {
                bluetoothHeadsetManager.deactivate();
            }
        } else if (audioDevice instanceof Speakerphone) {
            audioDeviceManager.enableSpeakerphone(true);
            if (bluetoothHeadsetManager != null) {
                bluetoothHeadsetManager.deactivate();
            }
        }
    }

    public class AudioDeviceState {

        private List<AudioDevice> audioDeviceList;
        private AudioDevice selectedAudioDevice;

        public AudioDeviceState(List<AudioDevice> audioDeviceList, AudioDevice selectedAudioDevice) {
            this.audioDeviceList = audioDeviceList;
            this.selectedAudioDevice = selectedAudioDevice;
        }

        public List<AudioDevice> getAudioDeviceList() {
            return audioDeviceList;
        }

        public void setAudioDeviceList(List<AudioDevice> audioDeviceList) {
            this.audioDeviceList = audioDeviceList;
        }

        public AudioDevice getSelectedAudioDevice() {
            return selectedAudioDevice;
        }

        public void setSelectedAudioDevice(AudioDevice selectedAudioDevice) {
            this.selectedAudioDevice = selectedAudioDevice;
        }
    }

    private void enumerateDevices(String bluetoothHeadsetName) {
        // save off the old state and 'semi'-deep copy the list of audio devices
        AudioDeviceState oldAudioDeviceState = new AudioDeviceState(new ArrayList<>(mutableAudioDevices), selectedDevice);
        // update audio device list and selected device
        addAvailableAudioDevices(bluetoothHeadsetName);

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

    private void addAvailableAudioDevices(String bluetoothHeadsetName) {
        mutableAudioDevices.clear();
        for (Class<? extends AudioDevice> audioDevice : preferredDeviceList) {
            if (audioDevice.equals(BluetoothHeadset.class)) {
                BluetoothHeadset bluetoothHeadset = bluetoothHeadsetManager != null
                    ? bluetoothHeadsetManager.getHeadset(bluetoothHeadsetName)
                    : null;
                if (bluetoothHeadset != null) {
                    mutableAudioDevices.add(bluetoothHeadset);
                }
            } else if (audioDevice.equals(WiredHeadset.class)) {
                if (wiredHeadsetAvailable) {
                    mutableAudioDevices.add(new WiredHeadset());
                }
            } else if (audioDevice.equals(Earpiece.class)) {
                if (audioDeviceManager.hasEarpiece() && !wiredHeadsetAvailable) {
                    mutableAudioDevices.add(new Earpiece());
                }
            } else if (audioDevice.equals(Speakerphone.class)) {
                if (audioDeviceManager.hasSpeakerphone()) {
                    mutableAudioDevices.add(new Speakerphone());
                }
            }
        }

        logger.d(TAG, "Available AudioDevice list updated: " + availableAudioDevices);
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
        if (bluetoothHeadsetManager != null) {
            bluetoothHeadsetManager.stop();
        }
        if (wiredHeadsetReceiver != null) {
            wiredHeadsetReceiver.stop();
        }
        audioDeviceChangeListener = null;
        state = State.STOPPED;
    }

    public static final List<Class<? extends AudioDevice>> defaultPreferredDeviceList = Arrays.asList(
        BluetoothHeadset.class,
        WiredHeadset.class,
        Earpiece.class,
        Speakerphone.class
    );
}
