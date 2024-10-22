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
        _title = cardConfiguration[@"title"] ?: @"";
        _subtitle = cardConfiguration[@"subtitle"] ?: @"";
        _ctaText = cardConfiguration[@"ctaText"] ?: @"";
    }
    return self;
}

-(VZBCardConfiguration*) getCardConfigurationForType:(NSString*) cardType {
    
    VZBCardConfiguration *cardConfiguration = nil;
    
    if ([cardType isEqualToString:@"CAST_INTRODUCTION"]) {
        
       VZBCastIntroductionCardConfiguration *ciCardConfiguration = [[VZBCastIntroductionCardConfiguration alloc] init];
       ciCardConfiguration.title = self.title;
       ciCardConfiguration.subtitle = self.subtitle;
       ciCardConfiguration.ctaText = self.ctaText;
       
       cardConfiguration = ciCardConfiguration;
    }
    
    return cardConfiguration;
}

@end
