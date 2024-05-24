//
//  VizbeeCastBarViewManager.m
//  RNVizbeeSenderSdk
//

#import <React/RCTViewManager.h>
#import "VizbeeCastBarView.h"
#import <React/RCTUIManager.h>

@interface VizbeeCastBarViewManager : RCTViewManager

@end

@implementation VizbeeCastBarViewManager

RCT_EXPORT_MODULE(VizbeeCastBarView)

- (UIView *)view {
    return [[VizbeeCastBarView alloc] init];
}

RCT_EXPORT_METHOD(getActive:(nonnull NSNumber *)reactTag callback:(RCTResponseSenderBlock)callback) {
    UIView *view = [self.bridge.uiManager viewForReactTag:reactTag];
    if ([view isKindOfClass:[VizbeeCastBarView class]]) {
        BOOL isActive = [(VizbeeCastBarView *)view isActive];
        callback(@[[NSNumber numberWithBool:isActive]]);
    } else {
        callback(@[@(NO)]);
    }
}

RCT_EXPORT_METHOD(getMinHeight:(nonnull NSNumber *)reactTag callback:(RCTResponseSenderBlock)callback) {
    UIView *view = [self.bridge.uiManager viewForReactTag:reactTag];
    if ([view isKindOfClass:[VizbeeCastBarView class]]) {
        CGFloat minHeight = [(VizbeeCastBarView *)view getMinHeight];
        callback(@[[NSNumber numberWithFloat:minHeight]]);
    } else {
        callback(@[@(0)]);
    }
}

RCT_EXPORT_VIEW_PROPERTY(onVisibilityChange, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(height, CGFloat)

@end
