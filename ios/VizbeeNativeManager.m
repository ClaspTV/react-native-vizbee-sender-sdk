#import "VizbeeNativeManager.h"
#import "VizbeeVideo.h"
#import <VizbeeKit/VizbeeKit.h>
#import <React/RCTLog.h>

@interface VizbeeNativeManager()

@property (nonatomic, assign) BOOL hasListeners;
@property (nonatomic, assign) VZBSessionState lastUpdatedState;

@end

@implementation VizbeeNativeManager

RCT_EXPORT_MODULE(VizbeeNativeManager)

//-------------
#pragma mark - Constructors & Notifications
//-------------

-(instancetype) init {
    self = [super init];
    if (self != nil) {
        
        _hasListeners = NO;
        _lastUpdatedState = VZBSessionStateNoDeviceAvailable;
        
        [self initNotifications];
    };
    return self;
}

-(void) dealloc {
    [self uninitNotifications];
}

-(void) initNotifications {
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onApplicationDidBecomeActive:)
                                                 name:UIApplicationDidBecomeActiveNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onApplicationWillResignActive:)
                                                 name:UIApplicationWillResignActiveNotification
                                               object:nil];
}

-(void) uninitNotifications {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

//----------------
#pragma mark - React Observers
//----------------

-(dispatch_queue_t) methodQueue {
    return dispatch_get_main_queue();
}

+(BOOL) requiresMainQueueSetup {
    return YES;
}

-(NSArray<NSString*>*) supportedEvents {
    return @[@"VZB_SESSION_STATUS", @"OKEvent"];
}

// will be called when this module's first listener is added.
- (void)startObserving {
    self.hasListeners = YES;
}

// will be called when this module's last listener is removed, or on dealloc.
- (void)stopObserving {
    self.hasListeners = NO;
}

-(void)sendEvent:(NSString*) name
        withBody:(NSDictionary*) body {
    
    NSLog(@"RNVZBSDK: Sending event %@ with body %@", name, body);
    
    if (self.hasListeners) {
        [self sendEventWithName:name body:body];
    } else {
        NSLog(@"RNVZBSDK: Filtered event because there are no listeners!");
    }
}

//----------------
#pragma mark - Flow APIs
//----------------

RCT_EXPORT_METHOD(smartPrompt) {

    RCTLogInfo(@"Invoking smartPrompt");
    [self topViewControllerThreadSafe:^(UIViewController* vc) {
        
        if (nil == vc) {
            RCTLogError(@"SmartPrompt - nil viewcontroller");
            return;
        }
        
        // Renamed old smartHelp API to new smartPrompt
        [Vizbee smartHelp:vc];
    }];
}

RCT_EXPORT_METHOD(smartCast) {
    
    RCTLogInfo(@"Invoking smartCast");
    [self topViewControllerThreadSafe:^(UIViewController* vc) {
        
        if (nil == vc) {
            RCTLogError(@"SmartCast - nil viewcontroller");
            return;
        }
        
        VZBSessionManager* sessionManager = [Vizbee getSessionManager];
        if (nil != sessionManager) {
            [sessionManager onCastIconTapped:vc];
        }
    }];
}

RCT_EXPORT_METHOD(smartPlay:(NSDictionary*) vizbeeVideoMap
        didPlayOnTVCallback:(RCTResponseSenderBlock) didPlayOnTVCallback
      doPlayOnPhoneCallback:(RCTResponseSenderBlock) doPlayOnPhoneCallback
        ) {
    
    RCTLogInfo(@"Invoking smartPlay");
    [self topViewControllerThreadSafe:^(UIViewController* vc) {
        
        if (nil == vc) {
            RCTLogError(@"SmartPlay - nil viewcontroller");
            doPlayOnPhoneCallback(nil);
            return;
        }
            
        VizbeeVideo* vizbeeVideo = [[VizbeeVideo alloc] init:vizbeeVideoMap];
        BOOL didPlayOnTV = [Vizbee smartPlay:vizbeeVideo
                                     atPosition:(1000 * vizbeeVideo.startPositionInSeconds)
                       presentingViewController:vc];
                              
        if (didPlayOnTV) {

            RCTLogInfo(@"SmartPlay success in casting content");
            if (nil != didPlayOnTVCallback) {
                didPlayOnTVCallback(nil);
            }

        } else {

            RCTLogError(@"SmartPlay failed in casting content");
            if (nil != doPlayOnPhoneCallback){
                doPlayOnPhoneCallback(nil);
            }
        }
    }];
}

//----------------
#pragma mark - Session APIs
//----------------

RCT_REMAP_METHOD(getSessionState, getSessionStateWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    RCTLogInfo(@"Invoking getSessionState");
}

RCT_REMAP_METHOD(getSessionConnectedDevice, getSessionConnectedDeviceWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    RCTLogInfo(@"Invoking getSessionConnectedDevice");
}

//----------------
#pragma mark - Video APIs
//----------------

RCT_EXPORT_METHOD(play) {
    RCTLogInfo(@"Invoking play");
}

RCT_EXPORT_METHOD(pause) {
    RCTLogInfo(@"Invoking pause");
}

RCT_EXPORT_METHOD(seek:(double) position) {
    RCTLogInfo(@"Invoking seek");
}

RCT_EXPORT_METHOD(stop) {
    RCTLogInfo(@"Invoking stop");
}

//----------------
#pragma mark - App & session lifecycle
//----------------

-(void) onApplicationDidBecomeActive:(NSNotification*)notification {
    [self addSessionStateListener];
}

-(void) onApplicationWillResignActive:(NSNotification*)notification {
    [self removeSessionStateListener];
}

-(void) onSessionStateChanged:(VZBSessionState)newState {
    
    [self notifySessionStatus:newState];
    
    // handle videoClient
    if (newState == VZBSessionStateConnected) {
        [self addVideoStatusListener];
    } else {
        [self removeVideoStatusListener];
    }
}

-(void) addSessionStateListener {

    // sanity
    [self removeSessionStateListener];
    
    VZBSessionManager* sessionManager = [Vizbee getSessionManager];
    if (nil != sessionManager) {
        
        NSLog(@"RNVZBSDK: Adding session state listener");
        [sessionManager addSessionStateDelegate:self];

        // force first update
        [self notifySessionStatus:[sessionManager getSessionState]];
    }
}

-(void) removeSessionStateListener {

    VZBSessionManager* sessionManager = [Vizbee getSessionManager];
    if (nil != sessionManager) {

        NSLog(@"RNVZBSDK: Removing session state listener");
        [sessionManager removeSessionStateDelegate:self];
    }
}

-(void) notifySessionStatus:(VZBSessionState) newState {

    if (newState == self.lastUpdatedState) {
        NSLog(@"RNVZBSDK: Ignoring duplicate state update");
        return;
    }
    self.lastUpdatedState = newState;

    NSString* state = [self getSessionStateString:newState];
    NSMutableDictionary* stateMap = [NSMutableDictionary new];
    [stateMap setObject:state forKey:@"connectionState"];

    NSMutableDictionary* deviceMap = [self getSessionConnectedDeviceMap];
    if (nil != deviceMap) {
        [stateMap addEntriesFromDictionary:deviceMap];
    }

    NSLog(@"RNVZBSDK: Sending session status %@", stateMap);
    [self sendEvent:@"VZB_SESSION_STATUS" withBody:stateMap];
}

-(NSString*) getSessionStateString:(VZBSessionState) state {

    switch (state) {
        case VZBSessionStateNoDeviceAvailable:
            return @"NO_DEVICES_AVAILABLE";
        case VZBSessionStateNotConnected:
            return @"NOT_CONNECTED";
        case VZBSessionStateConnecting:
            return @"CONNECTING";
        case VZBSessionStateConnected:
            return @"CONNECTED";
        default:
            return @"UNKNOWN";
    }
}

-(NSMutableDictionary*) getSessionConnectedDeviceMap {
    
    VZBSessionManager* sessionManager = [Vizbee getSessionManager];
    if (nil == sessionManager) {
        return nil;
    }

    VZBSession* currentSession = [sessionManager getCurrentSession];
    if (nil == currentSession) {
        return nil;
    }

    VZBScreen* screen = currentSession.vizbeeScreen;
    if (nil == screen) {
      return nil;
    }

    NSMutableDictionary* map = [NSMutableDictionary new];
    [map setObject:screen.screenType.typeName forKey:@"connectedDeviceType"];
    [map setObject:screen.screenInfo.friendlyName forKey:@"connectedDeviceFriendlyName"];
    [map setObject:screen.screenInfo.model forKey:@"connectedDeviceModel"];
    return map;
}

//----------------
#pragma mark - Video client listener
//----------------
    
- (void)onVideoStatusUpdate:(VZBVideoStatus *)videoStatus {
    [self notifyMediaStatus:videoStatus];
}

-(VZBVideoClient*) getSessionVideoClient {

    VZBSessionManager* sessionManager = [Vizbee getSessionManager];
    if (nil == sessionManager) {
        return nil;
    }

    VZBSession* currentSession = [sessionManager getCurrentSession];
    if (nil == currentSession) {
        return nil;
    }

    VZBVideoClient* videoClient = currentSession.videoClient;
    return videoClient;
}

-(void) addVideoStatusListener {

    // sanity
    [self removeVideoStatusListener];

    NSLog(@"RNVZBSDK: Trying to add video status listener");
    VZBVideoClient* videoClient = [self getSessionVideoClient];
    if (nil != videoClient) {

        [videoClient addVideoStatusDelegate:self];
        NSLog(@"RNVZBSDK: Success adding video status listener");

        // force first update
        [self notifyMediaStatus:[videoClient getVideoStatus]];
    } else {

        NSLog(@"RNVZBSDK: Failed adding video status listener");
    }
}

-(void) removeVideoStatusListener {
    
    NSLog(@"RNVZBSDK: Trying to remove video status listener");
    VZBVideoClient* videoClient = [self getSessionVideoClient];
    if (nil != videoClient) {
        
        NSLog(@"RNVZBSDK: Success removing video status listener");
        [videoClient removeVideoStatusDelegate:self];
    }
}

-(void) notifyMediaStatus:(VZBVideoStatus*) videoStatus {

    NSLog(@"RNVZBSDK: Sending media status %@", videoStatus);
    NSDictionary* videoStatusMap = [self getVideoStatusMap:videoStatus];
    [self sendEvent:@"VZB_MEDIA_STATUS" withBody:videoStatusMap];
}

-(NSMutableDictionary*) getVideoStatusMap:(VZBVideoStatus*) videoStatus {

    NSMutableDictionary* videoStatusMap = [NSMutableDictionary new];
    [videoStatusMap setObject:videoStatus.guid forKey:@"guid"];
    [videoStatusMap setObject:[NSNumber numberWithInt:videoStatus.streamPosition] forKey:@"position"];
    [videoStatusMap setObject:[NSNumber numberWithInt:videoStatus.streamDuration] forKey:@"duration"];
    [videoStatusMap setObject:[NSNumber numberWithBool:videoStatus.isStreamLive] forKey:@"isLive"];
    [videoStatusMap setObject:[self getPlayerStateString:videoStatus.playerState] forKey:@"playerState"];
    [videoStatusMap setObject:[NSNumber numberWithBool:videoStatus.isAdPlaying] forKey:@"isAdPlaying"];
    
    return videoStatusMap;
}

-(NSString*) getPlayerStateString:(VZBVideoPlayerState) state {

    switch (state) {
        case VZBVideoPlayerStateIdle:
            return @"IDLE";
        case VZBVideoPlayerStatePlaying:
            return @"PLAYING";
        case VZBVideoPlayerStatePaused:
            return @"PAUSED";
        case VZBVideoPlayerStateBuffering:
            return @"BUFFERING";
        default:
            return @"UNKNOWN";
    }
}

//----------------
#pragma mark - Helpers
//----------------

- (UIViewController *) topViewController {
    
    UIViewController *topVC = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
    while (topVC.presentedViewController) {
        topVC = topVC.presentedViewController;
    }
    return topVC;
}

-(void) topViewControllerThreadSafe:(void (^)(UIViewController*)) completion {
    
    dispatch_async(dispatch_get_main_queue(), ^(){
    
        UIViewController *topVC = [self topViewController];
        completion(topVC);
    });
}

@end