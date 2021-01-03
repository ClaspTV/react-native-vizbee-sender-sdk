//
//  VizbeeVideo.h
//  RNVizbeeSenderSdk
//

#import <Foundation/Foundation.h>

@interface VizbeeVideo : NSObject

/** Globally Unique ID of the content */
@property (nonatomic, copy) NSString* guid;

//----------------------------------
#pragma mark - Metadata
//----------------------------------

/** Title of the video */
@property (nonatomic, copy) NSString* title;

/** subTitle of the video */
@property (nonatomic, copy) NSString* subtitle;

/** Image URL */
@property (nonatomic, copy) NSString* imageUrl;

/** True if the video is a live stream; false by default */
@property (nonatomic, assign) BOOL isLive;

//----------------------------------
#pragma mark - StreamInfo
//----------------------------------

/** streamUrl */
@property (nonatomic, copy) NSString* streamUrl;

/** startPosition in seconds */
@property (nonatomic, assign) NSTimeInterval startPositionInSeconds;

//----------------------------------
#pragma mark - Custom metadata & streamInfo
//----------------------------------

@property (nonatomic, copy) NSDictionary* customProperties;

-(instancetype) init:(NSDictionary*) vizbeeVideo;

@end
