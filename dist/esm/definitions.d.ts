export declare enum AUDIO_MODE {
    EARPIECE = "EARPIECE",
    SPEAKER = "SPEAKER",
    NORMAL = "NORMAL",
    RINGTONE = "RINGTONE",
    BLUETOOTH = "BLUETOOTH"
}
export interface AudioTogglePlugin {
    setMode(data: {
        mode: AUDIO_MODE;
    }): Promise<void>;
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
