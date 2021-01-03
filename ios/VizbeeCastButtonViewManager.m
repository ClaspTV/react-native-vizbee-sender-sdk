#import <VizbeeKit/VizbeeKit.h>
#import <React/RCTViewManager.h>
#import <UIKit/UIKit.h>

@interface VizbeeCastButtonViewManager: RCTViewManager
@end

@implementation VizbeeCastButtonViewManager

RCT_EXPORT_MODULE(VizbeeCastButtonView)

-(UIView*) view {
    
    UIViewController* vc = [self currentTopViewController];
    return [Vizbee createCastButtonWithDelegate:nil
                              forViewController:vc];
}

- (UIViewController *)currentTopViewController {
    UIViewController *topVC = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
    while (topVC.presentedViewController) {
        topVC = topVC.presentedViewController;
    }
    return topVC;
}

@end
