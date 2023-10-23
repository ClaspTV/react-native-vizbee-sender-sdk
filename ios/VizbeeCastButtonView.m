//
//  VizbeeCastButtonView.m
//  RNVizbeeSenderSdk
//

#import <VizbeeKit/VizbeeKit.h>
#import <UIKit/UIKit.h>
#import "VizbeeCastButtonView.h"

@implementation VizbeeCastButtonView

- (void)layoutSubviews {
    // Apply any layout logic, set background color, text, etc.
    // 1. Create VZBCastButton
    UIViewController* vc = [self currentTopViewController];
    VZBCastButton* castButton = [Vizbee createCastButtonWithDelegate:nil
                              forViewController:vc];

    // 2. Create a castWrapperView and add castButton to it
    UIColor * initialColor = [VizbeeCastButtonView colorFromHex:self.color];
    if(initialColor){
        castButton.tintColor = initialColor;
    }
    [self addSubview:castButton];
    
    // 3. Add autolayout constraints
    castButton.translatesAutoresizingMaskIntoConstraints = false;
    [NSLayoutConstraint constraintWithItem:castButton
                         attribute:NSLayoutAttributeTop
                         relatedBy:NSLayoutRelationEqual
                            toItem:self
                         attribute:NSLayoutAttributeTop
                        multiplier:1.0 constant:0].active = YES;
    [NSLayoutConstraint constraintWithItem:castButton
                         attribute:NSLayoutAttributeLeading
                         relatedBy:NSLayoutRelationEqual
                            toItem:self
                         attribute:NSLayoutAttributeLeading
                        multiplier:1.0 constant:0].active = YES;
    [NSLayoutConstraint constraintWithItem:castButton
                       attribute:NSLayoutAttributeWidth
                       relatedBy:NSLayoutRelationEqual
                          toItem:self
                       attribute:NSLayoutAttributeWidth
                      multiplier:1.0 constant:0].active = YES;
    [NSLayoutConstraint constraintWithItem:castButton
                        attribute:NSLayoutAttributeHeight
                        relatedBy:NSLayoutRelationEqual
                           toItem:self
                        attribute:NSLayoutAttributeHeight
                       multiplier:1.0 constant:0].active = YES;
    
}

+ (UIColor *)colorFromHex:(NSString *)hexString {
    hexString = [hexString stringByReplacingOccurrencesOfString:@"#" withString:@""];
    NSScanner *scanner = [NSScanner scannerWithString:hexString];
    unsigned hexValue = 0;

    if ([scanner scanHexInt:&hexValue]) {
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
