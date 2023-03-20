package com.angeloraso.plugins.audiotoggle.bluetooth;

import static android.bluetooth.BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED;
import static android.bluetooth.BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED;
import static android.bluetooth.BluetoothHeadset.STATE_AUDIO_CONNECTED;
import static android.bluetooth.BluetoothHeadset.STATE_AUDIO_DISCONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import com.angeloraso.plugins.audiotoggle.AudioDevice;
import com.angeloraso.plugins.audiotoggle.AudioDeviceManager;
import com.angeloraso.plugins.audiotoggle.android.BluetoothDeviceWrapper;
import com.angeloraso.plugins.audiotoggle.android.BluetoothIntentProcessor;
import com.angeloraso.plugins.audiotoggle.android.BluetoothIntentProcessorImpl;
import com.angeloraso.plugins.audiotoggle.android.BluetoothPermissionCheckStrategy;
import com.angeloraso.plugins.audiotoggle.android.Logger;
import com.angeloraso.plugins.audiotoggle.android.PermissionsCheckStrategy;
import com.angeloraso.plugins.audiotoggle.android.SystemClockWrapper;
import java.util.List;

public class BluetoothHeadsetManager extends BroadcastReceiver implements BluetoothProfile.ServiceListener {

    private final Context context;
    private final Logger logger;
    private BluetoothAdapter bluetoothAdapter;
    private AudioDeviceManager audioDeviceManager;
    private BluetoothHeadsetConnectionListener headsetListener;
    private Handler bluetoothScoHandler;
    private SystemClockWrapper systemClockWrapper;
    private BluetoothIntentProcessor bluetoothIntentProcessor;
    private BluetoothHeadset headsetProxy;
    private PermissionsCheckStrategy permissionsRequestStrategy;
    private boolean hasRegisteredReceivers;
    private HeadsetState headsetState = HeadsetState.Disconnected;
    private EnableBluetoothScoJob enableBluetoothScoJob;
    private DisableBluetoothScoJob disableBluetoothScoJob;

    private static final String TAG = "BluetoothHeadsetManager";
    private static final String PERMISSION_ERROR_MESSAGE = "Bluetooth unsupported, permissions not granted";

    public BluetoothHeadsetManager(
        Context context,
        Logger logger,
        BluetoothAdapter bluetoothAdapter,
        AudioDeviceManager audioDeviceManager
    ) {
        this(context, logger, bluetoothAdapter, audioDeviceManager, null);
    }

    public BluetoothHeadsetManager(
        Context context,
        Logger logger,
        BluetoothAdapter bluetoothAdapter,
        AudioDeviceManager audioDeviceManager,
        BluetoothHeadsetConnectionListener headsetListener
    ) {
        this(context, logger, bluetoothAdapter, audioDeviceManager, headsetListener, new Handler(Looper.getMainLooper()));
    }

    public BluetoothHeadsetManager(
        Context context,
        Logger logger,
        BluetoothAdapter bluetoothAdapter,
        AudioDeviceManager audioDeviceManager,
        BluetoothHeadsetConnectionListener headsetListener,
        Handler bluetoothScoHandler
    ) {
        this(context, logger, bluetoothAdapter, audioDeviceManager, headsetListener, bluetoothScoHandler, new SystemClockWrapper());
    }

    public BluetoothHeadsetManager(
        Context context,
        Logger logger,
        BluetoothAdapter bluetoothAdapter,
        AudioDeviceManager audioDeviceManager,
        BluetoothHeadsetConnectionListener headsetListener,
        Handler bluetoothScoHandler,
        SystemClockWrapper systemClockWrapper
    ) {
        this(
            context,
            logger,
            bluetoothAdapter,
            audioDeviceManager,
            headsetListener,
            bluetoothScoHandler,
            systemClockWrapper,
            new BluetoothIntentProcessorImpl()
        );
    }

    public BluetoothHeadsetManager(
        Context context,
        Logger logger,
        BluetoothAdapter bluetoothAdapter,
        AudioDeviceManager audioDeviceManager,
        BluetoothHeadsetConnectionListener headsetListener,
        Handler bluetoothScoHandler,
        SystemClockWrapper systemClockWrapper,
        BluetoothIntentProcessor bluetoothIntentProcessor
    ) {
        this(
            context,
            logger,
            bluetoothAdapter,
            audioDeviceManager,
            headsetListener,
            bluetoothScoHandler,
            systemClockWrapper,
            bluetoothIntentProcessor,
            null
        );
    }

