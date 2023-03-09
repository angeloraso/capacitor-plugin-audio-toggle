import { WebPlugin } from '@capacitor/core';
import type { AudioTogglePlugin, DeviceName } from './definitions';
export declare class AudioToggleWeb extends WebPlugin implements AudioTogglePlugin {
    selectDevice(): Promise<void>;
    start(): Promise<{
        availableDevices: DeviceName[];
        selectedDevice: DeviceName;
    }>;
    stop(): Promise<void>;
    activate(): Promise<void>;
    deactivate(): Promise<void>;
    getDevices(): Promise<{
        availableDevices: DeviceName[];
    }>;
    getSelectedDevice(): Promise<{
        selectedDevice: DeviceName;
    }>;
}
