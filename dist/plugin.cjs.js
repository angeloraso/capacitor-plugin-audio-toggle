'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@capacitor/core');

const AudioToggle = core.registerPlugin('AudioToggle', {
    web: () => Promise.resolve().then(function () { return web; }).then(m => new m.AudioToggleWeb()),
});

class AudioToggleWeb extends core.WebPlugin {
    async selectDevice() {
        throw this.unimplemented('Not implemented on web.');
    }
    async enable() {
        throw this.unimplemented('Not implemented on web.');
    }
    async disable() {
        throw this.unimplemented('Not implemented on web.');
    }
    async getAvailableDevices() {
        throw this.unimplemented('Not implemented on web.');
    }
    async getSelectedDevice() {
        throw this.unimplemented('Not implemented on web.');
    }
}

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    AudioToggleWeb: AudioToggleWeb
});

exports.AudioToggle = AudioToggle;
//# sourceMappingURL=plugin.cjs.js.map
