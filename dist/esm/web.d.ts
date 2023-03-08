import { WebPlugin } from '@capacitor/core';
import type { AudioTogglePlugin } from './definitions';
export declare class AudioToggleWeb extends WebPlugin implements AudioTogglePlugin {
    setMode(): Promise<void>;
    start(): Promise<{
        device: any;
    }>;
    stop(): Promise<void>;
    getDevices(): Promise<{
        devices: any[];
    }>;
    getSelectedDevice(): Promise<{
        device: any;
    }>;
}
