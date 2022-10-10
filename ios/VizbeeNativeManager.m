#import "VizbeeNativeManager.h"
#import "VizbeeVideo.h"
#import <VizbeeKit/VizbeeKit.h>
#import <React/RCTLog.h>

@interface VizbeeNativeManager()

@property (nonatomic, assign) BOOL hasListeners;
@property (nonatomic, assign) VZBSessionState lastUpdatedState;

@property(nonatomic, strong) VZBCastBarViewController* castBarController;

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
                                           selector:@selector(onApplicationWillResignActive:)
                                               name:UIApplicationWillResignActiveNotification
                                             object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onApplicationDidBecomeActive:)
                                                 name:UIApplicationDidBecomeActiveNotification
                                               object:nil];
    // force first update
    [self onApplicationDidBecomeActive:nil];
    
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
    return @[@"VZB_SESSION_STATUS", @"VZB_MEDIA_STATUS"];
}

// will be called when this module's first listener is added.
- (void)startObserving {
    RCTLogInfo(@"RNVZBSDK: start observing invoked");
    self.hasListeners = YES;
}

// will be called when this module's last listener is removed, or on dealloc.
- (void)stopObserving {
    RCTLogInfo(@"RNVZBSDK: stop observing invoked");
    self.hasListeners = NO;
}

-(void)sendEvent:(NSString*) name
        withBody:(NSDictionary*) body {
    
    RCTLogInfo(@"RNVZBSDK: Sending event %@ with body %@", name, body);
    
    if (self.hasListeners) {
        [self sendEventWithName:name body:body];
    } else {
        RCTLogInfo(@"RNVZBSDK: Filtered event because there are no listeners!");
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
                                     atPosition:vizbeeVideo.startPositionInSeconds
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
  
    VZBSessionManager* sessionManager = [Vizbee getSessionManager];
    if (nil == sessionManager) {
          reject(@"No session manager", @"Session manager is nil", nil);
          return;
    }

    resolve([self getSessionStateString:[sessionManager getSessionState]]);
}

RCT_REMAP_METHOD(getSessionConnectedDevice, getSessionConnectedDeviceWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    RCTLogInfo(@"Invoking getSessionConnectedDevice");
  
    VZBSessionManager* sessionManager = [Vizbee getSessionManager];
    if (nil == sessionManager) {
          reject(@"No session manager", @"Session manager is nil", nil);
          return;
    }
  
    NSDictionary* map = [self getSessionConnectedDeviceMap];
    RCTLogInfo(@"getSessionConnectedDevice %@", map);
    resolve(map);
}

RCT_EXPORT_METHOD(disconnect) {
    RCTLogInfo(@"Invoking disconnect");

    [[Vizbee getSessionManager] disconnectSession];
}

//----------------
#pragma mark - Video APIs
//----------------

RCT_EXPORT_METHOD(play) {

    VZBVideoClient* videoClient = [self getSessionVideoClient];
    if (nil != videoClient) {
        [videoClient play];
    } else {
        RCTLogWarn(@"Play ignored because videoClient is null");
    }
}

RCT_EXPORT_METHOD(pause) {
  
    VZBVideoClient* videoClient = [self getSessionVideoClient];
    if (nil != videoClient) {
        [videoClient pause];
    } else {
        RCTLogWarn(@"Pause ignored because videoClient is null");
    }
}

RCT_EXPORT_METHOD(seek:(double) position) {
  
    VZBVideoClient* videoClient = [self getSessionVideoClient];
    if (nil != videoClient) {
        [videoClient seek:position];
    } else {
        RCTLogWarn(@"Seek ignored because videoClient is null");
    }
}

RCT_EXPORT_METHOD(stop) {
  
    VZBVideoClient* videoClient = [self getSessionVideoClient];
    if (nil != videoClient) {
        [videoClient stop];
    } else {
        RCTLogWarn(@"Stop ignored because videoClient is null");
    }
}

//----------------
#pragma mark - MiniCastController APIs
//----------------

RCT_EXPORT_METHOD(addMiniCastController:(int) bottomMargin height:(int) height) {

    RCTLogInfo(@"Invoking addMiniCastController");
    [self topViewControllerThreadSafe:^(UIViewController* vc) {
        
        if (nil == vc) {
            RCTLogError(@"addMiniCastController - nil viewcontroller");
            return;
        }
        if (nil != self.castBarController) {
            RCTLogWarn(@"addMiniCastController - mini cast controller already added");
            return;
        }
        
        int yPosition = vc.view.frame.size.height - (bottomMargin + height + vc.view.safeAreaInsets.bottom);
        CGRect frame = CGRectMake(
                            0, 
                            yPosition, 
                            vc.view.frame.size.width, 
                            height);
        self.castBarController = [Vizbee createCastBarController];
        self.castBarController.delegate = self;
        self.castBarController.view.frame = frame;
        
        [vc addChildViewController:self.castBarController];
        [vc.view addSubview:self.castBarController.view];
    }];
}

RCT_EXPORT_METHOD(setMiniCastControllerBackgroundColor:(NSString*) backgroundColor ) {
    RCTLogInfo(@"setMiniCastControllerBackgroundColor");
}

RCT_EXPORT_METHOD(setMiniCastControllerPlaybackButtonColor:(NSString*) buttonColor) {
    RCTLogInfo(@"setMiniCastControllerPlaybackButtonColor");
}

RCT_EXPORT_METHOD(showMiniCastController) {
  
    RCTLogInfo(@"Invoking showMiniCastController");
    self.castBarController.view.hidden = NO;
}

RCT_EXPORT_METHOD(hideMiniCastController) {
  
    RCTLogInfo(@"Invoking hideMiniCastController");
    self.castBarController.view.hidden = YES;
}

-(void)miniCastViewController:(VZBCastBarViewController*) miniCastViewController
                  shouldAppear:(BOOL) shouldAppear {
    self.castBarController.view.hidden = !shouldAppear;
}

//----------------
#pragma mark - App & session lifecycle
//----------------

-(void) onApplicationDidBecomeActive:(NSNotification*)notification {
    RCTLogInfo(@"onApplicationDidBecomeActive - adding session state listener");
    [self addSessionStateListener];
}

-(void) onApplicationWillResignActive:(NSNotification*)notification {
    RCTLogInfo(@"onApplicationWillResignActive - removing session state listener");
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
        
        RCTLogInfo(@"RNVZBSDK: Adding session state listener");
        [sessionManager addSessionStateDelegate:self];

        // force first update
        [self notifySessionStatus:[sessionManager getSessionState]];
    }
}

