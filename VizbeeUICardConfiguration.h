//
//  VizbeeUICardConfiguration.h
//  RNVizbeeSenderSdk
//
//  Created by Shiva on 22/10/24.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <VizbeeKit/VizbeeKit.h>

@interface VizbeeUICardConfiguration : NSObject

@property (nonatomic, strong) NSString* title;
@property (nonatomic, strong) NSString* subtitle;
@property (nonatomic, strong) NSString* ctaText;

-(instancetype) init:(NSDictionary*) cardConfiguration;

-(VZBCardConfiguration*) getCardConfigurationForType:(NSString*) cardType;

@end
