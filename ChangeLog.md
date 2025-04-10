# Change Log
All notable changes to VizbeeKit will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/) 
and this project adheres to [Semantic Versioning](http://semver.org/).


## [Unreleased]
### Added
### Changed (deprecated tag)
### Updated
### Deprecated 
### Removed
### Fixed
### Security

## [1.1.48] - 2025-04-10
### Updated
    Android SDK has been updated to 6.8.8, which includes support for passing Options.

## [1.1.47] - 2025-04-02
### Updated
    Updated VizbeeLogger from ts to js to fix issue for pure js app.

## [1.1.46] - 2025-03-28
### Updated
    Updated Session, Analytics, and CastIcon proxy listeners to be registered only after SDK initialization for iOS.

## [1.1.45] - 2025-03-19
### Updated
    Android SDK has been updated to 6.8.5, which includes support for Edge-to-Edge.
    
## [1.1.44] - 2025-03-14
### Fixed
    Andriod cast bar crash on hide/show and updated the native android sdk to 6.8.4.  

## [1.1.43] - 2025-01-21
### Added
    Support for sending cast icon states.  

## [1.1.42] - 2025-01-21
### Added
    Support for sending and receiving messages.  

## [1.1.41] - 2025-01-02
### Updated
    Added minimum and maximum version for the SDK support. Also added condition to use the VizbeeCastBar component only in  old architecture of RN.  

## [1.1.40] - 2024-12-10
### Updated
    Updated Android SDK to 6.7.6

## [1.1.39] - 2024-11-22
### Updated
    Added new remove listener in NativeEventEmitter for supporting latest RN versions.

## [1.1.38] - 2024-11-01
### Updated
    Replaced empty string with null and undefined for VizbeeUICardConfiguration.

## [1.1.37] - 2024-10-28
### Added
    Support for setting card(CA, CI & SI) configuration(title, subtitle etc.) through the API.
    Updaeted Android SDK to 6.7.5

## [1.1.36] - 2024-10-15
### Added
    Updaeted Android SDK to 6.7.3

## [1.1.35] - 2024-09-16
### Added
    Updaeted Android SDK to 6.7.1

## [1.1.34] - 2024-08-29
### Added
    Updaeted Android SDK to 6.6.9

## [1.1.33] - 2024-08-16
### Added
    An API getSignInInfoAsync to fetch signInInfo.

## [1.1.32] - 2024-08-09
### Added
    An API to add analytics attributes.

## [1.1.31] - 2024-07-25
### Fixed
    Crash on Cast Bar Wrapper component unmount.
    
## [1.1.30] - 2024-07-08
### Added
    React Native Wrapper component over android native cast bar.

## [1.1.29] - 2024-06-10
### Fixed
    Fixed paths in index.web.js 

## [1.1.28] - 2024-05-29
### Updated
    Analytics events with card dismissed event
    Android SDK to 6.6.0

## [1.1.27] - 2024-05-29
### Fixed
    Downgrading Android Native Library to version 6.5.5.
    
## [1.1.26] - 2024-05-24
### Added
    React Native Wrapper component over ios native cast bar.
    
## [1.1.25] - 2024-05-03
### Added
    React Native Wrapper component over native cast button

## [1.1.24] - 2024-04-25
### Added 
    Fix for the iOS build error

## [1.1.23] - 2024-04-22
### Added 
    Analytics support to send a few events

## [1.1.22] - 2024-04-10
### Added 
    Enhanced smartPrompt API to enable specific subflows

## [1.1.21] - 2024-04-04
### Added 
    Updated Android SDK to 6.5.4

## [1.1.20] - 2024-04-02
### Added 
    Updated Android SDK to 6.5.3

## [1.1.19] - 2023-12-20
### Added 
    Default UIWindowLevel set to UIWindowLevel + 3 for Vizbee iOS SDK to show all of Vizbee UI cards on it's own UIWindow.

## [1.1.18] - 2023-12-18
### Fixed 
    Cast icon not showing in Android app as it's default visibility state is GONE by the time react-native renders the UI.

## [1.1.17] - 2023-12-14
### Fixed 

## [1.1.16] - 2023-12-07
### Added 
    Color the cast icon from JS layer 

## [1.1.13] - 2023-09-28
### Added 
    SmartPlay flow support with VizbeeRequest on Android
