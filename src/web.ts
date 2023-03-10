import { WebPlugin } from '@capacitor/core';

import type { AudioTogglePlugin, DeviceName } from './definitions';

export class AudioToggleWeb extends WebPlugin implements AudioTogglePlugin {
  async selectDevice(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  async enable(): Promise<void>{
    throw this.unimplemented('Not implemented on web.');
  }

  async disable(): Promise<void>{
    throw this.unimplemented('Not implemented on web.');
  }

  async activate(): Promise<void>{
    throw this.unimplemented('Not implemented on web.');
  }

  async deactivate(): Promise<void>{
    throw this.unimplemented('Not implemented on web.');
  }

  async getAvailableDevices(): Promise<{available: DeviceName[]}>{
    throw this.unimplemented('Not implemented on web.');
  }
  
  async getSelectedDevice(): Promise<{selected: DeviceName}>{
    throw this.unimplemented('Not implemented on web.');
  }
}
