var capacitorAudioToggle = (function (exports, core) {
    'use strict';

    exports.AUDIO_MODE = void 0;
    (function (AUDIO_MODE) {
        AUDIO_MODE["EARPIECE"] = "EARPIECE";
        AUDIO_MODE["SPEAKER"] = "SPEAKER";
        AUDIO_MODE["NORMAL"] = "NORMAL";
        AUDIO_MODE["RINGTONE"] = "RINGTONE";
        AUDIO_MODE["BLUETOOTH"] = "BLUETOOTH";
    })(exports.AUDIO_MODE || (exports.AUDIO_MODE = {}));

    const AudioToggle = core.registerPlugin('AudioToggle', {
        web: () => Promise.resolve().then(function () { return web; }).then(m => new m.AudioToggleWeb()),
    });

    class AudioToggleWeb extends core.WebPlugin {
        async setMode() {
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

    Object.defineProperty(exports, '__esModule', { value: true });

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map
