import { WebPlugin } from '@capacitor/core';
import type { AudioTogglePlugin, DeviceName } from './definitions';
export declare class AudioToggleWeb extends WebPlugin implements AudioTogglePlugin {
    selectDevice(): Promise<void>;
    enable(): Promise<void>;
    disable(): Promise<void>;
    activate(): Promise<void>;
    deactivate(): Promise<void>;
    getAvailableDevices(): Promise<{
        earpiece: boolean;
        speakerphone: boolean;
        wired: boolean;
        bluetooth: boolean;
    }>;
    getSelectedDevice(): Promise<{
        selectedDevice: DeviceName;
    }>;
}
