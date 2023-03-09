# capacitor-plugin-audio-toggle

Capacitor plugin for audio mode toggle

## Install

```bash
npm install capacitor-plugin-audio-toggle
npx cap sync
```

## API

<docgen-index>

* [`selectDevice(...)`](#selectdevice)
* [`start()`](#start)
* [`stop()`](#stop)
* [`getDevices()`](#getdevices)
* [`getSelectedDevice()`](#getselecteddevice)
* [Enums](#enums)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### selectDevice(...)

```typescript
selectDevice(data: { device: DeviceName; }) => Promise<void>
```

| Param      | Type                                                           |
| ---------- | -------------------------------------------------------------- |
| **`data`** | <code>{ device: <a href="#devicename">DeviceName</a>; }</code> |

--------------------


### start()

```typescript
start() => Promise<{ availableDevices: DeviceName[]; selectedDevice: DeviceName; }>
```

**Returns:** <code>Promise&lt;{ availableDevices: DeviceName[]; selectedDevice: <a href="#devicename">DeviceName</a>; }&gt;</code>

--------------------


### stop()

```typescript
stop() => Promise<void>
```

--------------------


### getDevices()

```typescript
getDevices() => Promise<{ availableDevices: DeviceName[]; }>
```

**Returns:** <code>Promise&lt;{ availableDevices: DeviceName[]; }&gt;</code>

--------------------


### getSelectedDevice()

```typescript
getSelectedDevice() => Promise<{ selectedDevice: DeviceName; }>
```

**Returns:** <code>Promise&lt;{ selectedDevice: <a href="#devicename">DeviceName</a>; }&gt;</code>

--------------------


### Enums


#### DeviceName

| Members                | Value                           |
| ---------------------- | ------------------------------- |
| **`Earpiece`**         | <code>'Earpiece'</code>         |
| **`Speakerphone`**     | <code>'Speakerphone'</code>     |
| **`WiredHeadset`**     | <code>'WiredHeadset'</code>     |
| **`BluetoothHeadset`** | <code>'BluetoothHeadset'</code> |

</docgen-api>
