import type { PluginListenerHandle } from "@capacitor/core";
export declare enum DeviceName {
    Earpiece = "Earpiece",
    Speakerphone = "Speakerphone",
    WiredHeadset = "WiredHeadset",
    BluetoothHeadset = "BluetoothHeadset"
}
export interface AudioTogglePlugin {
    enable(): Promise<void>;
    disable(): Promise<void>;
    selectDevice(data: {
        device: DeviceName;
    }): Promise<void>;
    activate(): Promise<void>;
    deactivate(): Promise<void>;
    getAvailableDevices(): Promise<{
        available: DeviceName[];
    }>;
    getSelectedDevice(): Promise<{
        selected: DeviceName;
    }>;
    addListener(eventName: 'onChanges', listenerFunc: () => {
        available: DeviceName[];
        selected: DeviceName;
    }): Promise<PluginListenerHandle> & PluginListenerHandle;
}
