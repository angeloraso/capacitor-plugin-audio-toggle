package com.angeloraso.plugins.audiotoggle;

import android.content.Context;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.util.List;

@CapacitorPlugin(name = "AudioToggle")
public class AudioTogglePlugin extends Plugin {

    private AudioToggle audioToggle;

    public void load() {
        Context context = getContext();
        audioToggle = new AudioToggle(context, true);
    }

    @PluginMethod
    public void start(PluginCall call) {
        if (getActivity().isFinishing()) {
            call.reject("Audio toggle plugin error: App is finishing");
            return;
        }

        audioToggle.start(
            (audioDevices, audioDevice) -> {
                JSObject res = new JSObject();
                res.put("device", audioDevice);
                call.resolve(res);
            }
        );
    }

    @PluginMethod
    public void stop(PluginCall call) {
        if (getActivity().isFinishing()) {
            call.reject("Audio toggle plugin error: App is finishing");
            return;
        }

        audioToggle.stop();
        call.resolve();
    }

    @PluginMethod
    public void getDevices(PluginCall call) {
        if (getActivity().isFinishing()) {
            call.reject("Audio toggle plugin error: App is finishing");
            return;
        }

        JSObject res = new JSObject();
        List<AudioDevice> devices = audioToggle.availableAudioDevices;
        res.put("devices", devices);
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
        res.put("device", device);
        call.resolve(res);
    }

    @PluginMethod
    public void setMode(PluginCall call) {
        if (getActivity().isFinishing()) {
            call.reject("Audio toggle plugin error: App is finishing");
            return;
        }

        String mode = call.getString("mode");

        if (mode != null) {
            audioToggle.setMode(mode);
            call.resolve();
        } else {
            call.reject("Audio toggle plugin error: Audio mode is required");
        }
    }

    /**
     * Called when the activity will be destroyed.
     */
    @Override
    public void handleOnDestroy() {
        audioToggle.stop();
    }
}
