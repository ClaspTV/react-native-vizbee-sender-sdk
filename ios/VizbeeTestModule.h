//
//  VizbeeTestModule.h
//  RNVizbeeSenderSdk
//
//  Created by Prashanth Pappu on 1/2/21.
//

#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#else
#import <React/RCTBridgeModule.h>
#endif

#import <React/RCTEventEmitter.h>

@interface VizbeeTestModule : RCTEventEmitter <RCTBridgeModule>

@end
