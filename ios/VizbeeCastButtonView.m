//
//  VizbeeCastButtonView.m
//  RNVizbeeSenderSdk
//

#import <VizbeeKit/VizbeeKit.h>
#import "VizbeeCastButtonView.h"
#import <React/RCTLog.h>

@interface VizbeeCastButtonView ()

@property (nonatomic,strong)  VZBCastButton* castButton;

@end

@implementation VizbeeCastButtonView

@synthesize tintColor = _tintColor;

@synthesize disabled = _disabled;

- (NSString *)tintColor {
    return _tintColor;
}

- (void)setTintColor:(NSString *)tintColor {
    if(self.castButton){
        UIColor * updatedColor = [self colorFromHex:tintColor];
        if(updatedColor){
            self.castButton.tintColor = updatedColor;
        }else{
            RCTLogError(@"[RNVZBSDK] VizbeeCastButtonView::invalid tintColor - %@",tintColor);
        }
    }
    _tintColor = tintColor;
}

-(void)setDisabled:(BOOL)disabled{
    if(self.castButton){
        [self.castButton setEnabled:!disabled];
    }
    _disabled = disabled;
}

- (void)layoutSubviews {
    // Apply any layout logic, set background color, text, etc.
    // 1. Create VZBCastButton
    UIViewController* vc = [self currentTopViewController];
    self.castButton = [Vizbee createCastButtonWithDelegate:nil
                                         forViewController:vc];
    // 2. Create a castWrapperView and add castButton to it
    if(nil != self.tintColor){
        [self setTintColor:self.tintColor];
    }
    if(self.disabled == true){
        [self.castButton setEnabled:!self.disabled];
    }

    [self addSubview:self.castButton];
    
    // 3. Add autolayout constraints
    self.castButton.translatesAutoresizingMaskIntoConstraints = false;
    [NSLayoutConstraint constraintWithItem:self.castButton
                                 attribute:NSLayoutAttributeTop
                                 relatedBy:NSLayoutRelationEqual
                                    toItem:self
                                 attribute:NSLayoutAttributeTop
                                multiplier:1.0 constant:0].active = YES;
    [NSLayoutConstraint constraintWithItem:self.castButton
                                 attribute:NSLayoutAttributeLeading
                                 relatedBy:NSLayoutRelationEqual
                                    toItem:self
                                 attribute:NSLayoutAttributeLeading
                                multiplier:1.0 constant:0].active = YES;
    [NSLayoutConstraint constraintWithItem:self.castButton
                                 attribute:NSLayoutAttributeWidth
                                 relatedBy:NSLayoutRelationEqual
                                    toItem:self
                                 attribute:NSLayoutAttributeWidth
                                multiplier:1.0 constant:0].active = YES;
    [NSLayoutConstraint constraintWithItem:self.castButton
                                 attribute:NSLayoutAttributeHeight
                                 relatedBy:NSLayoutRelationEqual
                                    toItem:self
                                 attribute:NSLayoutAttributeHeight
                                multiplier:1.0 constant:0].active = YES;
    
}

- (BOOL)isValidHexColor:(NSString *)hexColor {
    // Define a regular expression pattern for a valid hex color code
    NSString *hexColorPattern = @"^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
    
    NSPredicate *hexColorTest = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", hexColorPattern];
    return [hexColorTest evaluateWithObject:hexColor];
}

- (UIColor *)colorFromHex:(NSString *)hexString {
    NSString *mutatedHexString = [hexString stringByReplacingOccurrencesOfString:@"#" withString:@""];
    NSScanner *scanner = [NSScanner scannerWithString:mutatedHexString];
    unsigned hexValue = 0;
    
    if ([scanner scanHexInt:&hexValue] && [self isValidHexColor:hexString]) {
        CGFloat red = ((hexValue & 0xFF0000) >> 16) / 255.0;
        CGFloat green = ((hexValue & 0x00FF00) >> 8) / 255.0;
        CGFloat blue = (hexValue & 0x0000FF) / 255.0;
        
        return [UIColor colorWithRed:red green:green blue:blue alpha:1.0];
    } else {
        return nil;
    }
}

- (UIViewController *)currentTopViewController {
    UIViewController *topVC = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
    while (topVC.presentedViewController) {
        topVC = topVC.presentedViewController;
    }
    return topVC;
}

@end
