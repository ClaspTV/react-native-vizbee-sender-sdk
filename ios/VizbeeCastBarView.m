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

// Initialize the view and set up necessary components
- (instancetype)init {
    self = [super init];
    if (self) {
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 0.1 * NSEC_PER_SEC), dispatch_get_main_queue(), ^{
            [self setup];
        });
    }
    return self;
}

// Setup method to initialize and configure the view
- (void)setup {
    [self.castBarController.view removeFromSuperview];
    self.castBarController = nil;
    self.heightConstraint = nil;
    self.castBarController = [Vizbee createCastBarController];
    self.castBarController.delegate = self;
    [self addSubview:self.castBarController.view];
    
    self.clipsToBounds = true;
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(applicationDidBecomeActive:)
                                                 name:UIApplicationDidBecomeActiveNotification
                                               object:nil];

    self.heightConstraint = [NSLayoutConstraint constraintWithItem:self attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1 constant:64]; // Default height constraint
    [self.heightConstraint setActive:YES];
    
    // Add constraints
    self.castBarController.view.translatesAutoresizingMaskIntoConstraints = NO;
    [NSLayoutConstraint activateConstraints:@[
        [self.castBarController.view.topAnchor constraintEqualToAnchor:self.topAnchor],
        [self.castBarController.view.bottomAnchor constraintEqualToAnchor:self.bottomAnchor],
        [self.castBarController.view.leadingAnchor constraintEqualToAnchor:self.leadingAnchor],
        [self.castBarController.view.trailingAnchor constraintEqualToAnchor:self.trailingAnchor]
    ]];
}

// Deallocate any resources when the view is deallocated
- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [self.castBarController.view removeFromSuperview];
    self.castBarController = nil;
    self.heightConstraint = nil;
}

// Method to handle application becoming active
- (void)applicationDidBecomeActive:(NSNotification *)notification {
    BOOL shouldAppear = self.castBarController.active;
    [self handleVisibilityChange:shouldAppear];
}

// Setter for the height property
- (void)setHeight:(CGFloat)height {
    _height = height;
    self.heightConstraint.constant = height;
}

// Method to get the active state of the view
- (BOOL)isActive {
    return self.castBarController.active;
}

// Method to get the minimum height of the view
- (CGFloat)getMinHeight {
    return self.castBarController.minHeight;
}

// Delegate method to handle visibility change of the cast bar view
- (void)miniCastViewController:(VZBCastBarViewController *)miniCastViewController shouldAppear:(BOOL)shouldAppear {
    [self handleVisibilityChange:shouldAppear];
}

// Method to handle visibility change of the view
- (void)handleVisibilityChange:(BOOL)shouldAppear {
    self.hidden = !shouldAppear;
    if (self.onVisibilityChange) {
        self.onVisibilityChange(@{@"shouldAppear": @(shouldAppear)});
    }
}

@end
