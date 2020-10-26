
# react-native-vizbee-sender-sdk
![Vizbee](https://static.claspws.tv/images/common/logos/vizbee_logo_tagline.png)

## Overview

Vizbee enables streaming apps to deliver consistent and seamless cross-device experiences. This repository provides the React Native SDK for enabling Vizbee's cross-device experiences. You need a [Vizbee account](https://console.vizbee.tv) in order to use this SDK.

## Getting started

### <a name="auto"></a> Mostly automatic installation

This command will add the Vizbee SDK to your repository and also perform 'automatic linking'.

`$ yarn add react-native-vizbee-sender-sdk`

#### Android

1. Vizbee Android Sender SDK is included as a dependency for the React Native Sender SDK. This Android SDK is available from Vizbee's artifactory server. Add the following to your app's Gradle built script

```
repositories {
    maven { url 'http://repo.claspws.tv/artifactory/libs'}
}
```

### Manual installation

#### Android

The following three steps should typically be automatically done when you try [automatic installation](#auto)

1. Add the Vizbee sender SDK node module as project in your app. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-vizbee-sender-sdk'
  	project(':react-native-vizbee-sender-sdk').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-vizbee-sender-sdk/android')
  	```

2. Add Vizbee SDK as a dependency for building your app. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      implementation project(':react-native-vizbee-sender-sdk')

3. Register the Vizbee sender SDK as a provider of native modules. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import tv.vizbee.rnsender.RNVizbeeSenderSdkPackage;` to the imports at the top of the file
  - Add `new RNVizbeeSenderSdkPackage()` to the list returned by the `getPackages()` method


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-vizbee-sender-sdk` and add `RNVizbeeSenderSdk.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNVizbeeSenderSdk.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)

## Developer guide

Get detailed instructions for integrating this React Native Vizbeee Sender from the [Vizbee console](https://console.vizbee.tv)
  