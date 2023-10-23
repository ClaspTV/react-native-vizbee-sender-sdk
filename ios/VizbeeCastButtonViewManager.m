#import <UIKit/UIKit.h>
#import <VizbeeKit/VizbeeKit.h>
#import <React/RCTViewManager.h>
#import <React/RCTUIManager.h>
#import "VizbeeCastButtonView.h"

@interface VizbeeCastButtonViewManager: RCTViewManager
@end

@implementation VizbeeCastButtonViewManager

RCT_EXPORT_MODULE(VizbeeCastButtonView)

RCT_EXPORT_VIEW_PROPERTY(color, NSString)

RCT_EXPORT_METHOD(setColor:(nonnull NSNumber *)viewTag color:(NSString *)color) {
    // Find the view by its tag
    // Get the bridge
    dispatch_async(dispatch_get_main_queue(), ^{
        RCTBridge *bridge = self.bridge;

        // Get the UIManager instance
        RCTUIManager *uiManager = bridge.uiManager;
        
        UIView *view = [uiManager viewForReactTag:viewTag];
        
        // Check if the view is a castWrapperView containing a VZBCastButton
        if ([view isKindOfClass:[UIView class]]) {
            for (UIView *subview in view.subviews) {
                if ([subview isKindOfClass:[VZBCastButton class]]) {
                    VZBCastButton *castButton = (VZBCastButton *)subview;
                    // Change the tint color
                    UIColor * initialColor = [VizbeeCastButtonView colorFromHex:color];
                    if(initialColor){
                       castButton.tintColor = initialColor;
                    }
                    break; // Assuming there is only one VZBCastButton in the castWrapperView
                }
            }
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