    public BluetoothHeadsetManager(
        Context context,
        Logger logger,
        BluetoothAdapter bluetoothAdapter,
        AudioDeviceManager audioDeviceManager,
        BluetoothHeadsetConnectionListener headsetListener,
        Handler bluetoothScoHandler,
        SystemClockWrapper systemClockWrapper,
        BluetoothIntentProcessor bluetoothIntentProcessor,
        BluetoothHeadset headsetProxy
    ) {
        this(
            context,
            logger,
            bluetoothAdapter,
            audioDeviceManager,
            headsetListener,
            bluetoothScoHandler,
            systemClockWrapper,
            bluetoothIntentProcessor,
            headsetProxy,
            new BluetoothPermissionCheckStrategy(context)
        );
    }

    public BluetoothHeadsetManager(
        Context context,
        Logger logger,
        BluetoothAdapter bluetoothAdapter,
        AudioDeviceManager audioDeviceManager,
        BluetoothHeadsetConnectionListener headsetListener,
        Handler bluetoothScoHandler,
        SystemClockWrapper systemClockWrapper,
        BluetoothIntentProcessor bluetoothIntentProcessor,
        BluetoothHeadset headsetProxy,
        PermissionsCheckStrategy permissionsRequestStrategy
    ) {
        this(
            context,
            logger,
            bluetoothAdapter,
            audioDeviceManager,
            headsetListener,
            bluetoothScoHandler,
            systemClockWrapper,
            bluetoothIntentProcessor,
            headsetProxy,
            permissionsRequestStrategy,
            false
        );
    }

    public BluetoothHeadsetManager(
        Context context,
        Logger logger,
        BluetoothAdapter bluetoothAdapter,
        AudioDeviceManager audioDeviceManager,
        BluetoothHeadsetConnectionListener headsetListener,
        Handler bluetoothScoHandler,
        SystemClockWrapper systemClockWrapper,
        BluetoothIntentProcessor bluetoothIntentProcessor,
        BluetoothHeadset headsetProxy,
        PermissionsCheckStrategy permissionsRequestStrategy,
        Boolean hasRegisteredReceivers
    ) {
        this.context = context;
        this.logger = logger;
        this.bluetoothAdapter = bluetoothAdapter;
        this.audioDeviceManager = audioDeviceManager;
        this.headsetListener = headsetListener;
        this.bluetoothScoHandler = bluetoothScoHandler;
        this.systemClockWrapper = systemClockWrapper;
        this.bluetoothIntentProcessor = bluetoothIntentProcessor;
        this.headsetProxy = headsetProxy;
        this.permissionsRequestStrategy = permissionsRequestStrategy;
        this.hasRegisteredReceivers = hasRegisteredReceivers;

        this.enableBluetoothScoJob =
            new EnableBluetoothScoJob(this.logger, this.audioDeviceManager, this.bluetoothScoHandler, this.systemClockWrapper);
        this.disableBluetoothScoJob =
            new DisableBluetoothScoJob(this.logger, this.audioDeviceManager, this.bluetoothScoHandler, this.systemClockWrapper);
    }

    public void setHeadsetState(HeadsetState value) {
        if (headsetState != value) {
            headsetState = value;
            this.logger.d(TAG, "Headset state changed to " + headsetState.getClass().getSimpleName());
            if (value == HeadsetState.Disconnected) {
                enableBluetoothScoJob.cancelBluetoothScoJob();
            }
        }
    }

