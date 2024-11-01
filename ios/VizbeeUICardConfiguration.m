//
//  VizbeeUICardConfiguration.m
//  RNVizbeeSenderSdk
//
//  Created by Shiva on 22/10/24.
//

#import "VizbeeUICardConfiguration.h"

@implementation VizbeeUICardConfiguration

-(instancetype) init:(NSDictionary*) cardConfiguration {
    
    self = [super init];
    if (self) {
        _title = cardConfiguration[@"title"];
        _subtitle = cardConfiguration[@"subtitle"];
        _ctaText = cardConfiguration[@"ctaText"];
    }
    return self;
}

-(VZBCardConfiguration*) getCardConfigurationForType:(NSString*) cardType {
    
    VZBCardConfiguration *cardConfiguration = nil;
    
    // create a specific card instance and set specific properties
    if ([cardType isEqualToString:@"CAST_AUTHORIZATION"]) {
       cardConfiguration = [[VZBCastAuthorizationCardConfiguration alloc] init];
    } else if ([cardType isEqualToString:@"CAST_INTRODUCTION"]) {
       cardConfiguration = [[VZBCastIntroductionCardConfiguration alloc] init];
    } else if ([cardType isEqualToString:@"SMART_INSTALL"]) {
       cardConfiguration = [[VZBSmartInstallCardConfiguration alloc] init];
    } else if ([cardType isEqualToString:@"GUIDED_SMART_INSTALL"]) {
       cardConfiguration = [[VZBGuidedSmartInstallCardConfiguration alloc] init];
    } else if ([cardType isEqualToString:@"MULTI_DEVICE_SMART_INSTALL"]) {
       cardConfiguration = [[VZBMultiDeviceSmartInstallCardConfiguration alloc] init];
    }
    
    // common attributes
    if (nil != cardConfiguration) {
        cardConfiguration.title = self.title;
        cardConfiguration.subtitle = self.subtitle;
        cardConfiguration.ctaText = self.ctaText;
    }
       
    return cardConfiguration;
}

@end