-(void) removeSessionStateListener {

    VZBSessionManager* sessionManager = [Vizbee getSessionManager];
    if (nil != sessionManager) {

        RCTLogInfo(@"RNVZBSDK: Removing session state listener");
        [sessionManager removeSessionStateDelegate:self];
    }
}

-(void) notifySessionStatus:(VZBSessionState) newState {

    if (newState == self.lastUpdatedState) {
        RCTLogInfo(@"RNVZBSDK: Ignoring duplicate state update");
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

    RCTLogInfo(@"RNVZBSDK: Sending session status %@", stateMap);
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

    RCTLogInfo(@"RNVZBSDK: Trying to add video status listener");
    VZBVideoClient* videoClient = [self getSessionVideoClient];
    if (nil != videoClient) {

        [videoClient addVideoStatusDelegate:self];
        RCTLogInfo(@"RNVZBSDK: Success adding video status listener");

        // force first update
        [self notifyMediaStatus:[videoClient getVideoStatus]];
    } else {

        RCTLogInfo(@"RNVZBSDK: Failed adding video status listener");
    }
}

-(void) removeVideoStatusListener {
    
    RCTLogInfo(@"RNVZBSDK: Trying to remove video status listener");
    VZBVideoClient* videoClient = [self getSessionVideoClient];
    if (nil != videoClient) {
        
        RCTLogInfo(@"RNVZBSDK: Success removing video status listener");
        [videoClient removeVideoStatusDelegate:self];
    }
}

-(void) notifyMediaStatus:(VZBVideoStatus*) videoStatus {

    RCTLogInfo(@"RNVZBSDK: Sending media status %@", videoStatus);
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
