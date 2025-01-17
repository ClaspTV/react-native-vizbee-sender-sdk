#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#else
#import <React/RCTBridgeModule.h>
#endif

#import <React/RCTEventEmitter.h>
#import <VizbeeKit/VizbeeKit.h>

@interface VizbeeNativeManager : RCTEventEmitter <RCTBridgeModule, 
                                                    VZBSessionStateDelegate, 
                                                    VZBAnalyticsDelegate, 
                                                    VZBVideoStatusUpdateDelegate, 
                                                    VZBVolumeStatusDelegate,
                                                    VZBEventHandler>

@end
  
