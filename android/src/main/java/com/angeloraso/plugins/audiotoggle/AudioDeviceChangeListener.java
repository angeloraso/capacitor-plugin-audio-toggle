package com.angeloraso.plugins.audiotoggle;

import java.util.List;

/**
 * Receives a list of the most recently available [AudioDevice]s. Also provides the
 * currently selected [AudioDevice] from [AudioToggle].
 * - audioDevices - The list of [AudioDevice]s or an empty list if none are available.
 * - selectedAudioDevice - The currently selected device or null if no device has been selected.
 */
public interface AudioDeviceChangeListener {
    void onAudioDevicesChanged(List<AudioDevice> audioDevices, AudioDevice selectedAudioDevice);
}
