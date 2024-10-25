package tv.vizbee.rnsender;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import androidx.annotation.NonNull;

import com.facebook.react.ReactActivity;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Promise;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONException;

import tv.vizbee.api.VideoTrackInfo;
import tv.vizbee.api.VizbeeContext;
import tv.vizbee.api.SmartHelpOptions;
import tv.vizbee.api.VizbeeRequest;
import tv.vizbee.api.RequestCallback;
import tv.vizbee.api.VizbeeStatus;
import tv.vizbee.api.analytics.VizbeeAnalyticsListener;
import tv.vizbee.api.analytics.VizbeeAnalyticsManager;
import tv.vizbee.api.analytics.VizbeeAnalyticsManager.VZBAnalyticsEventType;
import tv.vizbee.api.session.SessionState;
import tv.vizbee.api.session.SessionStateListener;
import tv.vizbee.api.session.VideoTrackStatus;
import tv.vizbee.api.session.VizbeeSessionManager;
import tv.vizbee.api.session.VizbeeScreen;
import tv.vizbee.api.session.VizbeeSession;
import tv.vizbee.api.session.VideoClient;
import tv.vizbee.api.session.VideoStatus;
import tv.vizbee.api.session.VolumeClient;
import tv.vizbee.api.uiConfig.UIConfiguration;
import tv.vizbee.api.uiConfig.cardConfig.CardConfiguration;
import tv.vizbee.api.uiConfig.cardConfig.UICardType;

