# capacitor-plugin-audio-toggle

Capacitor plugin for audio mode toggle

## Install

```bash
npm install capacitor-plugin-audio-toggle
npx cap sync
```

## API

<docgen-index>

* [`setMode(...)`](#setmode)
* [`start()`](#start)
* [`stop()`](#stop)
* [`getDevices()`](#getdevices)
* [`getSelectedDevice()`](#getselecteddevice)
* [Enums](#enums)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### setMode(...)

```typescript
setMode(data: { mode: AUDIO_MODE; }) => Promise<void>
```

| Param      | Type                                                         |
| ---------- | ------------------------------------------------------------ |
| **`data`** | <code>{ mode: <a href="#audio_mode">AUDIO_MODE</a>; }</code> |

--------------------


### start()

```typescript
start() => Promise<{ device: any; }>
```

**Returns:** <code>Promise&lt;{ device: any; }&gt;</code>

--------------------


### stop()

```typescript
stop() => Promise<void>
```

--------------------


### getDevices()

```typescript
getDevices() => Promise<{ devices: any[]; }>
```

**Returns:** <code>Promise&lt;{ devices: any[]; }&gt;</code>

--------------------


### getSelectedDevice()

```typescript
getSelectedDevice() => Promise<{ device: any; }>
```

**Returns:** <code>Promise&lt;{ device: any; }&gt;</code>

--------------------


### Enums


#### AUDIO_MODE

| Members         | Value                    |
| --------------- | ------------------------ |
| **`EARPIECE`**  | <code>'EARPIECE'</code>  |
| **`SPEAKER`**   | <code>'SPEAKER'</code>   |
| **`NORMAL`**    | <code>'NORMAL'</code>    |
| **`RINGTONE`**  | <code>'RINGTONE'</code>  |
| **`BLUETOOTH`** | <code>'BLUETOOTH'</code> |

</docgen-api>
