import { registerPlugin } from '@capacitor/core';

import type { AudioTogglePlugin } from './definitions';

const AudioToggle = registerPlugin<AudioTogglePlugin>('AudioToggle', {
  web: () => import('./web').then(m => new m.AudioToggleWeb()),
});

export * from './definitions';
export { AudioToggle };
