'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@capacitor/core');

exports.DeviceName = void 0;
(function (DeviceName) {
    DeviceName["Earpiece"] = "Earpiece";
    DeviceName["Speakerphone"] = "Speakerphone";
    DeviceName["WiredHeadset"] = "WiredHeadset";
    DeviceName["BluetoothHeadset"] = "BluetoothHeadset";
})(exports.DeviceName || (exports.DeviceName = {}));

const AudioToggle = core.registerPlugin('AudioToggle', {
    web: () => Promise.resolve().then(function () { return web; }).then(m => new m.AudioToggleWeb()),
});

class AudioToggleWeb extends core.WebPlugin {
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

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    AudioToggleWeb: AudioToggleWeb
});

exports.AudioToggle = AudioToggle;
//# sourceMappingURL=plugin.cjs.js.map
