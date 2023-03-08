import { registerPlugin } from '@capacitor/core';
const AudioToggle = registerPlugin('AudioToggle', {
    web: () => import('./web').then(m => new m.AudioToggleWeb()),
});
export * from './definitions';
export { AudioToggle };
//# sourceMappingURL=index.js.map