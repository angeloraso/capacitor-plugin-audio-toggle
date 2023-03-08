package com.angeloraso.plugins.audiotoggle;

/**
 * This class represents a single audio device that has been retrieved by the [AudioToggle].
 */
public abstract class AudioDevice {

    /** The friendly name of the device.*/
    public abstract String getName();

    /** An [AudioDevice] representing a Bluetooth Headset.*/
    public static class BluetoothHeadset extends AudioDevice {

        private final String name;

        public BluetoothHeadset() {
            this.name = "Bluetooth";
        }

        public BluetoothHeadset(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    /** An [AudioDevice] representing a Wired Headset.*/
    public static class WiredHeadset extends AudioDevice {

        private final String name;

        public WiredHeadset() {
            this.name = "Wired Headset";
        }

        @Override
        public String getName() {
            return name;
        }
    }

    /** An [AudioDevice] representing the Earpiece.*/
    public static class Earpiece extends AudioDevice {

        private final String name;

        public Earpiece() {
            this.name = "Earpiece";
        }

        @Override
        public String getName() {
            return name;
        }
    }

    /** An [AudioDevice] representing the Speakerphone.*/
    public static class Speakerphone extends AudioDevice {

        private final String name;

        public Speakerphone() {
            this.name = "Speakerphone";
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
