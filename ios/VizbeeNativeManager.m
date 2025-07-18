#import "VizbeeNativeManager.h"
#import "VizbeeConstants.h"
#import "VizbeeVideo.h"
#import "VizbeeUICardConfiguration.h"

#import <React/RCTLog.h>

@interface VizbeeNativeManager()

@property (nonatomic, assign) BOOL hasListeners;
@property (nonatomic, assign) VZBSessionState lastUpdatedState;

@property(nonatomic, strong) VZBCastIconProxy* castIconProxy;

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
    [self removeListeners];
    [self uninitNotifications];
}

-(void) initNotifications {
    
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::initNotifications - Registering for SDK initialization notification");
    
    // Adding listeners when the app becomes active and removing them when it resigns 
    // causes issues if SDK initialization is delayed due to a feature flag or other reasons, 
    // as SessionManager or AnalyticsManager may not be available. 
    // The correct approach is to add listeners when the SDK is initialized.
    [[NSNotificationCenter defaultCenter] addObserver:self 
                                                selector:@selector(onVizbeeSDKInitialized:) 
                                                    name:@"VizbeeSDKInitialized" 
                                                object:nil];

    // Call addListeners here because the SDK might have been initialized 
    // before the module was created and we might have missed the notification
    [self addListeners];
    
}

-(void) uninitNotifications {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)onVizbeeSDKInitialized:(NSNotification *)notification {
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::onVizbeeSDKInitialized - Received SDK initialization notification");
    [self addListeners];
}

// ----------------------------
# pragma mark - Listeners
// ----------------------------

-(void) addListeners {

    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::addListeners - Adding listeners");

    [self addSessionStateListener];
    [self addCastIconStateListener];
    [self addAnalyticsListener];
}

-(void) removeListeners {

    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::removeListeners - Removing listeners");

    [self removeSessionStateListener];
    [self removeCastIconStateListener];
    [self removeAnalyticsListener];
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
    return @[@"VZB_SESSION_STATUS", VZB_CASTICON_STATE, @"VZB_ANALYTICS_EVENT", VZB_INVOKE_GET_SIGNIN_INFO, @"VZB_MEDIA_STATUS", @"VZB_VOLUME_STATUS", @"VZB_EVENT"];
}

// will be called when this module's first listener is added.
- (void)startObserving {
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::startObserving - invoked");
    self.hasListeners = YES;
}

// will be called when this module's last listener is removed, or on dealloc.
- (void)stopObserving {
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::stopObserving - invoked");
    self.hasListeners = NO;
}

-(void)sendEvent:(NSString*) name
        withBody:(NSDictionary*) body {
    
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::sendEvent - Sending event %@ with body %@", name, body);
    
    if (self.hasListeners) {
        [self sendEventWithName:name body:body];
    } else {
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::sendEvent - Filtered event because there are no listeners!");
    }
}

//----------------
#pragma mark - Flow APIs
//----------------

RCT_EXPORT_METHOD(smartPrompt) {

    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::smartPrompt - Invoking smartPrompt");
    [self topViewControllerThreadSafe:^(UIViewController* vc) {
        
        if (nil == vc) {
            RCTLogError(@"[RNVZBSDK] VizbeeNativeManager::smartPrompt - nil viewcontroller");
            return;
        }
        
        // Renamed old smartHelp API to new smartPrompt
        [Vizbee smartHelp:vc];
    }];
}

RCT_EXPORT_METHOD(smartPromptWithOptions:(NSDictionary*) smartPromptOptions) {

    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::smartPromptWithOptions - Invoking smartPrompt with options %@", smartPromptOptions);
    [self topViewControllerThreadSafe:^(UIViewController* vc) {
        
        if (nil == vc) {
            RCTLogError(@"[RNVZBSDK] VizbeeNativeManager::smartPromptWithOptions - nil viewcontroller");
            return;
        }
        
        VZBSmartHelpOptions* smartHelpOptions = [VZBSmartHelpOptions new];
        smartHelpOptions.enabledSubflows = [[smartPromptOptions objectForKey:@"enabledSubflows"]intValue];
        [Vizbee smartHelpWithOptions:smartHelpOptions presentingViewController:vc];
    }];
}

RCT_EXPORT_METHOD(smartCast) {
    
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::smartCast - Invoking smartCast");
    [self topViewControllerThreadSafe:^(UIViewController* vc) {
        
        if (nil == vc) {
            RCTLogError(@"[RNVZBSDK] VizbeeNativeManager::smartCast - nil viewcontroller");
            return;
        }
        
        VZBSessionManager* sessionManager = [Vizbee getSessionManager];
        if (nil != sessionManager) {
            [sessionManager onCastIconTapped:vc];
        }
    }];
}

