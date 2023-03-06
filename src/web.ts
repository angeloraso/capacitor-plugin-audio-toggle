import { WebPlugin } from '@capacitor/core';

import type { AudioTogglePlugin } from './definitions';

export class AudioToggleWeb extends WebPlugin implements AudioTogglePlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
