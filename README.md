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
* [`selectDevice(...)`](#selectdevice)
* [`activate()`](#activate)
* [`deactivate()`](#deactivate)
* [`getAvailableDevices()`](#getavailabledevices)
* [`getSelectedDevice()`](#getselecteddevice)
* [`addListener('onChanges', ...)`](#addlisteneronchanges)
* [Interfaces](#interfaces)
* [Enums](#enums)

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


### selectDevice(...)

```typescript
selectDevice(data: { device: DeviceName; }) => Promise<void>
```

| Param      | Type                                                           |
| ---------- | -------------------------------------------------------------- |
| **`data`** | <code>{ device: <a href="#devicename">DeviceName</a>; }</code> |

--------------------


### activate()

```typescript
activate() => Promise<void>
```

--------------------


### deactivate()

```typescript
deactivate() => Promise<void>
```

--------------------


### getAvailableDevices()

```typescript
getAvailableDevices() => Promise<{ available: DeviceName[]; }>
```

**Returns:** <code>Promise&lt;{ available: DeviceName[]; }&gt;</code>

--------------------


### getSelectedDevice()

```typescript
getSelectedDevice() => Promise<{ selected: DeviceName; }>
```

**Returns:** <code>Promise&lt;{ selected: <a href="#devicename">DeviceName</a>; }&gt;</code>

--------------------


### addListener('onChanges', ...)

```typescript
addListener(eventName: 'onChanges', listenerFunc: () => { available: DeviceName[]; selected: DeviceName; }) => Promise<PluginListenerHandle> & PluginListenerHandle
```

| Param              | Type                                                                                               |
| ------------------ | -------------------------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'onChanges'</code>                                                                           |
| **`listenerFunc`** | <code>() =&gt; { available: DeviceName[]; selected: <a href="#devicename">DeviceName</a>; }</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### Interfaces


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


### Enums


#### DeviceName

| Members                | Value                           |
| ---------------------- | ------------------------------- |
| **`Earpiece`**         | <code>'Earpiece'</code>         |
| **`Speakerphone`**     | <code>'Speakerphone'</code>     |
| **`WiredHeadset`**     | <code>'WiredHeadset'</code>     |
| **`BluetoothHeadset`** | <code>'BluetoothHeadset'</code> |

</docgen-api>
