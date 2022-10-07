#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#else
#import <React/RCTBridgeModule.h>
#endif

#import <React/RCTEventEmitter.h>
#import <VizbeeKit/VZBSessionManager.h>
#import <VizbeeKit/VZBUIMiniCastViewDelegate.h>

@interface VizbeeNativeManager : RCTEventEmitter <RCTBridgeModule, VZBSessionStateDelegate, VZBVideoStatusUpdateDelegate, VZBUIMiniCastViewDelegate>

@end
  
