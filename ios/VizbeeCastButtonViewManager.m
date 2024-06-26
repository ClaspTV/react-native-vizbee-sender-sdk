#import <React/RCTViewManager.h>
#import "VizbeeCastButtonView.h"
#import <React/RCTUIManager.h>
#import <React/RCTLog.h>

@interface VizbeeCastButtonViewManager: RCTViewManager
@end

@implementation VizbeeCastButtonViewManager

RCT_EXPORT_MODULE(VizbeeCastButtonView)

RCT_EXPORT_VIEW_PROPERTY(tintColor, NSString)

RCT_EXPORT_VIEW_PROPERTY(disabled, BOOL)

RCT_EXPORT_METHOD(simulateButtonClick:(nonnull NSNumber *)viewTag)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    UIView *view = [self.bridge.uiManager viewForReactTag:viewTag];
    if ([view isKindOfClass:[VizbeeCastButtonView class]]) {
      VizbeeCastButtonView *castButtonView = (VizbeeCastButtonView *)view;
      [castButtonView simulateButtonClick];
    } else {
      RCTLogInfo(@"[RNVZBSDK] VizbeeCastButtonViewManager::view with tag %@ is not a VizbeeCastButtonView", viewTag);
    }
  });
}

/*
 Do not attempt to set the frame or backgroundColor properties on the UIView instance that is exposed through the -view method.
 React Native will overwrite the values set by your custom class in order to match your JavaScript component's layout props.
 If we need this granularity of control it might be better to wrap the UIView instance we want to style
 in another UIView and return the wrapper UIView instead.
 https://reactnative.dev/docs/native-components-ios
 */

-(UIView*) view {
    return [[VizbeeCastButtonView alloc]init];
}

@end
