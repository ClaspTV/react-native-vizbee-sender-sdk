//
//  VizbeeCastBarView.h
//  RNVizbeeSenderSdk
//

#import <UIKit/UIKit.h>

@interface VizbeeCastBarView : UIView

@property (nonatomic, copy) void (^onVisibilityChange)(NSDictionary *info);
@property (nonatomic, assign) CGFloat height;

- (BOOL)isActive;
- (CGFloat)getMinHeight;

@end