RCT_EXPORT_METHOD(smartPlay:(NSDictionary*) vizbeeVideoMap
                smartPlayOptions:(NSDictionary*) smartPlayOptionsMap
                didPlayOnTVCallback:(RCTResponseSenderBlock) didPlayOnTVCallback
                doPlayOnPhoneCallback:(RCTResponseSenderBlock) doPlayOnPhoneCallback) {
    
    __block RCTResponseSenderBlock _didPlayOnTVCallback = didPlayOnTVCallback;
    __block RCTResponseSenderBlock _doPlayOnPhoneCallback = doPlayOnPhoneCallback;
    
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::smartPlay - Invoking smartPlay");
    [self topViewControllerThreadSafe:^(UIViewController* vc) {
        
        // sanity
        if (nil == vc) {
            RCTLogError(@"[RNVZBSDK] VizbeeNativeManager::smartPlay - nil viewcontroller");
            if (nil != _doPlayOnPhoneCallback) {
                _doPlayOnPhoneCallback(@[@"FAILED_TO_GET_VIEW_CONTROLLER"]);
                _doPlayOnPhoneCallback = nil;
            }
            return;
        }
        
        // create VZBRequest from the app video object
        VizbeeVideo* vizbeeVideo = [[VizbeeVideo alloc] init:vizbeeVideoMap];
        VZBRequest* request = [[VZBRequest alloc] initWithAppVideo:vizbeeVideo
                                                    GUID:vizbeeVideo.guid
                                                    startPosition:vizbeeVideo.startPositionInSeconds];
        [request didPlayOnTV:^(VZBScreen *screen) {

            NSDictionary* connectedDeviceMap = [self getSessionConnectedDeviceMap];
            RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::smartPlay - Playing on screen = %@", connectedDeviceMap[@"connectedDeviceFriendlyName"]);
            if (nil != _didPlayOnTVCallback) {
                _didPlayOnTVCallback(@[connectedDeviceMap]);
                _didPlayOnTVCallback = nil;
            }
        }];
        [request doPlayOnPhone:^(VZBStatus *status) {

            NSString* reasonForPlayOnPhone = [self getPlayOnPhoneReason:status];
            RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::smartPlay - Play on phone with reason = %@", reasonForPlayOnPhone);
                if (nil != _doPlayOnPhoneCallback){
                    _doPlayOnPhoneCallback(@[reasonForPlayOnPhone]);
                    _doPlayOnPhoneCallback = nil;
                }
        }];
        
        // Create and initialize SmartPlayOptions from dictionary
        if (nil != smartPlayOptionsMap) {
            VZBSmartPlayOptions* options = [self getSmartPlayOptions:smartPlayOptionsMap];
            RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::smartPlay - smartPlayOptions = %@", options);
            [Vizbee smartPlay:request withOptions:options presentingViewController:vc];
        } else {
            [Vizbee smartPlay:request presentingViewController:vc];
        }
    }];
}

-(VZBSmartPlayOptions*) getSmartPlayOptions:(NSDictionary*) smartPlayOptions {
    VZBSmartPlayOptions* options = [VZBSmartPlayOptions new];
    if (smartPlayOptions && [smartPlayOptions isKindOfClass:[NSDictionary class]]) {
        if (smartPlayOptions[@"isFromSmartNotification"]) {
            options.isFromSmartNotification = [smartPlayOptions[@"isFromSmartNotification"] boolValue];
        }
        
        if (smartPlayOptions[@"smartPlayCardVisibility"]) {
            options.smartPlayCardVisibility = [smartPlayOptions[@"smartPlayCardVisibility"] intValue];
        }
    }
    return options;
}

//----------------
#pragma mark - Session APIs
//----------------

RCT_REMAP_METHOD(getSessionState, getSessionStateWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::getSessionState - Invoking getSessionState");
  
    VZBSessionManager* sessionManager = [Vizbee getSessionManager];
    if (nil == sessionManager) {
          reject(@"No session manager", @"Session manager is nil", nil);
          return;
    }

    resolve([self getSessionStateString:[sessionManager getSessionState]]);
}

RCT_REMAP_METHOD(getSessionConnectedDevice, getSessionConnectedDeviceWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::getSessionConnectedDevice - Invoking getSessionConnectedDevice");
  
    VZBSessionManager* sessionManager = [Vizbee getSessionManager];
    if (nil == sessionManager) {
          reject(@"No session manager", @"Session manager is nil", nil);
          return;
    }
  
    NSDictionary* map = [self getSessionConnectedDeviceMap];
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::getSessionConnectedDevice - %@", map);
    resolve(map);
}

RCT_EXPORT_METHOD(disconnect) {
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::disconnect - invoking disconnect");

    [[Vizbee getSessionManager] disconnectSession];
}

