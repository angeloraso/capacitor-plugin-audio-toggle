import type { PluginListenerHandle } from "@capacitor/core";
export declare type DeviceName = 'earpiece' | 'speakerphone' | 'wired' | 'bluetooth';
export interface AudioTogglePlugin {
    enable(): Promise<void>;
    disable(): Promise<void>;
    reset(): Promise<void>;
    selectDevice(data: {
        device: DeviceName;
    }): Promise<void>;
    getAvailableDevices(): Promise<{
        earpiece: boolean;
        speakerphone: boolean;
        wired: boolean;
        bluetooth: boolean;
    }>;
    getSelectedDevice(): Promise<{
        selectedDevice: DeviceName;
    }>;
    checkPermissions(): Promise<{
        granted: boolean;
    }>;
    requestPermissions(): Promise<{
        granted: boolean;
    }>;
    openBluetoothSettings(): Promise<void>;
    addListener(eventName: 'onChanges', listenerFunc: (data: {
        earpiece: boolean;
        speakerphone: boolean;
        wired: boolean;
        bluetooth: boolean;
        selectedDevice: DeviceName;
    }) => void): Promise<PluginListenerHandle> & PluginListenerHandle;
    removeAllListeners(): Promise<void>;
}
