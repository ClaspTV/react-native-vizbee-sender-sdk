#import <VizbeeKit/VizbeeKit.h>
#import <React/RCTViewManager.h>
#import <React/RCTUtils.h>

@interface VizbeeCastButtonViewManager: RCTViewManager
@end

@implementation VizbeeCastButtonViewManager

RCT_EXPORT_MODULE(VizbeeCastButtonView)

-(UIView*) view {
    
    UIViewController* vc = RCTPresentedViewcontroller();
    return [Vizbee createCastButtonWithDelegate:nil
                              forViewController:vc];
}

@end
