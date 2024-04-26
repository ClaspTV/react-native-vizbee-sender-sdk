//
//  VizbeeMiniCastBarViewManager.m
//  RNVizbeeSenderSdk
//

#import <React/RCTViewManager.h>
#import "VizbeeMiniCastBarView.h"
#import <React/RCTUIManager.h>

@interface VizbeeMiniCastBarViewManager : RCTViewManager

@end

@implementation VizbeeMiniCastBarViewManager

RCT_EXPORT_MODULE(VizbeeMiniCastBarView)

- (UIView *)view {
    return [[VizbeeMiniCastBarView alloc] init];
}

RCT_EXPORT_METHOD(getActive:(nonnull NSNumber *)reactTag callback:(RCTResponseSenderBlock)callback) {
    UIView *view = [self.bridge.uiManager viewForReactTag:reactTag];
    if ([view isKindOfClass:[VizbeeMiniCastBarView class]]) {
        BOOL isActive = [(VizbeeMiniCastBarView *)view isActive];
        callback(@[[NSNumber numberWithBool:isActive]]);
    } else {
        callback(@[@(NO)]);
    }
}

RCT_EXPORT_METHOD(getMinHeight:(nonnull NSNumber *)reactTag callback:(RCTResponseSenderBlock)callback) {
    UIView *view = [self.bridge.uiManager viewForReactTag:reactTag];
    if ([view isKindOfClass:[VizbeeMiniCastBarView class]]) {
        CGFloat minHeight = [(VizbeeMiniCastBarView *)view getMinHeight];
        callback(@[[NSNumber numberWithFloat:minHeight]]);
    } else {
        callback(@[@(0)]);
    }
}

RCT_EXPORT_VIEW_PROPERTY(onVisibilityChange, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(height, CGFloat)

@end
