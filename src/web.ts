import { WebPlugin } from '@capacitor/core';

import type { AudioTogglePlugin, DeviceName } from './definitions';

export class AudioToggleWeb extends WebPlugin implements AudioTogglePlugin {
  async selectDevice(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  async start(): Promise<{availableDevices: DeviceName[], selectedDevice: DeviceName}>{
    throw this.unimplemented('Not implemented on web.');
  }

  async stop(): Promise<void>{
    throw this.unimplemented('Not implemented on web.');
  }

  async getDevices(): Promise<{availableDevices: DeviceName[]}>{
    throw this.unimplemented('Not implemented on web.');
  }
  
  async getSelectedDevice(): Promise<{selectedDevice: DeviceName}>{
    throw this.unimplemented('Not implemented on web.');
  }
}
