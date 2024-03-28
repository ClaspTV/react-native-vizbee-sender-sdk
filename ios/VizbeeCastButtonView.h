//
//  VizbeeCastButtonView.h
//  RNVizbeeSenderSdk
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface VizbeeCastButtonView : UIView

@property (nonatomic, strong) NSString *tintColor;

@property (nonatomic, assign) BOOL enabled;

-(void)simulateButtonClick;

@end

NS_ASSUME_NONNULL_END
