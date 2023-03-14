package com.angeloraso.plugins.audiotoggle;

import android.content.Context;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.util.List;
import java.util.stream.Collectors;

@CapacitorPlugin(name = "AudioToggle")
public class AudioTogglePlugin extends Plugin {

    private AudioToggle audioToggle;

    public void load() {
        Context context = getContext();
        audioToggle = new AudioToggle(context, true);
    }

    @PluginMethod
    public void enable(PluginCall call) {
        if (getActivity().isFinishing()) {
            call.reject("Audio toggle plugin error: App is finishing");
            return;
        }

        audioToggle.setAudioToggleEventListener(this::onAudioToggleEvent);
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
            call.reject("Audio toggle plugin error: App is finishing");
            return;
        }

        audioToggle.stop();
        call.resolve();
    }

    @PluginMethod
    public void activate(PluginCall call) {
        if (getActivity().isFinishing()) {
            call.reject("Audio toggle plugin error: App is finishing");
            return;
        }

        audioToggle.activate();
        call.resolve();
    }

    @PluginMethod
    public void deactivate(PluginCall call) {
        if (getActivity().isFinishing()) {
            call.reject("Audio toggle plugin error: App is finishing");
            return;
        }

        audioToggle.deactivate();
        call.resolve();
    }

    @PluginMethod
    public void getAvailableDevices(PluginCall call) {
        if (getActivity().isFinishing()) {
            call.reject("Audio toggle plugin error: App is finishing");
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
            call.reject("Audio toggle plugin error: App is finishing");
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
            call.reject("Audio toggle plugin error: App is finishing");
            return;
        }

        String device = call.getString("device");

        if (device != null) {
            audioToggle.selectDevice(device);
            call.resolve();
        } else {
            call.reject("Audio toggle plugin error: Audio mode is required");
        }
    }

    @Override
    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void removeAllListeners(PluginCall call) {
        super.removeAllListeners(call);
        unsetAppListeners();
    }

    private void unsetAppListeners() {
        bridge.getApp().setStatusChangeListener(null);
        bridge.getApp().setAppRestoredListener(null);
    }
}
