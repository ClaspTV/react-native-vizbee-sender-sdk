// VizbeeCastBarManager.m

#import <React/RCTViewManager.h>
#import <VizbeeKit/VizbeeKit.h>
#import <React/RCTViewManager.h>

@interface VizbeeCastBarManager : RCTViewManager

@end

@implementation VizbeeCastBarManager

RCT_EXPORT_MODULE();

- (UIView *)view {
    return [[VZBCastBarViewController alloc] init];
}

RCT_EXPORT_VIEW_PROPERTY(delegate, id<VZBUIMiniCastViewDelegate>);
RCT_EXPORT_VIEW_PROPERTY(active, BOOL);
RCT_EXPORT_VIEW_PROPERTY(minHeight, CGFloat);
RCT_EXPORT_VIEW_PROPERTY(associatedViewController, UIViewController);

@end
