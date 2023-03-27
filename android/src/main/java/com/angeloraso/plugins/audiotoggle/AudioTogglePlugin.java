package com.angeloraso.plugins.audiotoggle;

import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
import static android.provider.Settings.ACTION_BLUETOOTH_SETTINGS;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import java.util.List;

@CapacitorPlugin(
    name = "AudioToggle",
    permissions = {
        @Permission(alias = "BLUETOOTH", strings = { Manifest.permission.BLUETOOTH }),
        @Permission(alias = "BLUETOOTH_CONNECT", strings = { Manifest.permission.BLUETOOTH_CONNECT })
    }
)
public class AudioTogglePlugin extends Plugin {

    private AudioToggle audioToggle;

    public void load() {
        Context context = getContext();
        audioToggle = new AudioToggle(context, true);
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void enable(PluginCall call) {
        if (getActivity().isFinishing()) {
            call.reject(getActivity().getString(R.string.app_finishing));
            return;
        }

        audioToggle.setAudioToggleEventListener(this::onAudioToggleEvent);
        if (Build.VERSION.SDK_INT >= 31 && !bluetoothPermissionIsGranted()) {
            requestPermissions(call);
        }
        audioToggle.start();
        call.resolve();
    }

    private void onAudioToggleEvent(List<AudioDevice> audioDevices, AudioDevice audioDevice) {
        JSObject res = new JSObject();

        res.put("earpiece", false);
        res.put("speakerphone", false);
        res.put("wired", false);
        res.put("bluetooth", false);

        for (AudioDevice device : audioDevices) {
            res.put(device.getName(), true);
        }

        res.put("selectedDevice", audioDevice.getName());

        bridge.triggerWindowJSEvent("onChanges");
        notifyListeners("onChanges", res);
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void disable(PluginCall call) {
        if (getActivity().isFinishing()) {
            call.reject(getActivity().getString(R.string.app_finishing));
            return;
        }

        audioToggle.stop();
        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void reset(PluginCall call) {
        if (getActivity().isFinishing()) {
            call.reject(getActivity().getString(R.string.app_finishing));
            return;
        }

        audioToggle.deactivate();
        call.resolve();
    }

    @PluginMethod
    public void getAvailableDevices(PluginCall call) {
        if (getActivity().isFinishing()) {
            call.reject(getActivity().getString(R.string.app_finishing));
            return;
        }

        JSObject res = new JSObject();

        res.put("earpiece", false);
        res.put("speakerphone", false);
        res.put("wired", false);
        res.put("bluetooth", false);

        for (AudioDevice device : audioToggle.availableAudioDevices) {
            res.put(device.getName(), true);
        }

        call.resolve(res);
    }

    @PluginMethod
    public void getSelectedDevice(PluginCall call) {
        if (getActivity().isFinishing()) {
            call.reject(getActivity().getString(R.string.app_finishing));
            return;
        }

        JSObject res = new JSObject();
        AudioDevice device = audioToggle.selectedAudioDevice;
        res.put("selectedDevice", device.getName());
        call.resolve(res);
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void setRingtoneMode(PluginCall call) {
        if (getActivity().isFinishing()) {
            call.reject(getActivity().getString(R.string.app_finishing));
            return;
        }

        audioToggle.setRingtoneMode();
        call.resolve();
    }

    @PluginMethod
    public void selectDevice(PluginCall call) {
        if (getActivity().isFinishing()) {
            call.reject(getActivity().getString(R.string.app_finishing));
            return;
        }

        String device = call.getString("device");

        if (device != null) {
            audioToggle.selectDevice(device);
            call.resolve();
        } else {
            call.reject("Audio toggle plugin error: Device is required");
        }
    }

    @Override
    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void removeAllListeners(PluginCall call) {
        super.removeAllListeners(call);
        audioToggle.deactivate();
        unsetAppListeners();
        call.resolve();
    }

    @PluginMethod
    public void checkPermissions(PluginCall call) {
        if (getActivity().isFinishing()) {
            String appFinishingMsg = getActivity().getString(R.string.app_finishing);
            call.reject(appFinishingMsg);
            return;
        }

        JSObject res = new JSObject();
        res.put("granted", bluetoothPermissionIsGranted());
        call.resolve(res);
    }

    @PluginMethod
    public void requestPermissions(PluginCall call) {
        if (getActivity().isFinishing()) {
            String appFinishingMsg = getActivity().getString(R.string.app_finishing);
            call.reject(appFinishingMsg);
            return;
        }

        if (bluetoothPermissionIsGranted()) {
            audioToggle.startBluetoothListener();
            JSObject res = new JSObject();
            res.put("granted", true);
            call.resolve(res);
        } else {
            if (Build.VERSION.SDK_INT >= 31) {
                requestPermissionForAlias(getActivity().getString(R.string.bluetooth_connect_alias), call, "bluetoothPermissionCallback");
            } else {
                requestPermissionForAlias(getActivity().getString(R.string.bluetooth_alias), call, "bluetoothPermissionCallback");
            }
        }
    }

    @PermissionCallback
    private void bluetoothPermissionCallback(PluginCall call) {
        JSObject res = new JSObject();

        if (bluetoothPermissionIsGranted()) {
            audioToggle.startBluetoothListener();
            res.put("granted", true);
            call.resolve(res);
        } else {
            res.put("granted", false);
            call.resolve(res);
        }
    }

    @PluginMethod
    public void isBluetoothEnabled(PluginCall call) {
        boolean enabled = audioToggle.isBluetoothEnabled();
        JSObject res = new JSObject();
        res.put("enabled", enabled);
        call.resolve(res);
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void openBluetoothSettings(PluginCall call) {
        Intent intent = new Intent(ACTION_BLUETOOTH_SETTINGS);
        getActivity().startActivity(intent);
        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void openAppSettings(PluginCall call) {
        Intent intent = new Intent(ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
        getActivity().startActivity(intent);
        call.resolve();
    }

    private boolean bluetoothPermissionIsGranted() {
        if (Build.VERSION.SDK_INT >= 31) {
            return getPermissionState(getActivity().getString(R.string.bluetooth_connect_alias)) == PermissionState.GRANTED;
        } else {
            return getPermissionState(getActivity().getString(R.string.bluetooth_alias)) == PermissionState.GRANTED;
        }
    }

    private void unsetAppListeners() {
        bridge.getApp().setStatusChangeListener(null);
        bridge.getApp().setAppRestoredListener(null);
    }
}
