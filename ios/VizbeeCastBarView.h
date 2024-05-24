//
//  VizbeeCastBarView.h
//  RNVizbeeSenderSdk
//

#import <UIKit/UIKit.h>

@interface VizbeeCastBarView : UIView

/// Callback block invoked when visibility changes
@property (nonatomic, copy) void (^onVisibilityChange)(NSDictionary *info);

/// Height of the view
@property (nonatomic, assign) CGFloat height;

/// Method to check if the view is active
- (BOOL)isActive;

/// Method to get the minimum height of the view
- (CGFloat)getMinHeight;

@end
