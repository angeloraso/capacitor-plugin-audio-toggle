import { WebPlugin } from '@capacitor/core';

import type { AudioTogglePlugin, DeviceName } from './definitions';

export class AudioToggleWeb extends WebPlugin implements AudioTogglePlugin {
  async selectDevice(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  async setRingtoneMode(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  async enable(): Promise<void>{
    throw this.unimplemented('Not implemented on web.');
  }

  async disable(): Promise<void>{
    throw this.unimplemented('Not implemented on web.');
  }

  async reset(): Promise<void>{
    throw this.unimplemented('Not implemented on web.');
  }

  async getAvailableDevices(): Promise<{earpiece: boolean, speakerphone: boolean, wired: boolean, bluetooth: boolean}>{
    throw this.unimplemented('Not implemented on web.');
  }
  
  async getSelectedDevice(): Promise<{selectedDevice: DeviceName}>{
    throw this.unimplemented('Not implemented on web.');
  }

  async checkPermissions(): Promise<{granted: boolean}> {
    throw this.unimplemented('Not implemented on web.');
  }

  async requestPermissions(): Promise<{granted: boolean}> {
    throw this.unimplemented('Not implemented on web.');
  }

  async isBluetoothEnabled(): Promise<{enabled: boolean}> {
    throw this.unimplemented('Not implemented on web.');
  }

  async openBluetoothSettings(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  async openAppSettings(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }
}