    public static BluetoothHeadsetManager newInstance(
        Context context,
        Logger logger,
        BluetoothAdapter bluetoothAdapter,
        AudioDeviceManager audioDeviceManager
    ) {
        if (bluetoothAdapter != null) {
            return new BluetoothHeadsetManager(context, logger, bluetoothAdapter, audioDeviceManager);
        } else {
            logger.d(TAG, "Bluetooth is not supported on this device");
            return null;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onServiceConnected(int profile, BluetoothProfile bluetoothProfile) {
        headsetProxy = (BluetoothHeadset) bluetoothProfile;
        if (hasPermissions()) {
            for (BluetoothDevice device : bluetoothProfile.getConnectedDevices()) {
                logger.d(TAG, "Bluetooth " + device.getName() + " connected");
            }
        }
        if (hasConnectedDevice()) {
            connect();
            if (headsetListener != null) {
                headsetListener.onBluetoothHeadsetStateChanged(getHeadsetName());
            }
        }
    }

    @Override
    public void onServiceDisconnected(int profile) {
        logger.d(TAG, "Bluetooth disconnected");
        setHeadsetState(HeadsetState.Disconnected);
        if (headsetListener != null) {
            headsetListener.onBluetoothHeadsetStateChanged(null);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isCorrectIntentAction(intent.getAction())) {
            BluetoothDeviceWrapper bluetoothDevice = getHeadsetDevice(intent);
            if (bluetoothDevice != null) {
                int state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, STATE_DISCONNECTED);
                if (!hasPermissions()) {
                    return;
                }

                switch (state) {
                    case STATE_CONNECTED:
                        this.logger.d(TAG, "Bluetooth headset " + bluetoothDevice.getName() + " connected");
                        connect();
                        if (headsetListener != null) {
                            headsetListener.onBluetoothHeadsetStateChanged(bluetoothDevice.getName());
                        }
                        break;
                    case STATE_DISCONNECTED:
                        this.logger.d(TAG, "Bluetooth headset " + bluetoothDevice.getName() + " disconnected");
                        disconnect();
                        if (headsetListener != null) {
                            headsetListener.onBluetoothHeadsetStateChanged(null);
                        }
                        break;
                    case STATE_AUDIO_CONNECTED:
                        this.logger.d(TAG, "Bluetooth audio connected on device " + bluetoothDevice.getName());
                        enableBluetoothScoJob.cancelBluetoothScoJob();
                        setHeadsetState(HeadsetState.AudioActivated);
                        if (headsetListener != null) {
                            headsetListener.onBluetoothHeadsetStateChanged(bluetoothDevice.getName());
                        }
                        break;
                    case STATE_AUDIO_DISCONNECTED:
                        this.logger.d(TAG, "Bluetooth audio disconnected on device " + bluetoothDevice.getName());
                        disableBluetoothScoJob.cancelBluetoothScoJob();
                        /*
                         * This block is needed to restart bluetooth SCO in the event that
                         * the active bluetooth headset has changed.
                         */
                        if (hasActiveHeadsetChanged()) {
                            enableBluetoothScoJob.executeBluetoothScoJob();
                        }

                        if (headsetListener != null) {
                            headsetListener.onBluetoothHeadsetStateChanged(null);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void start(BluetoothHeadsetConnectionListener headsetListener) {
        if (hasPermissions()) {
            if (!hasRegisteredReceivers) {
                this.headsetListener = headsetListener;
                bluetoothAdapter.getProfileProxy(context, this, BluetoothProfile.HEADSET);
                context.registerReceiver(this, new IntentFilter(ACTION_CONNECTION_STATE_CHANGED));
                context.registerReceiver(this, new IntentFilter(ACTION_AUDIO_STATE_CHANGED));
                hasRegisteredReceivers = true;
            }
        } else {
            logger.w(TAG, PERMISSION_ERROR_MESSAGE);
        }
    }

    public void stop() {
        if (hasPermissions()) {
            headsetListener = null;
            bluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, headsetProxy);
            if (hasRegisteredReceivers) {
                context.unregisterReceiver(this);
                hasRegisteredReceivers = false;
            }
        } else {
            logger.w(TAG, PERMISSION_ERROR_MESSAGE);
        }
    }

    public void activate() {
        if (hasPermissions()) {
            if (headsetState == HeadsetState.Connected || headsetState == HeadsetState.AudioActivationError) {
                enableBluetoothScoJob.executeBluetoothScoJob();
            } else {
                logger.w(TAG, "Cannot activate when in the " + headsetState.getClass().getSimpleName() + " state");
            }
        } else {
            logger.w(TAG, PERMISSION_ERROR_MESSAGE);
        }
    }

    public void deactivate() {
        if (headsetState == HeadsetState.AudioActivated) {
            disableBluetoothScoJob.executeBluetoothScoJob();
        } else {
            logger.w(TAG, "Cannot deactivate when in the " + headsetState.getClass().getSimpleName() + " state");
        }
    }

    public boolean hasActivationError() {
        if (hasPermissions()) {
            return headsetState == HeadsetState.AudioActivationError;
        } else {
            logger.w(TAG, PERMISSION_ERROR_MESSAGE);
            return false;
        }
    }

    public AudioDevice.BluetoothHeadset getHeadset() {
        if (hasPermissions()) {
            if (headsetState != HeadsetState.Disconnected) {
                return new AudioDevice.BluetoothHeadset();
            } else {
                return null;
            }
        } else {
            logger.w(TAG, PERMISSION_ERROR_MESSAGE);
            return null;
        }
    }

    public boolean isEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    private boolean isCorrectIntentAction(String intentAction) {
        return intentAction.equals(ACTION_CONNECTION_STATE_CHANGED) || intentAction.equals(ACTION_AUDIO_STATE_CHANGED);
    }

    private void connect() {
        if (!hasActiveHeadset()) {
            setHeadsetState(HeadsetState.Connected);
        }
    }

    private void disconnect() {
        if (hasActiveHeadset()) {
            setHeadsetState(HeadsetState.AudioActivated);
        } else if (hasConnectedDevice()) {
            setHeadsetState(HeadsetState.Connected);
        } else {
            setHeadsetState(HeadsetState.Disconnected);
        }
    }

    private boolean hasActiveHeadsetChanged() {
        return headsetState == HeadsetState.AudioActivated && hasConnectedDevice() && !hasActiveHeadset();
    }

    @SuppressLint("MissingPermission")
    private String getHeadsetName() {
        if (headsetProxy != null) {
            if (!hasPermissions()) {
                return null;
            }

            List<BluetoothDevice> devices = headsetProxy.getConnectedDevices();
            if (devices != null) {
                if (devices.size() > 1 && hasActiveHeadset()) {
                    for (BluetoothDevice device : devices) {
                        if (headsetProxy.isAudioConnected(device)) {
                            logger.d(TAG, "Device size > 1 with device name: " + device.getName());
                            return device.getName();
                        }
                    }
                } else if (devices.size() == 1) {
                    BluetoothDevice device = devices.get(0);
                    logger.d(TAG, "Device size 1 with device name: " + device.getName());
                    return device.getName();
                } else {
                    logger.d(TAG, "Device size 0");
                }
            }
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    private boolean hasActiveHeadset() {
        if (headsetProxy != null) {
            if (!hasPermissions()) {
                return false;
            }

            List<BluetoothDevice> devices = headsetProxy.getConnectedDevices();
            if (devices != null) {
                for (BluetoothDevice device : devices) {
                    if (headsetProxy.isAudioConnected(device)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    private boolean hasConnectedDevice() {
        if (headsetProxy != null) {
            if (!hasPermissions()) {
                return false;
            }

            List<BluetoothDevice> devices = headsetProxy.getConnectedDevices();
            if (devices != null) {
                return !devices.isEmpty();
            }
        }
        return false;
    }

    private BluetoothDeviceWrapper getHeadsetDevice(Intent intent) {
        BluetoothDeviceWrapper deviceWrapper = null;
        if (hasPermissions()) {
            deviceWrapper = bluetoothIntentProcessor.getBluetoothDevice(intent);
        }

        if (deviceWrapper != null) {
            if (isHeadsetDevice(deviceWrapper)) {
                return deviceWrapper;
            }
        }
        return null;
    }

    private boolean isHeadsetDevice(BluetoothDeviceWrapper deviceWrapper) {
        Integer deviceClass = deviceWrapper.getDeviceClass();
        if (deviceClass != null) {
            return (
                deviceClass == BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE ||
                deviceClass == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET ||
                deviceClass == BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO ||
                deviceClass == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES ||
                deviceClass == BluetoothClass.Device.Major.UNCATEGORIZED
            );
        }
        return false;
    }

    private boolean hasPermissions() {
        return permissionsRequestStrategy.hasPermissions();
    }

    private abstract static class HeadsetState {

        public static final HeadsetState Disconnected = new Disconnected();
        public static final HeadsetState Connected = new Connected();
        public static final HeadsetState AudioActivating = new AudioActivating();
        public static final HeadsetState AudioActivationError = new AudioActivationError();
        public static final HeadsetState AudioActivated = new AudioActivated();

        private HeadsetState() {}

        private static final class Disconnected extends HeadsetState {}

        private static final class Connected extends HeadsetState {}

        private static final class AudioActivating extends HeadsetState {}

        private static final class AudioActivationError extends HeadsetState {}

        private static final class AudioActivated extends HeadsetState {}
    }

    private class EnableBluetoothScoJob extends BluetoothScoJob {

        AudioDeviceManager audioDeviceManager;

        public EnableBluetoothScoJob(
            final Logger logger,
            final AudioDeviceManager audioDeviceManager,
            final Handler bluetoothScoHandler,
            final SystemClockWrapper systemClockWrapper
        ) {
            super(logger, bluetoothScoHandler, systemClockWrapper);
            this.audioDeviceManager = audioDeviceManager;
        }

        @Override
        protected void scoAction() {
            logger.d(TAG, "Attempting to enable bluetooth SCO");
            audioDeviceManager.enableBluetoothSco(true);
            setHeadsetState(HeadsetState.AudioActivating);
        }

        @Override
        protected void scoTimeOutAction() {
            setHeadsetState(HeadsetState.AudioActivationError);
            if (headsetListener != null) {
                headsetListener.onBluetoothHeadsetActivationError();
            }
        }
    }

    private class DisableBluetoothScoJob extends BluetoothScoJob {

        AudioDeviceManager audioDeviceManager;

        public DisableBluetoothScoJob(
            final Logger logger,
            final AudioDeviceManager audioDeviceManager,
            final Handler bluetoothScoHandler,
            final SystemClockWrapper systemClockWrapper
        ) {
            super(logger, bluetoothScoHandler, systemClockWrapper);
            this.audioDeviceManager = audioDeviceManager;
        }

        @Override
        protected void scoAction() {
            logger.d(TAG, "Attempting to disable bluetooth SCO");
            audioDeviceManager.enableBluetoothSco(false);
            setHeadsetState(HeadsetState.Connected);
        }

        @Override
        protected void scoTimeOutAction() {
            setHeadsetState(HeadsetState.AudioActivationError);
        }
    }
}