//----------------
#pragma mark - Cast Icon State APIs
//----------------

RCT_REMAP_METHOD(getCastIconState, getCastIconStateWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::getCastIconState - Invoking getCastIconState");
  
    if (nil == self.castIconProxy) {
        self.castIconProxy = [Vizbee getCastIconProxy];
    }
    if (nil == self.castIconProxy) {
          reject(@"No castIconProxy", @"castIconProxy is nil", nil);
          return;
    }

    resolve([self getCastIconStateString:[self.castIconProxy getCastState]]);
}

//----------------
#pragma mark - Event APIs
//----------------

RCT_EXPORT_METHOD(registerForEvent:(NSString *)eventName) {
    if (nil == eventName) {
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::registerForEvent - ignored because eventName is nil");
        return;
    }

    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::registerForEvent - invoking with name: %@", eventName);

    VZBEventManager* eventManager = [self getEventManager];
    if (nil == eventManager) {
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::registerForEvent - no event manager, event manager is nil");
        return;
    }
    
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::registerForEvent - invoking registerEventWithName");
    [eventManager registerForEvent:eventName eventHandler:self];
}

RCT_EXPORT_METHOD(unregisterForEvent:(NSString *)eventName) {
    if (nil == eventName) {
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::unregisterForEvent - ignored because eventName is nil");
        return;
    }

    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::unregisterForEvent - invoking with name: %@", eventName);
    
    VZBEventManager* eventManager = [self getEventManager];
    if (nil == eventManager) {
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::unregisterForEvent - no event manager, event manager is nil");
        return;
    }
    
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::unregisterForEvent - invoking unregisterEventWithName");
    [eventManager unregisterForEvent:eventName eventHandler:self];
}

RCT_EXPORT_METHOD(sendEvent:(NSString *)eventName 
                    data:(NSDictionary *)eventData) {

    if (nil == eventName || [eventName length] == 0) {
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::sendEvent - ignored because eventName is nil");
        return;
    }
    if (nil == eventData || [eventData count] == 0) {
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::sendEvent - ignored because eventData is nil");
        return;
    }

    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::sendEvent - invoking with name: %@ data: %@", 
               eventName, eventData);
               
    
    VZBEventManager* eventManager = [self getEventManager];
    if (nil == eventManager) {
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::unregisterForEvent - no event manager, event manager is nil");
        return;
    }
    
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::sendEvent - invoking sendEventWithName");
    [eventManager sendEventWithName:eventName andData:eventData];
}

-(void) onEvent:(VZBEvent *)event {
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::onEvent - invoking with name: %@ data: %@", 
               event.name, event.data);
    
    NSMutableDictionary* eventMap = [NSMutableDictionary new];
    [eventMap setValue:event.name forKey:@"eventName"];
    [eventMap setValue:event.data forKey:@"eventData"];
    
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::onEvent - Sending event %@", eventMap);
    [self sendEvent:@"VZB_EVENT" withBody:eventMap];
}

-(VZBEventManager*) getEventManager {
    VZBSessionManager* sessionManager = [Vizbee getSessionManager];
    if (nil == sessionManager) {
        return nil;
    }
    
    VZBSession* currentSession = [sessionManager getCurrentSession];
    if (nil == currentSession) {
        return nil;
    }
    
    return currentSession.eventManager;
}

//----------------
#pragma mark - Signin APIs
//----------------

RCT_EXPORT_METHOD(onGetSignInInfo:(NSDictionary*) signInInfo) {
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::onGetSignInInfo - invoking onGetSignInInfo");

    VZBSessionManager* sessionManager = [Vizbee getSessionManager];
    if (nil == sessionManager) {
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::onGetSignInInfo - no session manager, session manager is nil");
        return;
    }

    VZBSession* currentSession = [sessionManager getCurrentSession];
    if (nil == currentSession) {
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::onGetSignInInfo - no current session, current session is nil");
        return;
    }

    NSMutableDictionary* authInfo = [NSMutableDictionary new];
    [authInfo setValue:signInInfo forKey:@"authInfo"];

    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::onGetSignInInfo - invoking sendEventWithName with authInfo = %@", authInfo);
    [currentSession sendEventWithName:VZB_SIGNIN_EVENT andData:authInfo];
}

//----------------
#pragma mark - Video APIs
//----------------

RCT_EXPORT_METHOD(play) {

    VZBVideoClient* videoClient = [self getSessionVideoClient];
    if (nil != videoClient) {
        [videoClient play];
    } else {
        RCTLogWarn(@"[RNVZBSDK] VizbeeNativeManager::play - ignored because videoClient is null");
    }
}