public class VizbeeNativeManager extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private static final String LOG_TAG = "VZBRNSDK_VizbeeNativeManager";

    private final ReactApplicationContext reactContext;

    public VizbeeNativeManager(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;

        this.reactContext.addLifecycleEventListener(this);
    }

    @Override
    public String getName() {
        return "VizbeeNativeManager";
    }

    //----------------
    // Flow APIs
    //----------------

    @ReactMethod
    public void smartPrompt() {

        Log.v(LOG_TAG, "Invoking smartPrompt");

        Activity activity = this.reactContext.getCurrentActivity();
        if (activity == null) {
            Log.e(LOG_TAG, "SmartPrompt - null activity");
            return;
        }

        // Renamed old smartHelp API to new smartPrompt
        VizbeeContext.getInstance().smartHelp(activity);
    }

    @ReactMethod
    public void smartPromptWithOptions(ReadableMap smartPromptOptions) {

        Log.v(LOG_TAG, "Invoking smartPromptWithOptions " + smartPromptOptions);

        Activity activity = this.reactContext.getCurrentActivity();
        if (activity == null) {
            Log.e(LOG_TAG, "smartPromptWithOptions - null activity");
            return;
        }

        SmartHelpOptions smartHelpOptions = new SmartHelpOptions();
        if (smartPromptOptions.hasKey("enabledSubflows")) {
            smartHelpOptions.enabledSubflows = smartPromptOptions.getInt("enabledSubflows");
        }

        // Renamed old smartHelp API to new smartPrompt
        VizbeeContext.getInstance().smartHelp(smartHelpOptions, activity);
    }

    @ReactMethod
    public void smartCast() {

        Log.v(LOG_TAG, "Invoking smartCast");

        Activity activity = this.reactContext.getCurrentActivity();
        if (activity == null) {
            Log.e(LOG_TAG, "SmartCast - null activity");
            return;
        }

        VizbeeSessionManager sessionManager = VizbeeContext.getInstance().getSessionManager();
        if (null != sessionManager) {
            sessionManager.onCastIconClicked(activity);
        }
    }

    private Callback mDidPlayOnTVCallback;
    private Callback mDoPlayOnPhoneCallback;
    @ReactMethod
    public void smartPlay(ReadableMap vizbeeVideoMap, Callback didPlayOnTVCallback, Callback doPlayOnPhoneCallback){

        this.mDoPlayOnPhoneCallback = doPlayOnPhoneCallback;
        this.mDidPlayOnTVCallback = didPlayOnTVCallback;

        Log.v(LOG_TAG, "Invoking smartPlay");

        Activity activity = this.reactContext.getCurrentActivity();
        if (activity == null) {
            Log.e(LOG_TAG, "SmartPlay - null activity");
            if (null != this.mDoPlayOnPhoneCallback) {
                mDoPlayOnPhoneCallback.invoke("FAILED_TO_GET_ACTIVITY");
                mDoPlayOnPhoneCallback = null;
            }
            return;
        }

        VizbeeVideo vizbeeVideo = new VizbeeVideo(vizbeeVideoMap);
        // IMPORTANT: Android expects position in milliseconds (while iOS expects in seconds!)
        VizbeeRequest request = new VizbeeRequest(vizbeeVideo, vizbeeVideo.getGuid(), (long)(1000*vizbeeVideo.getStartPositionInSeconds()));
        request.setCallback(new RequestCallback() {
            @Override
            public void didPlayOnTV(@NonNull VizbeeScreen screen) {
                Log.i(LOG_TAG, "Played on TV = " + screen.toString());
                if (null != mDidPlayOnTVCallback) {
                    WritableMap connectedDevicemap = VizbeeNativeManager.this.getSessionConnectedDeviceMap();
                    mDidPlayOnTVCallback.invoke(connectedDevicemap);
                    mDidPlayOnTVCallback = null;
                }
            }
            @Override
            public void doPlayOnPhone(@NonNull VizbeeStatus status) {
                Log.i(LOG_TAG,"Play on phone with status = " + status);
                if (null != mDoPlayOnPhoneCallback) {
                    String reasonForPlayOnPhone = getPlayOnPhoneReason(status);
                    mDoPlayOnPhoneCallback.invoke(reasonForPlayOnPhone);
                    mDoPlayOnPhoneCallback = null;
                }
            }
        });

        VizbeeContext.getInstance().smartPlay(activity, request);
    }

    //----------------
    // Session APIs
    //----------------

    @ReactMethod
    public void getSessionState(final Promise promise) {

        getReactApplicationContext().runOnUiQueueThread(new Runnable() {

            @Override
            public void run() {

                VizbeeSessionManager sessionManager = VizbeeContext.getInstance().getSessionManager();
                if (null == sessionManager) {
                    promise.resolve(null);
                    return;
                }

                promise.resolve(VizbeeNativeManager.this.getSessionStateString(sessionManager.getSessionState()));
            }
        });
    }

    @ReactMethod
    public void getSessionConnectedDevice(final Promise promise) {

        getReactApplicationContext().runOnUiQueueThread(new Runnable() {

            @Override
            public void run() {
                promise.resolve(VizbeeNativeManager.this.getSessionConnectedDeviceMap());
            }
        });
    }

    @ReactMethod
    public void disconnect() {
        VizbeeSessionManager sessionManager = VizbeeContext.getInstance().getSessionManager();
        if (null != sessionManager) {
            sessionManager.disconnectSession();
        }
    }

    //----------------
    // SignIn APIs
    //----------------

    @ReactMethod
    public void onGetSignInInfo(ReadableMap vizbeeSignInInfoMap) {
        Log.v(LOG_TAG, "Invoking onGetSignInInfo");
        VizbeeSessionManager sessionManager = VizbeeContext.getInstance().getSessionManager();
        if (null == sessionManager) {
            Log.i(LOG_TAG, "sessionManager is null");
            return;
        }

        VizbeeSession currentSession = sessionManager.getCurrentSession();
        if (null == currentSession) {
            Log.i(LOG_TAG, "currentSession is null");
            return;
        }

        try {
            JSONObject authInfo = RNJSONConverter.convertMapToJson(vizbeeSignInInfoMap);
            JSONObject authJSONObject = new JSONObject();
            authJSONObject.put("authInfo", authInfo);

            currentSession.sendEventWithName(VizbeeConstants.VZB_SIGNIN_EVENT, authJSONObject);
        } catch (Exception e) {
            Log.w(LOG_TAG, "Exception while converting vizbeeSignInInfoMap to JSON");
        }
    }

    //----------------
    // Video APIs
    //----------------

    @ReactMethod
    public void play() {

        VideoClient videoClient = getSessionVideoClient();
        if (null != videoClient) {
            videoClient.play();
        } else {
            Log.w(LOG_TAG, "Play ignored because videoClient is null");
        }
    }

    @ReactMethod
    public void pause() {

        VideoClient videoClient = getSessionVideoClient();
        if (null != videoClient) {
            videoClient.pause();
        } else {
            Log.w(LOG_TAG, "Pause ignored because videoClient is null");
        }
    }

    @ReactMethod
    public void seek(double position) {

        VideoClient videoClient = getSessionVideoClient();
        if (null != videoClient) {
            videoClient.seek((long)position);
        } else {
            Log.w(LOG_TAG, "Seek ignored because videoClient is null");
        }
    }

    @ReactMethod
    public void stop() {
        VideoClient videoClient = getSessionVideoClient();
        if (null != videoClient) {
            videoClient.stop();
        } else {
            Log.w(LOG_TAG, "Stop ignored because videoClient is null");
        }
    }

    @ReactMethod
    public void supportsVolumeControl(Callback callback) {
        VolumeClient volumeClient = getSessionVolumeClient();
        if (null != volumeClient) {
            if (null != callback) {
                callback.invoke((volumeClient.supportsVolumeControl()));
            } else {
                Log.w(LOG_TAG, "supportsVolumeControl ignored because callback is null");
            }
        } else {
            Log.w(LOG_TAG, "supportsVolumeControl ignored because volumeClient is null");
        }
    }

    @ReactMethod
    public void setVolume(float volume) {
        VolumeClient volumeClient = getSessionVolumeClient();
        if (null != volumeClient) {
            volumeClient.setVolume(volume);
        } else {
            Log.w(LOG_TAG, "setVolume ignored because volumeClient is null");
        }
    }

    @ReactMethod
    public void getVolume(Callback volumeCallback) {
        VolumeClient volumeClient = getSessionVolumeClient();
        if (null != volumeClient) {
            if (null != volumeCallback) {
                volumeCallback.invoke((getVolumeStatusMap()));
            } else {
                Log.w(LOG_TAG, "getVolume ignored because volumeCallback is null");
            }
        } else {
            Log.w(LOG_TAG, "getVolume ignored because volumeClient is null");
        }
    }

    @ReactMethod
    public void mute() {
        VolumeClient volumeClient = getSessionVolumeClient();
        if (null != volumeClient) {
            volumeClient.setMute(true);
        } else {
            Log.w(LOG_TAG, "Mute ignored because volumeClient is null");
        }
    }

    @ReactMethod
    public void unmute() {
        VolumeClient volumeClient = getSessionVolumeClient();
        if (null != volumeClient) {
            volumeClient.setMute(false);
        } else {
            Log.w(LOG_TAG, "Unmute ignored because volumeClient is null");
        }
    }

    @ReactMethod
    public void setActiveTrack(ReadableMap track) {

        // not getting the track type, setting default to type TEXT
        VideoTrackInfo trackInfo = new VideoTrackInfo.Builder(track.getInt("identifier"), VideoTrackInfo.TYPE_TEXT)
            .setContentId(track.getString("contentIdentifier"))
            .setContentType(track.getString("contentType"))
            .setName(track.getString("name"))
            .setLanguage(track.getString("languageCode"))
            .build();
        List<VideoTrackInfo> tracks = new ArrayList();
        tracks.add(trackInfo);

        VideoClient videoClient = getSessionVideoClient();
        if (null != videoClient) {
            videoClient.setActiveTracks(tracks);
        } else {
            Log.w(LOG_TAG, "setActiveTrack ignored because videoClient is null");
        }
    }

    @ReactMethod
    public void resetActiveTrack() {

        List<VideoTrackInfo> tracks = new ArrayList();
        VideoClient videoClient = getSessionVideoClient();
        if (null != videoClient) {
            videoClient.setActiveTracks(tracks);
        } else {
            Log.w(LOG_TAG, "resetActiveTrack ignored because videoClient is null");
        }
    }

    //----------------
    // UI
    //----------------

    @ReactMethod
    public void setUICardConfiguration(ReadableMap cardConfigurationMap, String forCardType) {

        try {
            VizbeeUICardConfiguration uiCardConfiguration = new VizbeeUICardConfiguration(cardConfigurationMap);
            CardConfiguration cardConfiguration = uiCardConfiguration.getCardConfigurationForType(forCardType);
            UICardType cardType = getCardType(forCardType);
            if (null != cardConfiguration && null != cardType) {
                UIConfiguration uiConfiguration = VizbeeContext.getInstance().getUIConfiguration();
                if (null != uiConfiguration) {
                    uiConfiguration.setCardConfiguration(cardConfiguration, cardType);
                }
            } else {
                Log.i(LOG_TAG, "[RNVZBSDK] VizbeeNativeManager::setUICardConfiguration - received card configuration for unknown card type" + cardType);
            }
        } catch (Exception e) {
            Log.w(LOG_TAG, "Exception while converting card configuration");
        }
    }

    @ReactMethod
    public void removeUICardConfiguration(String cardType) {

        UICardType cardType = getCardType(cardType);
        if (null == cardType) {
            Log.i(LOG_TAG, "[RNVZBSDK] VizbeeNativeManager::removeUICardConfiguration - received unknown card type" + cardType);
            return;
        }

        UIConfiguration uiConfiguration = VizbeeContext.getInstance().getUIConfiguration();
        if (null != uiConfiguration) {
            uiConfiguration.removeCardConfiguration(cardType);
        }
    }

    private UICardType getCardType(String cardType) {

        if (cardType.equals("CAST_INTRODUCTION")) {
           return UICardType.CAST_INTRODUCTION;
        } else if (cardType.equals("SMART_INSTALL")) {
            return UICardType.SMART_INSTALL;
         } else if (cardType.equals("GUIDED_SMART_INSTALL")) {
            return UICardType.GUIDED_SMART_INSTALL;
         } else if (cardType.equals("MULTI_DEVICE_SMART_INSTALL")) {
            return UICardType.MULTI_DEVICE_SMART_INSTALL;
         }
    
        return null;
    }

    //----------------
    // Analytics
    //----------------

    @ReactMethod
    public void addAnalyticsAttributes(ReadableMap attributes) {

        try {
            JSONObject attributesJSON = RNJSONConverter.convertMapToJson(attributes);
            VizbeeContext.getInstance().addCustomEventAttributes(attributesJSON);
        } catch (Exception e) {
            Log.w(LOG_TAG, "Exception while converting analytics attributes to JSON");
        }
    }

    //----------------
    // App & session lifecycle
    //----------------

    @Override
    public void onHostResume() {
        Log.v(LOG_TAG, "onHostResume");
        this.addSessionStateListener();
        this.addAnalyticsListener();
    }

    @Override
    public void onHostPause() {
        Log.v(LOG_TAG, "onHostPause");
    }

    @Override
    public void onHostDestroy() {
        Log.v(LOG_TAG, "onHostDestroy");
        this.removeSessionStateListener();
        this.removeAnalyticsListener();
    }

    private SessionStateListener sessionStateListener;
    private int lastUpdatedState = 0; // UNKNOWN

    private void addSessionStateListener() {

        // sanity
        this.removeSessionStateListener();

        VizbeeSessionManager sessionManager = VizbeeContext.getInstance().getSessionManager();
        if (null != sessionManager) {

            Log.i(LOG_TAG, "Adding session state listener");
            this.sessionStateListener = newState -> {

                // handle videoClient/volumeClient
                if (newState == SessionState.CONNECTED) {
                    VizbeeNativeManager.this.addVideoStatusListener();
                    VizbeeNativeManager.this.addVolumeStatusListener();

                    VizbeeNativeManager.this.notifyGetSignInInfo();
                } else {
                    VizbeeNativeManager.this.removeVideoStatusListener();
                    VizbeeNativeManager.this.removeVolumeStatusListener();
                }

                VizbeeNativeManager.this.notifySessionStatus(newState);
            };
            sessionManager.addSessionStateListener(this.sessionStateListener);

            // force first update
            this.notifySessionStatus(sessionManager.getSessionState());
        }
    }

    private void removeSessionStateListener() {

        VizbeeSessionManager sessionManager = VizbeeContext.getInstance().getSessionManager();
        if (null != sessionManager) {
            if (null != this.sessionStateListener) {

                Log.i(LOG_TAG, "Removing session state listener");
                sessionManager.removeSessionStateListener(this.sessionStateListener);
            }
        }
        this.sessionStateListener = null;
    }

    private void notifySessionStatus(int newState) {

        if (newState == lastUpdatedState) {
            Log.w(LOG_TAG, "Ignoring duplicate state update");
            return;
        }
        lastUpdatedState = newState;

        String state = this.getSessionStateString(newState);
        WritableMap stateMap = Arguments.createMap();
        stateMap.putString("connectionState", state);

        WritableMap deviceMap = this.getSessionConnectedDeviceMap();
        if (null != deviceMap) {
            stateMap.merge(deviceMap);
        }

        this.sendEvent("VZB_SESSION_STATUS", stateMap);
    }

    private String getSessionStateString(int state) {

        switch (state) {
            case SessionState.NO_DEVICES_AVAILABLE:
                return "NO_DEVICES_AVAILABLE";
            case SessionState.NOT_CONNECTED:
                return "NOT_CONNECTED";
            case SessionState.CONNECTING:
                return "CONNECTING";
            case SessionState.CONNECTED:
                return "CONNECTED";
            default:
                return "UNKNOWN";
        }
    }

    private WritableMap getSessionConnectedDeviceMap() {

        VizbeeSessionManager sessionManager = VizbeeContext.getInstance().getSessionManager();
        if (null == sessionManager) {
            return null;
        }

        VizbeeSession currentSession = sessionManager.getCurrentSession();
        if (null == currentSession) {
            return null;
        }

        VizbeeScreen screen = currentSession.getVizbeeScreen();
        if (null == screen) {
            return null;
        }

        WritableMap map = Arguments.createMap();
        map.putString("connectedDeviceType", screen.getScreenType().getTypeName());
        map.putString("connectedDeviceFriendlyName", screen.getScreenInfo().getFriendlyName());
        map.putString("connectedDeviceModel", screen.getScreenInfo().getModel());
        return map;
    }

    private void notifyGetSignInInfo() {

        Log.v(LOG_TAG, "Sending signin info trigger ...");
        WritableMap signInInfoMap = Arguments.createMap();
        this.sendEvent(VizbeeConstants.VZB_INVOKE_GET_SIGNIN_INFO, signInInfoMap);
    }

    //----------------
    // Analytics listener
    //----------------

    private VizbeeAnalyticsListener analyticsListener;

    private void addAnalyticsListener() {

        // sanity
        this.removeAnalyticsListener();

        VizbeeAnalyticsManager analyticsManager = VizbeeContext.getInstance().getAnalyticsManager();
        if (null != analyticsManager) {

            Log.i(LOG_TAG, "Adding analytics listener");
            this.analyticsListener = (event, properties) -> {
                VizbeeNativeManager.this.notifyAnalyticsEvent(event, properties);
            };
            analyticsManager.addAnalyticsListener(this.analyticsListener);
        } else {
            Log.i(LOG_TAG, "Adding analytics listener - Failed to add, analyticsManager is null");
        }
    }

    private void removeAnalyticsListener() {

        VizbeeAnalyticsManager analyticsManager = VizbeeContext.getInstance().getAnalyticsManager();
        if (null != analyticsManager && null != analyticsListener) {
            Log.i(LOG_TAG, "Removing analytics listener");
            analyticsManager.removeAnalyticsListener(this.analyticsListener);
        } else {
            Log.i(LOG_TAG, "Removing analytics listener - Failed to remove, analyticsManager or analyticsListener is null");
        }
        this.analyticsListener = null;
    }

    private void notifyAnalyticsEvent(VZBAnalyticsEventType eventType, JSONObject properties) {

        // add event
        String eventName = this.getAnalyticsEventNameString(eventType);
        WritableMap eventMap = Arguments.createMap();
        eventMap.putString("event", eventName);

        // add properties
        if (null != properties) {
            try {
                WritableMap propertiesMap = RNJSONConverter.convertJsonToMap(properties);
                eventMap.putMap("properties", propertiesMap);
            } catch (JSONException e) {
                Log.w(LOG_TAG, "Exception while converting event properties to WritableMap");
            }
        }

        this.sendEvent("VZB_ANALYTICS_EVENT", eventMap);
    }

    private String getAnalyticsEventNameString(VZBAnalyticsEventType eventType) {

        switch (eventType) {
            case VZBAnalyticsEventTypeCastIntroductionCardShown:
                return "CAST_INTRODUCTION_CARD_SHOWN";
            case VZBAnalyticsEventTypeSmartInstallCardShown:
                return "SMART_INSTALL_CARD_SHOWN";
            case VZBAnalyticsEventTypeCastIconDeviceSelectionCardShown:
                return "CAST_ICON_DEVICE_SELECTION_CARD_SHOWN";
            case VZBAnalyticsEventTypeSmartPlayDeviceSelectionCardShown:
                return "SMART_PLAY_DEVICE_SELECTION_CARD_SHOWN";
            case VZBAnalyticsEventTypeSmartNotificationDeviceSelectionCardShown:
                return "SMART_NOTIFICATION_DEVICE_SELECTION_CARD_SHOWN";
            case VZBAnalyticsEventTypeScreenDeviceSelected:
                return "SCREEN_DEVICE_SELECTED";
            case VZBAnalyticsEventTypePlayOnPhoneSelected:
                return "PLAY_ON_PHONE_SELECTED";
            case VZBAnalyticsEventTypePlayOnTVSelected:
                return "PLAY_ON_TV_SELECTED";
            case VZBAnalyticsEventTypeCardDismissed:
                return "CARD_DISMISSED";
            default:
                return "UNKNOWN";
        }
    }

    //----------------
    // Video client listener
    //----------------

    private VideoClient.VideoStatusListener videoStatusListener;

    private VideoClient getSessionVideoClient() {

        VizbeeSessionManager sessionManager = VizbeeContext.getInstance().getSessionManager();
        if (null == sessionManager) {
            return null;
        }

        VizbeeSession currentSession = sessionManager.getCurrentSession();
        if (null == currentSession) {
            return null;
        }

        return currentSession.getVideoClient();
    }

    private void addVideoStatusListener() {

        // sanity
        this.removeVideoStatusListener();

        Log.v(LOG_TAG, "TRYING to add video status listener");
        VideoClient videoClient = getSessionVideoClient();
        if (null != videoClient) {

            this.videoStatusListener = VizbeeNativeManager.this::notifyMediaStatus;
            videoClient.addVideoStatusListener(this.videoStatusListener);
            Log.i(LOG_TAG, "SUCCESS adding video status listener");

            // force first update
            this.notifyMediaStatus(videoClient.getVideoStatus());
        } else {

            Log.w(LOG_TAG, "FAILED to add video status listener");
        }
    }

    private void removeVideoStatusListener() {

        Log.v(LOG_TAG, "TRYING to remove video status listener");
        VideoClient videoClient = getSessionVideoClient();
        if (null != videoClient) {
            if (null != this.videoStatusListener) {

                Log.i(LOG_TAG, "SUCCESS removing video status listener");
                videoClient.removeVideoStatusListener(this.videoStatusListener);
            }
        }
        this.videoStatusListener = null;
    }

    private void notifyMediaStatus(VideoStatus videoStatus) {

        Log.v(LOG_TAG, "Sending media status ...");
        Log.v(LOG_TAG, videoStatus.toString());
        WritableMap videoStatusMap = this.getVideoStatusMap(videoStatus);
        this.sendEvent("VZB_MEDIA_STATUS", videoStatusMap);
    }

    private WritableMap getVideoStatusMap(VideoStatus videoStatus) {

        WritableMap videoStatusMap = Arguments.createMap();
        videoStatusMap.putString("guid", videoStatus.getGuid());
        videoStatusMap.putString("title", videoStatus.getTitle());
        videoStatusMap.putString("subTitle", videoStatus.getSubTitle());
        videoStatusMap.putString("imageURL", videoStatus.getImageUrl());

        videoStatusMap.putString("playerState", getPlayerStateString(videoStatus.getPlayerState()));
        videoStatusMap.putBoolean("isLive", videoStatus.isStreamLive());
        videoStatusMap.putInt("position", (int) videoStatus.getStreamPosition());
        videoStatusMap.putInt("duration", (int) videoStatus.getStreamDuration());
        videoStatusMap.putInt("streamPosition", (int) videoStatus.getStreamPosition());
        videoStatusMap.putInt("streamDuration", (int) videoStatus.getStreamDuration());

        videoStatusMap.putBoolean("isAdPlaying", videoStatus.isAdPlaying());
        videoStatusMap.putDouble("adPosition", videoStatus.getAdPosition());
        videoStatusMap.putDouble("adDuration", videoStatus.getAdDuration());

        videoStatusMap.putMap("trackStatus", getTrackStatusMap(videoStatus.getVideoTrackStatus()));

        return videoStatusMap;
    }

    private String getPlayerStateString(int state) {

        switch (state) {
            case VideoStatus.PLAYER_STATE_IDLE:
                return "Idle";
            case VideoStatus.PLAYER_STATE_STARTED:
                return "Started";
            case VideoStatus.PLAYER_STATE_PLAYING:
                return "Playing";
            case VideoStatus.PLAYER_STATE_PAUSED:
                return "Paused";
            case VideoStatus.PLAYER_STATE_BUFFERING:
                return "Buffering";
            case VideoStatus.PLAYER_STATE_ERROR:
                return "Error";
            case VideoStatus.PLAYER_STATE_STOPPED:
                return "Stopped";
            case VideoStatus.PLAYER_STATE_STOPPED_ON_DISCONNECT:
                return "Stopped_On_Disconnect";
            case VideoStatus.PLAYER_STATE_ENDED:
                return "Ended";
            default:
                return "Idle";
        }
    }

