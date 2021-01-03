//
//  VizbeeVideo.m
//  RNVizbeeSenderSdk
//

#import "VizbeeVideo.h"

/*
 
 // JS Onject for reference
 export default class VizbeeVideo {

     constructor() {

         this.guid = null;

         // metadata
         this.title = "";
         this.subtitle = "";
         this.imageUrl = "";
         this.isLive = false;

         // streamInfo
         this.streamUrl = null;
         this.tracks = {};
         this.startPositionInSeconds = 0;

         // custom
         this.customProperties = {};
     }
 }
 */

@implementation VizbeeVideo

-(instancetype) init:(NSDictionary*) vizbeeVideo {
    
    self = [super init];
    if (nil != self) {
        
        /** Globally Unique ID of the content */
        _guid = (NSString*) [vizbeeVideo objectForKey:@"guid"];

        /** Title of the video */
        _title = (NSString*) [vizbeeVideo objectForKey:@"title"];

        /** subTitle of the video */
        _subtitle = (NSString*) [vizbeeVideo objectForKey:@"subtitle"];

        /** Image URL */
        _imageUrl = (NSString*) [vizbeeVideo objectForKey:@"imageUrl"];

        /** True if the video is a live stream; false by default */
        NSNumber* isLiveObject = (NSNumber*) [vizbeeVideo objectForKey:@"isLive"];
        if (nil != isLiveObject) {
            _isLive = [isLiveObject boolValue];
        }

        /** streamUrl */
        _streamUrl = (NSString*) [vizbeeVideo objectForKey:@"streamUrl"];

        /** startPosition in seconds */
        NSNumber* positionObject = (NSNumber*) [vizbeeVideo objectForKey:@"startPositionInSeconds"];
        if (nil != positionObject) {
            _startPositionInSeconds = [positionObject doubleValue];
        }

        NSDictionary* customProperties = (NSDictionary*) [vizbeeVideo objectForKey:@"customProperties"];
        _customProperties = [NSDictionary dictionaryWithDictionary:customProperties];
    }
    
    return self;
}

-(NSDictionary*) toDictionary {
    
    NSDictionary* result = @{
        @"guid" : self.guid,
        @"title" : self.title,
        @"subtitle" : self.subtitle,
        @"imageUrl" : self.imageUrl,
        @"isLive" : [NSNumber numberWithBool:self.isLive],
        
        @"streamUrl" : self.streamUrl,
        @"startPositionInSeconds" : [NSNumber numberWithDouble:self.startPositionInSeconds],
        
        @"customProperties" : [NSDictionary dictionaryWithDictionary:self.customProperties]
    };
    return result;
}

@end