//
//  VizbeeCastButtonView.m
//  RNVizbeeSenderSdk
//
//  Created by Sidharth Datta on 20/10/23.
//
#import <VizbeeKit/VizbeeKit.h>
#import <UIKit/UIKit.h>
#import "VizbeeCastButtonView.h"

@interface VizbeeCastButtonView ()

@property (nonatomic,strong)  VZBCastButton* castButton;

@end

@implementation VizbeeCastButtonView

@synthesize color = _color;

- (NSString *)color {
    return _color;
}

- (void)setColor:(NSString *)color {
    UIColor * updatedColor = [self colorFromHex:color];
    if(updatedColor){
        self.castButton.tintColor = updatedColor;
        _color = color;
    }
}

- (void)layoutSubviews {
    // Apply any layout logic, set background color, text, etc.
    // 1. Create VZBCastButton
    UIViewController* vc = [self currentTopViewController];
    self.castButton = [Vizbee createCastButtonWithDelegate:nil
                                         forViewController:vc];
    // 2. Create a castWrapperView and add castButton to it
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

- (UIColor *)colorFromHex:(NSString *)hexString {
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
