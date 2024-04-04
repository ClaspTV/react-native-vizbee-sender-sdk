#import <React/RCTViewManager.h>
#import "VizbeeCastButtonView.h"
#import <React/RCTUIManager.h>
#import <React/RCTLog.h>

@interface VizbeeCastButtonViewManager: RCTViewManager
@end

@implementation VizbeeCastButtonViewManager

RCT_EXPORT_MODULE(VizbeeCastButtonView)

RCT_EXPORT_VIEW_PROPERTY(tintColor, NSString)

RCT_EXPORT_VIEW_PROPERTY(enabled, BOOL)

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
