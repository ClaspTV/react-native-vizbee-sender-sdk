# react-native-vizbee-sender-sdk

<img src="https://static.claspws.tv/images/common/logos/vizbee_logo_tagline.png" alt="vizbee" width="400"/>

## Overview

Vizbee enables streaming apps to deliver great cross-device experiences. This repository provides the React Native SDK for enabling Vizbee's cross-device experiences. You need a [Vizbee account](https://console.vizbee.tv) in order to use this SDK.

**Supported React Native versions:** `>=0.60.0 <=0.87.1`.

**Known limitation:** on React Native's new architecture, `<VizbeeCastBar>` renders but its visibility-change events do not reach JS. The component dispatches via `getJSModule(RCTEventEmitter)`, which React Native rejects under bridgeless mode (`UIManagerHelper.getEventDispatcher` is required). Verified on RN 0.87.1.

## Build setup

See [build setup instructions](https://gist.github.com/vizbee/48815c9cf8f752507b470ef99c6cc090) for RN Vizbee Sender SDK.

## Developer guide

Once you are setup, you can get further instructions for integrating this React Native Vizbeee Sender from the [Vizbee console](https://console.vizbee.tv)