//----------------
// Volume Listeners
//----------------

    private VolumeClient.Listener volumeStatusListener;

    private VolumeClient getSessionVolumeClient() {

        VizbeeSessionManager sessionManager = VizbeeContext.getInstance().getSessionManager();
        if (null == sessionManager) {
            return null;
        }

        VizbeeSession currentSession = sessionManager.getCurrentSession();
        if (null == currentSession) {
            return null;
        }

        return currentSession.getVolumeClient();
    }

    private void addVolumeStatusListener() {

        // sanity
        this.removeVolumeStatusListener();

        Log.v(LOG_TAG, "TRYING to add volume status listener");
        VolumeClient volumeClient = getSessionVolumeClient();
        if (null != volumeClient) {

            this.volumeStatusListener = VizbeeNativeManager.this::notifyVolumeStatus;
            volumeClient.addVolumeChangedListener(this.volumeStatusListener);
            Log.i(LOG_TAG, "SUCCESS adding volume status listener");

            // force first update
            this.notifyVolumeStatus();
        } else {

            Log.w(LOG_TAG, "FAILED to add volume status listener");
        }
    }

    private void removeVolumeStatusListener() {

        Log.v(LOG_TAG, "TRYING to remove volume status listener");
        VolumeClient volumeClient = getSessionVolumeClient();
        if (null != volumeClient) {
            if (null != this.volumeStatusListener) {

                Log.i(LOG_TAG, "SUCCESS removing volume status listener");
                volumeClient.removeVolumeChangedListener(this.volumeStatusListener);
            }
        }
        this.volumeStatusListener = null;
    }

    private void notifyVolumeStatus() {
        Log.v(LOG_TAG, "Sending volume status ...");
        this.sendEvent("VZB_VOLUME_STATUS", getVolumeStatusMap());
    }

    private WritableMap getVolumeStatusMap() {


        WritableMap volumeStatusMap = Arguments.createMap();
        VolumeClient volumeClient = getSessionVolumeClient();
        if (null != volumeClient) {
            volumeStatusMap.putDouble("volume", volumeClient.getVolume());
            volumeStatusMap.putBoolean("isMute", volumeClient.isMute());
        }

        return volumeStatusMap;
    }

    //----------------
    // SmartPlay Callback Helpers (PlayOnPhone Reason)
    //----------------

    private String getPlayOnPhoneReason(VizbeeStatus status) {

        switch (status.getStatusCode()) {
            case VizbeeStatus.SDK_NOT_INITIALIZED:
                return "SDK_NOT_INITIALIZED";
            case VizbeeStatus.VIDEO_EXCLUDED_FROM_SMART_PLAY:
                return "VIDEO_EXCLUDED_FROM_SMART_PLAY";
            case VizbeeStatus.FAILED_TO_RESOLVE_METADATA:
                return "FAILED_TO_RESOLVE_METADATA";
            case VizbeeStatus.FAILED_TO_RESOLVE_STREAM_INFO:
                return "FAILED_TO_RESOLVE_STREAM_INFO";
            case VizbeeStatus.CONFIG_FORCES_TO_PLAY_ON_PHONE:
                return "CONFIG_FORCES_TO_PLAY_ON_PHONE";
            case VizbeeStatus.USER_SELECTED_PLAY_ON_PHONE:
                return "USER_SELECTED_PLAY_ON_PHONE";
            default:
                return "GENERIC";
        }
    }

    //----------------
    // Track Status Helpers
    //----------------

    private WritableMap getTrackStatusMap(VideoTrackStatus videoTrackStatus) {

        if (null == videoTrackStatus) {
            return null;
        }

        WritableMap trackInfoMap = Arguments.createMap();

        // available tracks info
        WritableArray availableTracksInfo = Arguments.createArray();
        for (VideoTrackInfo trackInfo : videoTrackStatus.getAvailableTracks()) {
            availableTracksInfo.pushMap(getTrackInfoMap(trackInfo));
        }
        trackInfoMap.putArray("availableTracks", availableTracksInfo);

        // current track info
        WritableMap videoTrackInfoMap = getTrackInfoMap(videoTrackStatus.getCurrentTrack());
        trackInfoMap.putMap("currentTrack", videoTrackInfoMap);

        return trackInfoMap;
    }

    private WritableMap getTrackInfoMap(VideoTrackInfo trackInfo) {

        if (null == trackInfo) {
            return null;
        }

        WritableMap trackInfoMap = Arguments.createMap();
        trackInfoMap.putDouble("identifier", trackInfo.getId());
        trackInfoMap.putString("contentIdentifier", trackInfo.getContentId());
        trackInfoMap.putString("contentType", trackInfo.getContentType());
        trackInfoMap.putString("name", trackInfo.getName());
        trackInfoMap.putString("languageCode", trackInfo.getLanguage());
        return trackInfoMap;
    }

    //----------------
    // Bridge events
    //----------------

    private void sendEvent(String eventName, @Nullable WritableMap params) {
        getReactApplicationContext()
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
    }
}
