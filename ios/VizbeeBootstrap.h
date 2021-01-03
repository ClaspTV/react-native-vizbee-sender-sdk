//
//  VizbeeBootstrap.h
//  RNVizbeeSenderSdk
//

#import <VizbeeKit/VizbeeKit.h>
#import <Foundation/Foundation.h>

@interface VizbeeBootstrap : NSObject

+(instancetype) getInstance;

-(void) initialize:(NSString*)vizbeeAppId;

-(void) initialize:(NSString*)vizbeeAppId withOptions:(VZBOptions*) options;

@end
