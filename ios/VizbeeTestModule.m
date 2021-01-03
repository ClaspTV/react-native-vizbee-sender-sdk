//
//  VizbeeTestModule.m
//  RNVizbeeSenderSdk
//
//  Created by Prashanth Pappu on 1/2/21.

#import "VizbeeTestModule.h"
#import <React/RCTLog.h>

@implementation VizbeeTestModule

{
  BOOL _hasListeners;
}

RCT_EXPORT_MODULE(VizbeeTestModule)

-(dispatch_queue_t) methodQueue {
    return dispatch_get_main_queue();
}

-(NSArray<NSString*>*) supportedEvents {
        return @[@"CancelEvent", @"OKEvent"];
}

// Will be called when this module's first listener is added.
- (void)startObserving
{
  _hasListeners = YES;
}

// Will be called when this module's last listener is removed, or on dealloc.
- (void)stopObserving
{
  _hasListeners = NO;
}

@end
  
