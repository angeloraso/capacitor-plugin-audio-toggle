import { WebPlugin } from '@capacitor/core';

import type { AudioTogglePlugin } from './definitions';

export class AudioToggleWeb extends WebPlugin implements AudioTogglePlugin {
  async setMode(): Promise<void>{
    throw this.unimplemented('Not implemented on web.');
  }

  async start(): Promise<{device: any}>{
    throw this.unimplemented('Not implemented on web.');
  }

  async stop(): Promise<void>{
    throw this.unimplemented('Not implemented on web.');
  }

  async getDevices(): Promise<{devices: any[]}>{
    throw this.unimplemented('Not implemented on web.');
  }
  
  async getSelectedDevice(): Promise<{device: any}>{
    throw this.unimplemented('Not implemented on web.');
  }
}
