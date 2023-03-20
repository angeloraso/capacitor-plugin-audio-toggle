import type { PluginListenerHandle } from "@capacitor/core";

export type DeviceName = 'earpiece' | 'speakerphone' | 'wired' | 'bluetooth';

export interface AudioTogglePlugin {
  enable(): Promise<void>;
  disable(): Promise<void>;
  reset(): Promise<void>;
  selectDevice(data: {device: DeviceName}): Promise<void>;
  getAvailableDevices(): Promise<{earpiece: boolean, speakerphone: boolean, wired: boolean, bluetooth: boolean}>;
  getSelectedDevice(): Promise<{selectedDevice: DeviceName}>;
  checkPermissions(): Promise<PermissionStatus>;
  requestPermissions(): Promise<PermissionStatus>;

  addListener(
    eventName: 'onChanges',
    listenerFunc: (data: {earpiece: boolean, speakerphone: boolean, wired: boolean, bluetooth: boolean, selectedDevice: DeviceName}) => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;
  removeAllListeners(): Promise<void>;
}
