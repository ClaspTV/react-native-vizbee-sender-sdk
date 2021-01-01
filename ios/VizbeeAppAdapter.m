//
//  VizbeeAppAdapter.m
//  VizbeeKit
//

#import "VizbeeAppAdapter.h"

@implementation VizbeeAppAdapter

-(void) getVideoInfoByGUID:(nonnull NSString *)guid
                 onSuccess:(void(^_Nonnull)(id _Nonnull appVideoObject))successCallback
                 onFailure:(void(^_Nonnull)(NSError* _Nonnull error))failureCallback {
    
    if (nil != failureCallback) {
        NSError *error = [NSError errorWithDomain:@"vizbee_rn_sender_sdk"
                                           code:100
                                       userInfo:@{
                                                   NSLocalizedDescriptionKey:@"Method not implemented"
                                       }];
        failureCallback(error);
    }
}

-(void) getVZBMetadataFromVideo:(id _Nonnull)appVideoObject
                      onSuccess:(void(^_Nonnull)(VZBVideoMetadata* _Nonnull videoMetadata))successCallback
                      onFailure:(void(^_Nonnull)(NSError* _Nonnull error))failureCallback {
    
    if (nil != failureCallback) {
        NSError *error = [NSError errorWithDomain:@"vizbee_rn_sender_sdk"
                                           code:100
                                       userInfo:@{
                                                   NSLocalizedDescriptionKey:@"Method not implemented"
                                       }];
        failureCallback(error);
    }
}

-(void) getVZBStreamInfoFromVideo:(id _Nonnull)appVideoObject
                        forScreen:(nonnull VZBScreenType *)screenType
                        onSuccess:(void (^ _Nonnull)(VZBVideoStreamInfo * _Nonnull))successCallback
                        onFailure:(void (^ _Nonnull)(NSError * _Nonnull))failureCallback {
    
    if (nil != failureCallback) {
        NSError *error = [NSError errorWithDomain:@"vizbee_rn_sender_sdk"
                                           code:100
                                       userInfo:@{
                                                   NSLocalizedDescriptionKey:@"Method not implemented"
                                       }];
        failureCallback(error);
    }
}

-(void) goToViewControllerForGUID:(NSString*) guid
                                     onSuccess:(void (^ _Nonnull)(UIViewController * _Nonnull))successCallback
                                     onFailure:(void (^ _Nonnull)(NSError * _Nonnull))failureCallback {

    if (nil != failureCallback) {
        NSError *error = [NSError errorWithDomain:@"vizbee_rn_sender_sdk"
                                           code:100
                                       userInfo:@{
                                                   NSLocalizedDescriptionKey:@"Method not implemented"
                                       }];
        failureCallback(error);
    }
    
}

-(void) playVideoOnPhone:(id) nativeVideoInfo
              atPosition:(NSTimeInterval)playHeadTime
            shouldAutoPlay:(BOOL)shouldAutoPlay
presentingViewController:(UIViewController *)viewController {
}

@end
