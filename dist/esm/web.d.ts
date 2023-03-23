import { WebPlugin } from '@capacitor/core';
import type { AudioTogglePlugin, DeviceName } from './definitions';
export declare class AudioToggleWeb extends WebPlugin implements AudioTogglePlugin {
    selectDevice(): Promise<void>;
    setRingtoneMode(): Promise<void>;
    enable(): Promise<void>;
    disable(): Promise<void>;
    reset(): Promise<void>;
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
    isBluetoothEnabled(): Promise<{
        enabled: boolean;
    }>;
    openBluetoothSettings(): Promise<void>;
    openAppSettings(): Promise<void>;
}
