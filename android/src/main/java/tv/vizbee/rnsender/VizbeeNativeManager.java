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

import tv.vizbee.api.VideoTrackInfo;
import tv.vizbee.api.VizbeeContext;
import tv.vizbee.api.session.SessionState;
import tv.vizbee.api.session.SessionStateListener;
import tv.vizbee.api.session.VideoTrackStatus;
import tv.vizbee.api.session.VizbeeSessionManager;
import tv.vizbee.api.session.VizbeeScreen;
import tv.vizbee.api.session.VizbeeSession;
import tv.vizbee.api.session.VideoClient;
import tv.vizbee.api.session.VideoStatus;
import tv.vizbee.api.session.VolumeClient;

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

    @ReactMethod
    public void smartPlay(ReadableMap vizbeeVideoMap, Callback didPlayOnTVCallback, Callback doPlayOnPhoneCallback){

        Log.v(LOG_TAG, "Invoking smartPlay");

        Activity activity = this.reactContext.getCurrentActivity();
        if (activity == null) {
            Log.e(LOG_TAG, "SmartPlay - null activity");
            return;
        }

        // NOTE:
        // To enable the SmartPlay flow, the smartPlay api has to be updated.
        // Until the api is updated, call the smartPlay api only when the mobile
        // is connected with the receiver. If not connected call the doPlayOnPhoneCallback
        // with the reason as `CONFIG_FORCES_TO_PLAY_ON_PHONE`
        // call the didPlayOnTV with the connected device info
        
        VizbeeSessionManager sessionManager = VizbeeContext.getInstance().getSessionManager();
        String sessionState = VizbeeNativeManager.this.getSessionStateString(sessionManager.getSessionState());
        if (sessionState.equalsIgnoreCase("CONNECTED")) {

            VizbeeVideo vizbeeVideo = new VizbeeVideo(vizbeeVideoMap);

            // IMPORTANT: Android expects position in milliseconds (while iOS expects in seconds!)
            boolean didPlayOnTV = VizbeeContext.getInstance().smartPlay(activity, vizbeeVideo, (long)(1000*vizbeeVideo.getStartPositionInSeconds()));
            if (didPlayOnTV) {

                WritableMap connectedDevicemap = VizbeeNativeManager.this.getSessionConnectedDeviceMap();
                Log.i(LOG_TAG, "SmartPlay - playing on tv");
                didPlayOnTVCallback.invoke(connectedDevicemap);

            } else {

                Log.w(LOG_TAG, "SmartPlay - play on phone should not get called when the mobile is connected with the receiver");
                doPlayOnPhoneCallback.invoke("CONFIG_FORCES_TO_PLAY_ON_PHONE");
            }
        } else {
            Log.i(LOG_TAG, "SmartPlay - Mobile not connected with the receiver, invoking play on phone with reason CONFIG_FORCES_TO_PLAY_ON_PHONE");
            doPlayOnPhoneCallback.invoke("CONFIG_FORCES_TO_PLAY_ON_PHONE");
        }
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

            currentSession.sendEventWithName("tv.vizbee.homesign.signin", authJSONObject); 
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
    // App & session lifecycle
    //----------------

    @Override
    public void onHostResume() {
        Log.v(LOG_TAG, "onHostResume");
        this.addSessionStateListener();
    }

    @Override
    public void onHostPause() {
        Log.v(LOG_TAG, "onHostPause");
    }

    @Override
    public void onHostDestroy() {
        Log.v(LOG_TAG, "onHostDestroy");
        this.removeSessionStateListener();
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
        this.sendEvent("VZB_INVOKE_GET_SIGNIN_INFO", signInInfoMap);
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
