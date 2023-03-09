import { WebPlugin } from '@capacitor/core';
export class AudioToggleWeb extends WebPlugin {
    async selectDevice() {
        throw this.unimplemented('Not implemented on web.');
    }
    async start() {
        throw this.unimplemented('Not implemented on web.');
    }
    async stop() {
        throw this.unimplemented('Not implemented on web.');
    }
    async getDevices() {
        throw this.unimplemented('Not implemented on web.');
    }
    async getSelectedDevice() {
        throw this.unimplemented('Not implemented on web.');
    }
}
//# sourceMappingURL=web.js.map