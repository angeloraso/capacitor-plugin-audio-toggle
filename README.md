# capacitor-plugin-audio-toggle

Capacitor plugin for audio mode toggle

## Install

```bash
npm install capacitor-plugin-audio-toggle
npx cap sync
```

## API

<docgen-index>

* [`enable()`](#enable)
* [`disable()`](#disable)
* [`reset()`](#reset)
* [`selectDevice(...)`](#selectdevice)
* [`getAvailableDevices()`](#getavailabledevices)
* [`getSelectedDevice()`](#getselecteddevice)
* [`addListener('onChanges', ...)`](#addlisteneronchanges)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### enable()

```typescript
enable() => Promise<void>
```

--------------------


### disable()

```typescript
disable() => Promise<void>
```

--------------------


### reset()

```typescript
reset() => Promise<void>
```

--------------------


### selectDevice(...)

```typescript
selectDevice(data: { device: DeviceName; }) => Promise<void>
```

| Param      | Type                                                           |
| ---------- | -------------------------------------------------------------- |
| **`data`** | <code>{ device: <a href="#devicename">DeviceName</a>; }</code> |

--------------------


### getAvailableDevices()

```typescript
getAvailableDevices() => Promise<{ earpiece: boolean; speakerphone: boolean; wired: boolean; bluetooth: boolean; }>
```

**Returns:** <code>Promise&lt;{ earpiece: boolean; speakerphone: boolean; wired: boolean; bluetooth: boolean; }&gt;</code>

--------------------


### getSelectedDevice()

```typescript
getSelectedDevice() => Promise<{ selectedDevice: DeviceName; }>
```

**Returns:** <code>Promise&lt;{ selectedDevice: <a href="#devicename">DeviceName</a>; }&gt;</code>

--------------------


### addListener('onChanges', ...)

```typescript
addListener(eventName: 'onChanges', listenerFunc: (data: { earpiece: boolean; speakerphone: boolean; wired: boolean; bluetooth: boolean; selectedDevice: DeviceName; }) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

| Param              | Type                                                                                                                                                                    |
| ------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'onChanges'</code>                                                                                                                                                |
| **`listenerFunc`** | <code>(data: { earpiece: boolean; speakerphone: boolean; wired: boolean; bluetooth: boolean; selectedDevice: <a href="#devicename">DeviceName</a>; }) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

--------------------


### Interfaces


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


### Type Aliases


#### DeviceName

<code>'earpiece' | 'speakerphone' | 'wired' | 'bluetooth'</code>

</docgen-api>