RCT_EXPORT_METHOD(pause) {
  
    VZBVideoClient* videoClient = [self getSessionVideoClient];
    if (nil != videoClient) {
        [videoClient pause];
    } else {
        RCTLogWarn(@"[RNVZBSDK] VizbeeNativeManager::pause - ignored because videoClient is null");
    }
}

RCT_EXPORT_METHOD(seek:(double) position) {
  
    VZBVideoClient* videoClient = [self getSessionVideoClient];
    if (nil != videoClient) {
        [videoClient seek:position/1000];
    } else {
        RCTLogWarn(@"[RNVZBSDK] VizbeeNativeManager::seek - ignored because videoClient is null");
    }
}

RCT_EXPORT_METHOD(stop) {
  
    VZBVideoClient* videoClient = [self getSessionVideoClient];
    if (nil != videoClient) {
        [videoClient stop];
    } else {
        RCTLogWarn(@"[RNVZBSDK] VizbeeNativeManager::stop - ignored because videoClient is null");
    }
}

RCT_EXPORT_METHOD(setActiveTrack:(NSDictionary *) track) {

    VZBVideoTrackInfo* trackInfo = [VZBVideoTrackInfo new];
    trackInfo.identifier = [[track objectForKey:@"identifier"] intValue];
    trackInfo.contentIdentifier = [track objectForKey:@"contentIdentifier"];
    trackInfo.contentType = [track objectForKey:@"contentType"];
    trackInfo.name = [track objectForKey:@"name"];
    trackInfo.languageCode = [track objectForKey:@"languageCode"];
    NSMutableArray* tracks = [NSMutableArray new];
    [tracks addObject:trackInfo];
  
    VZBVideoClient* videoClient = [self getSessionVideoClient];
    if (nil != videoClient) {
        [videoClient setActiveTracks:tracks];
    } else {
        RCTLogWarn(@"[RNVZBSDK] VizbeeNativeManager::setActiveTrack - ignored because videoClient is null");
    }
}

RCT_EXPORT_METHOD(resetActiveTrack) {

    // empty array
    NSMutableArray* tracks = [NSMutableArray new];
    VZBVideoClient* videoClient = [self getSessionVideoClient];
    if (nil != videoClient) {
        [videoClient setActiveTracks:tracks];
    } else {
        RCTLogWarn(@"[RNVZBSDK] VizbeeNativeManager::resetActiveTrack - ignored because videoClient is null");
    }
}

RCT_EXPORT_METHOD(supportsVolumeControl:(RCTResponseSenderBlock) callback) {
  
    VZBVolumeClient* volumeClient = [self getSessionVolumeClient];
    if (nil != volumeClient) {
        if (nil != callback) {
            callback(@[@(volumeClient.supportsVolumeControl)]);
        } else {
            RCTLogWarn(@"[RNVZBSDK] VizbeeNativeManager::supportsVolumeControl - ignored because callback is null");
        }
    } else {
        RCTLogWarn(@"[RNVZBSDK] VizbeeNativeManager::supportsVolumeControl - ignored because volumeClient is null");
    }
}

RCT_EXPORT_METHOD(setVolume:(float) volume) {
  
    VZBVolumeClient* volumeClient = [self getSessionVolumeClient];
    if (nil != volumeClient) {
        [volumeClient setVolume:volume];
    } else {
        RCTLogWarn(@"[RNVZBSDK] VizbeeNativeManager::setVolume - ignored because volumeClient is null");
    }
}

RCT_EXPORT_METHOD(getVolume:(RCTResponseSenderBlock) volumeCallback) {
  
    VZBVolumeClient* volumeClient = [self getSessionVolumeClient];
    if (nil != volumeClient) {
        if (nil != volumeCallback) {
            volumeCallback(@[[self getVolumeStatusMap:volumeClient]]);
        } else {
            RCTLogWarn(@"[RNVZBSDK] VizbeeNativeManager::getVolume - ignored because volumeCallback is null");
        }
    } else {
        RCTLogWarn(@"[RNVZBSDK] VizbeeNativeManager::getVolume - ignored because volumeClient is null");
    }
}

RCT_EXPORT_METHOD(mute) {
  
    VZBVolumeClient* volumeClient = [self getSessionVolumeClient];
    if (nil != volumeClient) {
        [volumeClient setMute:YES];
    } else {
        RCTLogWarn(@"[RNVZBSDK] VizbeeNativeManager::mute - ignored because volumeClient is null");
    }
}

RCT_EXPORT_METHOD(unmute) {
  
    VZBVolumeClient* volumeClient = [self getSessionVolumeClient];
    if (nil != volumeClient) {
        [volumeClient setMute:NO];
    } else {
        RCTLogWarn(@"[RNVZBSDK] VizbeeNativeManager::unmute - ignored because volumeClient is null");
    }
}

