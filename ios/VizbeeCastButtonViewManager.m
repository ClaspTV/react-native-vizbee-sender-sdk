#import <VizbeeKit/VizbeeKit.h>
#import <React/RCTViewManager.h>
#import <UIKit/UIKit.h>

@interface VizbeeCastButtonViewManager: RCTViewManager
@end

@implementation VizbeeCastButtonViewManager

RCT_EXPORT_MODULE(VizbeeCastButtonView)

/*
 Do not attempt to set the frame or backgroundColor properties on the UIView instance that is exposed through the -view method.
 React Native will overwrite the values set by your custom class in order to match your JavaScript component's layout props.
 If we need this granularity of control it might be better to wrap the UIView instance we want to style
 in another UIView and return the wrapper UIView instead.
 https://reactnative.dev/docs/native-components-ios
 */
-(UIView*) view {
    
    // 1. Create VZBCastButton
    UIViewController* vc = [self currentTopViewController];
    VZBCastButton* castButton = [Vizbee createCastButtonWithDelegate:nil
                              forViewController:vc];
    
    // 2. Create a castWrapperView and add castButton to it
    UIView* castWrapperView = [[UIView alloc] init];
    [castWrapperView addSubview:castButton];
    
    // 3. Add autolayout constraints
    castButton.translatesAutoresizingMaskIntoConstraints = false;
    [NSLayoutConstraint constraintWithItem:castButton
                         attribute:NSLayoutAttributeTop
                         relatedBy:NSLayoutRelationEqual
                            toItem:castWrapperView
                         attribute:NSLayoutAttributeTop
                        multiplier:1.0 constant:0].active = YES;
    [NSLayoutConstraint constraintWithItem:castButton
                         attribute:NSLayoutAttributeLeading
                         relatedBy:NSLayoutRelationEqual
                            toItem:castWrapperView
                         attribute:NSLayoutAttributeLeading
                        multiplier:1.0 constant:0].active = YES;
    [NSLayoutConstraint constraintWithItem:castButton
                       attribute:NSLayoutAttributeWidth
                       relatedBy:NSLayoutRelationEqual
                          toItem:castWrapperView
                       attribute:NSLayoutAttributeWidth
                      multiplier:1.0 constant:0].active = YES;
    [NSLayoutConstraint constraintWithItem:castButton
                        attribute:NSLayoutAttributeHeight
                        relatedBy:NSLayoutRelationEqual
                           toItem:castWrapperView
                        attribute:NSLayoutAttributeHeight
                       multiplier:1.0 constant:0].active = YES;
    
    return castWrapperView;
}

- (UIViewController *)currentTopViewController {
    UIViewController *topVC = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
    while (topVC.presentedViewController) {
        topVC = topVC.presentedViewController;
    }
    return topVC;
}

@end
