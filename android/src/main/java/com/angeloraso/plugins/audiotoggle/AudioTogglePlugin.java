package com.angeloraso.plugins.audiotoggle;

import android.Manifest;
import android.content.Context;
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
    permissions = { @Permission(alias = "audio_toggle_bluetooth", strings = { Manifest.permission.BLUETOOTH_CONNECT }) }
)
public class AudioTogglePlugin extends Plugin {

    private AudioToggle audioToggle;

    public void load() {
        Context context = getContext();
        audioToggle = new AudioToggle(context, true);
    }

    @PluginMethod
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

    @PluginMethod
    public void disable(PluginCall call) {
        if (getActivity().isFinishing()) {
            call.reject(getActivity().getString(R.string.app_finishing));
            return;
        }

        audioToggle.stop();
        call.resolve();
    }

    @PluginMethod
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
            JSObject res = new JSObject();
            res.put("granted", true);
            call.resolve(res);
        } else {
            requestPermissionForAlias(getActivity().getString(R.string.permission_alias), call, "bluetoothPermissionCallback");
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

    private boolean bluetoothPermissionIsGranted() {
        return getPermissionState(getActivity().getString(R.string.permission_alias)) == PermissionState.GRANTED;
    }

    private void unsetAppListeners() {
        bridge.getApp().setStatusChangeListener(null);
        bridge.getApp().setAppRestoredListener(null);
    }
}