// ----------------------------
# pragma mark - UI
// ----------------------------
RCT_EXPORT_METHOD(setUICardConfiguration:(NSDictionary*) cardConfigurationMap forCardType:(NSString*) cardTypeString) {

    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::setUICardConfiguration - cardConfigurationMap %@ cardType %@", cardConfigurationMap, cardTypeString);

    VizbeeUICardConfiguration* uiCardConfiguration = [[VizbeeUICardConfiguration alloc] init:cardConfigurationMap];
    VZBCardConfiguration* cardConfiguration = [uiCardConfiguration getCardConfigurationForType:cardTypeString];
    VZBUICardType cardType = [self getCardType:cardTypeString];
    if (nil != cardConfiguration && cardType != -1) {
        VZBUIConfiguration* uiConfiguration = [Vizbee getUIConfiguration];
        [uiConfiguration setCardConfiguration:cardConfiguration forCardType:cardType];
    } else {
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::setUICardConfiguration - received card configuration for unknown card type %@", cardType);
    }
}

RCT_EXPORT_METHOD(removeUICardConfiguration:(NSString*) cardType) {
    VZBUICardType uiCardType = [self getCardType:cardType];
    if (uiCardType == -1) {
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::removeCardConfiguration - received unknown card type");
        return;
    }

    VZBUIConfiguration* uiConfiguration = [Vizbee getUIConfiguration];
    [uiConfiguration removeCardConfiguration:uiCardType];
}

-(VZBUICardType) getCardType:(NSString*) cardType {

    if ([cardType isEqualToString:@"CAST_AUTHORIZATION"]) {
        return VZBUICardTypeCastAuthorization;
    } else if ([cardType isEqualToString:@"CAST_INTRODUCTION"]) {
        return VZBUICardTypeCastIntroduction;
    } else if ([cardType isEqualToString:@"SMART_INSTALL"]) {
        return VZBUICardTypeSmartInstall;
    } else if ([cardType isEqualToString:@"GUIDED_SMART_INSTALL"]) {
        return VZBUICardTypeGuidedSmartInstall;
    } else if ([cardType isEqualToString:@"MULTI_DEVICE_SMART_INSTALL"]) {
        return VZBUICardTypeMultiDeviceSmartInstall;
    }

    return -1; // unknown
}

// ----------------------------
# pragma mark - Analytics
// ----------------------------

RCT_EXPORT_METHOD(addAnalyticsAttributes:(NSDictionary*) attributes) {
    [Vizbee addCustomEventAttributes:attributes];
}

//----------------
#pragma mark - session lifecycle
//----------------

-(void) onSessionStateChanged:(VZBSessionState)newState {
    
    [self notifySessionStatus:newState];
    
    // handle videoClient/volumeClient
    if (newState == VZBSessionStateConnected) {
        [self addVideoStatusListener];
        [self addVolumeStatusListener];

        NSMutableDictionary* dummySignInInfoMap = [NSMutableDictionary new];
        [self sendEvent:VZB_INVOKE_GET_SIGNIN_INFO withBody:dummySignInInfoMap];
    } else {
        [self removeVideoStatusListener];
        [self removeVolumeStatusListener];
    }
}

-(void) addSessionStateListener {

    // sanity
    [self removeSessionStateListener];
    
    VZBSessionManager* sessionManager = [Vizbee getSessionManager];
    if (nil != sessionManager) {
        
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::addSessionStateListener - Adding session state listener");
        [sessionManager addSessionStateDelegate:self];

        // force first update
        [self notifySessionStatus:[sessionManager getSessionState]];
    }
}

-(void) removeSessionStateListener {

    VZBSessionManager* sessionManager = [Vizbee getSessionManager];
    if (nil != sessionManager) {

        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::removeSessionStateListener - Removing session state listener");
        [sessionManager removeSessionStateDelegate:self];
    }
}

-(void) notifySessionStatus:(VZBSessionState) newState {

    if (newState == self.lastUpdatedState) {
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::notifySessionStatus - Ignoring duplicate state update");
        return;
    }
    self.lastUpdatedState = newState;

    NSString* state = [self getSessionStateString:newState];
    NSMutableDictionary* stateMap = [NSMutableDictionary new];
    [stateMap setValue:state forKey:@"connectionState"];

    NSMutableDictionary* deviceMap = [self getSessionConnectedDeviceMap];
    if (nil != deviceMap) {
        [stateMap addEntriesFromDictionary:deviceMap];
    }

    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::notifySessionStatus - Sending session status %@", stateMap);
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
    [map setValue:screen.screenType.typeName forKey:@"connectedDeviceType"];
    [map setValue:screen.screenInfo.friendlyName forKey:@"connectedDeviceFriendlyName"];
    [map setValue:screen.screenInfo.model forKey:@"connectedDeviceModel"];
    return map;
}

