//
//  VizbeeCastButtonView.h
//  RNVizbeeSenderSdk
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface VizbeeCastButtonView : UIView

@property (nonatomic, strong) NSString *color;
+ (UIColor *)colorFromHex:(NSString *)hexString;

@end

NS_ASSUME_NONNULL_END
