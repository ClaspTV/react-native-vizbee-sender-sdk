//
//  VizbeeCastBarView.m
//  RNVizbeeSenderSdk
//

#import "VizbeeCastBarView.h"
#import <VizbeeKit/VizbeeKit.h>

@interface VizbeeCastBarView () <VZBUIMiniCastViewDelegate>

@property (nonatomic, strong) VZBCastBarViewController *castBarController;
@property (nonatomic, strong) NSLayoutConstraint *heightConstraint;

@end

@implementation VizbeeCastBarView

- (instancetype)init {
    self = [super init];
    if (self) {
        [self.castBarController.view removeFromSuperview];
        self.castBarController = nil;
        self.heightConstraint = nil;
        self.clipsToBounds = true;
    }
    return self;
}

- (void)layoutSubviews {
    self.castBarController = [Vizbee createCastBarController];
    self.castBarController.delegate = self;
    
    self.heightConstraint = [NSLayoutConstraint constraintWithItem:self attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1 constant:64]; // Default height constraint
    [self.heightConstraint setActive:YES];
    
    [self addSubview:self.castBarController.view];
    
    // Add constraints
    self.castBarController.view.translatesAutoresizingMaskIntoConstraints = NO;
    [NSLayoutConstraint activateConstraints:@[
        [self.castBarController.view.topAnchor constraintEqualToAnchor:self.topAnchor],
        [self.castBarController.view.bottomAnchor constraintEqualToAnchor:self.bottomAnchor],
        [self.castBarController.view.leadingAnchor constraintEqualToAnchor:self.leadingAnchor],
        [self.castBarController.view.trailingAnchor constraintEqualToAnchor:self.trailingAnchor]
    ]];
}

- (void)setHeight:(CGFloat)height {
    _height = height;
    self.heightConstraint.constant = height;
}

- (BOOL)isActive {
    return self.castBarController.active;
}

- (CGFloat)getMinHeight {
    return self.castBarController.minHeight;
}

- (void)miniCastViewController:(VZBCastBarViewController *)miniCastViewController shouldAppear:(BOOL)shouldAppear {
    self.hidden = !shouldAppear;
    if (self.onVisibilityChange) {
        self.onVisibilityChange(@{@"shouldAppear": @(shouldAppear)});
    }
}

@end