//----------------
#pragma mark - Cast icon listener
//----------------

-(void) addCastIconStateListener {
    if (nil == self.castIconProxy) {
        self.castIconProxy = [Vizbee getCastIconProxy];
    }

    if (nil != self.castIconProxy) {
        [self.castIconProxy addStateChangeListener:self];
        
        // force first update
        [self notifyCastIconState:[self.castIconProxy getCastState]];
    }
}

-(void) removeCastIconStateListener {
    if (nil != self.castIconProxy) {
        [self.castIconProxy removeStateChangeListener:self];
    }
}

-(void) onStateChange:(VZBCastingState) state {
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::onCastIconStateChanged - Cast icon state changed to %lu", (unsigned long)state);

    [self notifyCastIconState:state];
}

-(void) notifyCastIconState:(VZBCastingState) state {
    NSString* stateString = [self getCastIconStateString:state];
    NSMutableDictionary* stateMap = [NSMutableDictionary new];
    [stateMap setValue:stateString forKey:@"castIconState"];

    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::notifyCastIconState - Sending cast icon state %@", stateMap);
    [self sendEvent:VZB_CASTICON_STATE withBody:stateMap];
}

-(NSString*) getCastIconStateString:(VZBCastingState) state {
    switch (state) {
        case VZBCastingStateUnavailable:
        case VZBCastingStateScanning:
            return @"UNAVAILABLE";
        case VZBCastingStateDisconnected:
            return @"DISCONNECTED";
        case VZBCastingStateConnecting:
            return @"CONNECTING";
        case VZBCastingStateConnected:
            return @"CONNECTED";
        case VZBCastingStateDeactivated:
            return @"DEACTIVATED";
        default:
            return @"UNKNOWN";
    }
}

//----------------
#pragma mark - Analytics listener
//----------------

-(void) addAnalyticsListener {

    // sanity
    [self removeAnalyticsListener];
    
    VZBAnalyticsManager* analyticsManager = [Vizbee getAnalyticsManager];
    if (nil != analyticsManager) {
        
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::addAnalyticsListener - Adding analytics listener");
        [analyticsManager addAnalyticsDelegate:self];
    } else {
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::addAnalyticsListener - Failed to add analytics listener, analyticsManager is nil");
    }
}

-(void) removeAnalyticsListener {

    VZBAnalyticsManager* analyticsManager = [Vizbee getAnalyticsManager];
    if (nil != analyticsManager) {
        
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::removeAnalyticsListener - Removing analytics listener");
        [analyticsManager removeAnalyticsDelegate:self];
    } else {
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::removeAnalyticsListener - Failed to remove analytics listener, analyticsManager is nil");
    }
}

// ----------------------------
# pragma mark - VZBAnalyticsDelegate
// ----------------------------

-(void) onAnalyticsEvent:(VZBAnalyticsEventType) event withAttrs:(NSDictionary*) attrs {

    NSString* eventName = [self getAnalyticsEventString:event];
    NSMutableDictionary* eventMap = [NSMutableDictionary new];
    [eventMap setValue:eventName forKey:@"event"];

    if (nil != attrs) {
        [eventMap setValue:attrs forKey:@"properties"];
    }

    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::onAnalyticsEvent - Sending event %@", eventMap);
    [self sendEvent:@"VZB_ANALYTICS_EVENT" withBody:eventMap];
}

-(NSString*) getAnalyticsEventString:(VZBAnalyticsEventType) event {

    switch(event) {
            
        case VZBAnalyticsEventTypeCastIntroductionCardShown:
            return @"CAST_INTRODUCTION_CARD_SHOWN";
        case VZBAnalyticsEventTypeSmartInstallCardShown:
            return @"SMART_INSTALL_CARD_SHOWN";
        case VZBAnalyticsEventTypeCastIconDeviceSelectionCardShown:
            return @"CAST_ICON_DEVICE_SELECTION_CARD_SHOWN";
        case VZBAnalyticsEventTypeSmartPlayDeviceSelectionCardShown:
            return @"SMART_PLAY_DEVICE_SELECTION_CARD_SHOWN";
        case VZBAnalyticsEventTypeSmartNotificationDeviceSelectionCardShown:
            return @"SMART_NOTIFICATION_DEVICE_SELECTION_CARD_SHOWN";
        case VZBAnalyticsEventTypeScreenDeviceSelected:
            return @"SCREEN_DEVICE_SELECTED";
        case VZBAnalyticsEventTypePlayOnPhoneSelected:
            return @"PLAY_ON_PHONE_SELECTED";
        case VZBAnalyticsEventTypePlayOnTVSelected:
            return @"PLAY_ON_TV_SELECTED";
        case VZBAnalyticsEventTypeCardDismissed:
            return @"CARD_DISMISSED";
        default:
            return @"UNKNOWN";
    }
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

    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::addVideoStatusListener - Trying to add video status listener");
    VZBVideoClient* videoClient = [self getSessionVideoClient];
    if (nil != videoClient) {

        [videoClient addVideoStatusDelegate:self];
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::addVideoStatusListener - Success adding video status listener");

        // force first update
        [self notifyMediaStatus:[videoClient getVideoStatus]];
    } else {

        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::addVideoStatusListener - Failed adding video status listener");
    }
}

