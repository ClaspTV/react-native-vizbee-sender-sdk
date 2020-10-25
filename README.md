
# react-native-vizbee-sender-sdk
![Vizbee](https://static.claspws.tv/images/common/logos/vizbee_logo_tagline.png)

## Getting started

`$ npm install react-native-vizbee-sender-sdk --save`

### Mostly automatic installation

`$ react-native link react-native-vizbee-sender-sdk`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-vizbee-sender-sdk` and add `RNVizbeeSenderSdk.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNVizbeeSenderSdk.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import tv.vizbee.rnsender.RNVizbeeSenderSdkPackage;` to the imports at the top of the file
  - Add `new RNVizbeeSenderSdkPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-vizbee-sender-sdk'
  	project(':react-native-vizbee-sender-sdk').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-vizbee-sender-sdk/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-vizbee-sender-sdk')
  	```


## Usage
```javascript
import RNVizbeeSenderSdk from 'react-native-vizbee-sender-sdk';

// TODO: What to do with the module?
RNVizbeeSenderSdk;
```
  