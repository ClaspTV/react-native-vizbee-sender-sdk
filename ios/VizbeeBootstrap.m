//
//  VizbeeBootstrap.m
//  RNVizbeeSenderSdk
//

#import "VizbeeBootstrap.h"
#import <VizbeeKit/VizbeeKit.h>
#import "VizbeeAppAdapter.h"

@implementation VizbeeBootstrap

+(instancetype) getInstance {
    static VizbeeBootstrap* singleton = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^(void){
        singleton = [[self alloc] init];
    });
    return singleton;
}

-(void) initialize:(NSString*)vizbeeAppId {
    
    VZBOptions *vizbeeOptions = [VZBOptions new];
    [self initialize:vizbeeAppId withOptions:vizbeeOptions];
}

-(void) initialize:(NSString*)vizbeeAppId withOptions:(VZBOptions*) options {
    
    // always use production
    options.isProduction = YES;
    VizbeeAppAdapter* vizbeeAppAdapter = [[VizbeeAppAdapter alloc] init];
    [Vizbee startWithAppID:appId appAdapterDelegate:vizbeeAppAdapter andVizbeeOptions:vizbeeOptions];
}

@end