-(void) removeVideoStatusListener {
    
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::removeVideoStatusListener - Trying to remove video status listener");
    VZBVideoClient* videoClient = [self getSessionVideoClient];
    if (nil != videoClient) {
        
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::removeVideoStatusListener - Success removing video status listener");
        [videoClient removeVideoStatusDelegate:self];
    }
}

-(void) notifyMediaStatus:(VZBVideoStatus*) videoStatus {

    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::notifyMediaStatus - Sending media status %@", videoStatus);
    NSDictionary* videoStatusMap = [self getVideoStatusMap:videoStatus];
    [self sendEvent:@"VZB_MEDIA_STATUS" withBody:videoStatusMap];
}

-(NSMutableDictionary*) getVideoStatusMap:(VZBVideoStatus*) videoStatus {

    NSMutableDictionary* videoStatusMap = [NSMutableDictionary new];
    [videoStatusMap setValue:videoStatus.guid forKey:@"guid"];

    [videoStatusMap setValue:videoStatus.title forKey:@"title"];
    [videoStatusMap setValue:videoStatus.subTitle forKey:@"subTitle"];
    [videoStatusMap setValue:videoStatus.imageURL forKey:@"imageURL"];

    [videoStatusMap setValue:[self getPlayerStateString:videoStatus.playerState] forKey:@"playerState"];
    [videoStatusMap setValue:[NSNumber numberWithBool:videoStatus.isStreamLive] forKey:@"isLive"];
    [videoStatusMap setValue:[NSNumber numberWithInt:videoStatus.streamPosition * 1000] forKey:@"position"];
    [videoStatusMap setValue:[NSNumber numberWithInt:videoStatus.streamDuration * 1000] forKey:@"duration"];
    [videoStatusMap setValue:[NSNumber numberWithInt:videoStatus.streamPosition * 1000] forKey:@"streamPosition"];
    [videoStatusMap setValue:[NSNumber numberWithInt:videoStatus.streamDuration * 1000] forKey:@"streamDuration"];

    [videoStatusMap setValue:[NSNumber numberWithBool:videoStatus.isAdPlaying] forKey:@"isAdPlaying"];
    [videoStatusMap setValue:[NSNumber numberWithInt:videoStatus.adPosition] forKey:@"adPosition"];
    [videoStatusMap setValue:[NSNumber numberWithInt:videoStatus.adDuration] forKey:@"adDuration"];

    [videoStatusMap setValue:[self getTrackStatusMap:videoStatus.trackStatus] forKey:@"trackStatus"];
    
    return videoStatusMap;
}

-(NSString*) getPlayerStateString:(VZBVideoPlayerState) state {

    switch (state) {
        case VZBVideoPlayerStateIdle:
            return @"Idle";
        case VZBVideoPlayerStateStarted:
            return @"Started";
        case VZBVideoPlayerStatePlaying:
            return @"Playing";
        case VZBVideoPlayerStatePaused:
            return @"Paused";
        case VZBVideoPlayerStateBuffering:
            return @"Buffering";
        case VZBVideoPlayerStateError:
            return @"Error";
        case VZBVideoPlayerStateStopped:
            return @"Stopped";
        case VZBVideoPlayerStateStoppedOnDisconnect:
            return @"Stopped_On_Disconnect";
        case VZBVideoPlayerStateEnded:
            return @"Ended";
        default:
            return @"Idle";
    }
}

//----------------
#pragma mark - Volume Listeners
//----------------

- (void)onVolumeChanged {
    VZBVolumeClient* volumeClient = [self getSessionVolumeClient];
    if (nil != volumeClient) {
        [self notifyVolumeStatus:[self getVolumeStatusMap:volumeClient]];
    }
}

-(VZBVolumeClient*) getSessionVolumeClient {

    VZBSessionManager* sessionManager = [Vizbee getSessionManager];
    if (nil == sessionManager) {
        return nil;
    }

    VZBSession* currentSession = [sessionManager getCurrentSession];
    if (nil == currentSession) {
        return nil;
    }

    VZBVolumeClient* volumeClient = currentSession.volumeClient;
    return volumeClient;
}

