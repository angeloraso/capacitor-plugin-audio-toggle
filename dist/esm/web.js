import { WebPlugin } from '@capacitor/core';
export class AudioToggleWeb extends WebPlugin {
    async selectDevice() {
        throw this.unimplemented('Not implemented on web.');
    }
    async enable() {
        throw this.unimplemented('Not implemented on web.');
    }
    async disable() {
        throw this.unimplemented('Not implemented on web.');
    }
    async activate() {
        throw this.unimplemented('Not implemented on web.');
    }
    async deactivate() {
        throw this.unimplemented('Not implemented on web.');
    }
    async getAvailableDevices() {
        throw this.unimplemented('Not implemented on web.');
    }
    async getSelectedDevice() {
        throw this.unimplemented('Not implemented on web.');
    }
}
//# sourceMappingURL=web.js.map