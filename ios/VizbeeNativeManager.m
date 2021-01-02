
#import "VizbeeNativeManager.h"
#import <React/RCTLog.h>

@implementation VizbeeNativeManager

RCT_EXPORT_MODULE()

-(dispatch_queue_t) methodQueue {
    return dispatch_get_main_queue();
}

//----------------
// Flow APIs
//----------------

RCT_EXPORT_METHOD(smartPrompt) {
    RCTLogInfo(@"Invoking smartPrompt");
}

RCT_EXPORT_METHOD(smartCast) {
    RCTLogInfo(@"Invoking smartCast");
}

RCT_EXPORT_METHOD(smartPlay:(NSDictionary*) vizbeeVideoMap
        didPlayOnTVCallback:(RCTResponseSenderBlock)callback
         doPlayOnTVCallback:(RCTResponseSenderBlock)callback
        ) {
    RCTLogInfo(@"Invoking smartPlay");
}

//----------------
// Session APIs
//----------------

RCT_EXPORT_METHOD(getSessionState:(RCTPromiseResolveBlock)resolver
                         rejecter:(RCTPromiseRejectBlock)rejecter) {
    RCTLogInfo(@"Invoking getSessionState");
}

RCT_EXPORT_METHOD(getSessionConnectedDevice:(RCTPromiseResolveBlock)resolver
                                   rejecter:(RCTPromiseRejectBlock)rejecter) {
    RCTLogInfo(@"Invoking getSessionConnectedDevice");
}

//----------------
// Video APIs
//----------------

RCT_EXPORT_METHOD(play) {
    RCTLogInfo(@"Invoking play");
}

RCT_EXPORT_METHOD(pause) {
    RCTLogInfo(@"Invoking pause");
}

RCT_EXPORT_METHOD(seek) {
    RCTLogInfo(@"Invoking seek");
}

RCT_EXPORT_METHOD(stop) {
    RCTLogInfo(@"Invoking stop");
}

@end
  