-(void) addVolumeStatusListener {

    // sanity
    [self removeVolumeStatusListener];

    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::addVolumeStatusListener - Trying to add volume status listener");
    VZBVolumeClient* volumeClient = [self getSessionVolumeClient];
    if (nil != volumeClient) {

        [volumeClient addVolumeStatusDelegate:self];
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::addVolumeStatusListener - Success adding volume status listener");

        // force first update
        [self notifyVolumeStatus:[self getVolumeStatusMap:volumeClient]];
    } else {

        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::addVolumeStatusListener - Failed adding volume status listener");
    }
}

-(void) removeVolumeStatusListener {
    
    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::removeVolumeStatusListener - Trying to remove volume status listener");
    VZBVolumeClient* volumeClient = [self getSessionVolumeClient];
    if (nil != volumeClient) {
        
        RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::removeVolumeStatusListener - Success removing volume status listener");
        [volumeClient removeVolumeStatusDelegate:self];
    }
}

-(void) notifyVolumeStatus:(NSMutableDictionary*) volumeStatusMap {

    RCTLogInfo(@"[RNVZBSDK] VizbeeNativeManager::notifyVolumeStatus - Sending volume status %@", volumeStatusMap);
    [self sendEvent:@"VZB_VOLUME_STATUS" withBody:volumeStatusMap];
}

-(NSMutableDictionary*) getVolumeStatusMap:(VZBVolumeClient*) volumeClient {

    NSMutableDictionary* volumeStatusMap = [NSMutableDictionary new];
    [volumeStatusMap setValue:@([volumeClient getVolume]) forKey:@"volume"];
    [volumeStatusMap setValue:@([volumeClient isMute]) forKey:@"isMute"];
    
    return volumeStatusMap;
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

//----------------
#pragma mark - SmartPlay Callback Helpers (PlayOnPhone Reason)
//----------------

-(NSString*) getPlayOnPhoneReason:(VZBStatus*) statusCode {

    NSString *reason = nil;
    switch(statusCode.code) {
        case VZBStatusCodeSDKNotInitialized:
            reason = @"SDK_NOT_INITIALIZED";
            break;
        case VZBStatusCodeVideoExcludedFromSmartPlay:
            reason = @"VIDEO_EXCLUDED_FROM_SMART_PLAY";
            break;
        case VZBStatusCodeFailedToResolveMetadata:
            reason = @"FAILED_TO_RESOLVE_METADATA";
            break;
        case VZBStatusCodeFailedToResolveStreamInfo:
            reason = @"FAILED_TO_RESOLVE_STREAM_INFO";
            break;
        case VZBStatusCodeConfigForcesToPlayOnPhone:
            reason = @"CONFIG_FORCES_TO_PLAY_ON_PHONE";
            break;
        case VZBStatusCodeUserSelectedPlayOnPhone:
            reason = @"USER_SELECTED_PLAY_ON_PHONE";
            break;
        default:
            reason = @"GENERIC";
    }

    return reason;
}

//----------------
#pragma mark - Track Status Helpers
//----------------

-(NSMutableDictionary*) getTrackStatusMap:(VZBVideoTrackStatus*) trackStatus {

    NSMutableDictionary* trackStatusMap = [NSMutableDictionary new];
    
    // available tracks info
    NSMutableArray* availableTracksInfo = [NSMutableArray new];
    for (VZBVideoTrackInfo* trackInfo in trackStatus.availableTracks) {
        [availableTracksInfo addObject:[self getTrackInfoMap:trackInfo]];
    }
    [trackStatusMap setValue:availableTracksInfo forKey:@"availableTracks"];

    // current track info
    NSMutableDictionary* trackInfo = [self getTrackInfoMap:trackStatus.currentTrack];
    [trackStatusMap setValue:trackInfo forKey:@"currentTrack"];
    
    return trackStatusMap;
}

-(NSMutableDictionary*) getTrackInfoMap:(VZBVideoTrackInfo*) trackInfo {

    if (nil == trackInfo) {
        return [NSMutableDictionary new];
    }
    
    NSMutableDictionary* trackInfoMap = [NSMutableDictionary new];
    [trackInfoMap setValue:@(trackInfo.identifier) forKey:@"identifier"];
    [trackInfoMap setValue:trackInfo.contentIdentifier forKey:@"contentIdentifier"];
    [trackInfoMap setValue:trackInfo.contentType forKey:@"contentType"];
    [trackInfoMap setValue:trackInfo.name forKey:@"name"];
    [trackInfoMap setValue:trackInfo.languageCode forKey:@"languageCode"];

    return trackInfoMap;
}

@end
