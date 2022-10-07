package tv.vizbee.rnsender;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.FragmentActivity;

import com.facebook.react.ReactActivity;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Promise;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import tv.vizbee.api.VizbeeContext;
import tv.vizbee.api.session.SessionState;
import tv.vizbee.api.session.SessionStateListener;
import tv.vizbee.api.session.VizbeeSessionManager;
import tv.vizbee.api.session.VizbeeScreen;
import tv.vizbee.api.session.VizbeeSession;
import tv.vizbee.api.session.VideoClient;
import tv.vizbee.api.session.VideoStatus;

public class VizbeeNativeManager extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private static final String LOG_TAG = VizbeeNativeManager.class.getName();

    private final ReactApplicationContext reactContext;
    VizbeeMiniCastController miniCastController;

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

        VizbeeVideo vizbeeVideo = new VizbeeVideo(vizbeeVideoMap);

        // IMPORTANT: Android expects position in milliseconds (while iOS expects in seconds!)
        boolean didPlayOnTV = VizbeeContext.getInstance().smartPlay(activity, vizbeeVideo, (long)(1000*vizbeeVideo.getStartPositionInSeconds()));
        if (didPlayOnTV) {

          Log.i(LOG_TAG, "SmartPlay success in casting content");
          didPlayOnTVCallback.invoke();

        } else {

          Log.e(LOG_TAG, "SmartPlay failed in casting content");
          doPlayOnPhoneCallback.invoke();
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
    public void seek(long position) {

        VideoClient videoClient = getSessionVideoClient();
        if (null != videoClient) {
            videoClient.seek(position);
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

    //----------------
    // MiniCastController APIs
    //----------------

    @ReactMethod
    public void addMiniCastController(int bottomMargin, int height) {
        Log.v(LOG_TAG, "Adding MiniCastController");

        // sanity
        ReactActivity reactActivity = (ReactActivity)this.reactContext.getCurrentActivity();
        if (null == reactActivity) {
            Log.e(LOG_TAG, "MiniCastController - null activity");
            return;
        }
        ViewGroup rootView = reactActivity.getWindow().getDecorView().findViewById(android.R.id.content);
        if (null == rootView) {
            Log.e(LOG_TAG, "MiniCastController - null rootView");
            return;
        }
        if (null != this.miniCastController) {
            Log.e(LOG_TAG, "MiniCastController - already added to the rootview");
            return;
        }

        // crate frame container
        FrameLayout frameContainer = createFragmentContainer(bottomMargin, height);
        reactActivity.runOnUiThread(() -> {

            Log.v(LOG_TAG, "addFragmentContainer to rootView");
            rootView.addView(frameContainer);

            // create MiniCastController and add it to frameContainer
            VizbeeNativeManager.this.miniCastController = new VizbeeMiniCastController();
            FragmentActivity fragmentActivity = (FragmentActivity) VizbeeNativeManager.this.reactContext.getCurrentActivity();
            fragmentActivity.getSupportFragmentManager().beginTransaction().add(
                R.id.minicastcontroller_fragment_container,
                VizbeeNativeManager.this.miniCastController,
                "VZBMiniCastControllerFragment").commitAllowingStateLoss();
        });
    }

    @ReactMethod
    public void setMiniCastControllerBackgroundColor(String backgroundColor) {

        getReactApplicationContext().runOnUiQueueThread(new Runnable() {

            @Override
            public void run() {
                if (null != VizbeeNativeManager.this.miniCastController) {
                    VizbeeNativeManager.this.miniCastController.setBackgroundColor(backgroundColor);
                }
            }
        });
    }

    @ReactMethod
    public void setMiniCastControllerPlaybackButtonColor(String buttonColor) {

        getReactApplicationContext().runOnUiQueueThread(new Runnable() {

            @Override
            public void run() {
                if (null != VizbeeNativeManager.this.miniCastController) {
                    VizbeeNativeManager.this.miniCastController.setButtonColor(buttonColor);
                }
            }
        });
    }

    // Vizbee will handle default behaviour of showing/hiding based on the Cast status.
    // Control show/hide explicitly as per required
    @ReactMethod
    public void showMiniCastController() {
        this.miniCastController.show();
    }

    @ReactMethod
    public void hideMiniCastController() {
        this.miniCastController.hide();
    }

    private FrameLayout createFragmentContainer(int bottomMargin, int height) {
        Log.v(LOG_TAG, "addFragmentContainer");

        final DisplayMetrics dm = this.reactContext.getResources().getDisplayMetrics();
        float bottomMarginInDP = bottomMargin * dm.density;
        float heightInDP = height * dm.density;
        Log.v(LOG_TAG, "bottomMargin " + bottomMargin + " bottomMarginInDP " + bottomMarginInDP);

        FrameLayout frameContainer = new FrameLayout(this.reactContext);
        frameContainer.setId(R.id.minicastcontroller_fragment_container);
        FrameLayout.LayoutParams layoutParams =
            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (int) heightInDP);
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.bottomMargin = (int) bottomMarginInDP;
        frameContainer.setLayoutParams(layoutParams);
        return frameContainer;
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
            this.sessionStateListener = new SessionStateListener() {

                @Override
                public void onSessionStateChanged(int newState) {

                    // handle videoClient
                    if (newState == SessionState.CONNECTED) {
                        VizbeeNativeManager.this.addVideoStatusListener();
                    } else {
                        VizbeeNativeManager.this.removeVideoStatusListener();
                    }

                    VizbeeNativeManager.this.notifySessionStatus(newState);
                }
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

        VideoClient videoClient = currentSession.getVideoClient();
        return videoClient;
    }

    private void addVideoStatusListener() {

        // sanity
        this.removeVideoStatusListener();

        Log.v(LOG_TAG, "TRYING to add video status listener");
        VideoClient videoClient = getSessionVideoClient();
        if (null != videoClient) {

            this.videoStatusListener = new VideoClient.VideoStatusListener() {

                @Override
                public void onVideoStatusUpdated(VideoStatus status) {
                    VizbeeNativeManager.this.notifyMediaStatus(status);
                }
            };
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
        videoStatusMap.putInt("position", (int) videoStatus.getStreamPosition());
        videoStatusMap.putInt("duration", (int) videoStatus.getStreamDuration());
        videoStatusMap.putBoolean("isLive", videoStatus.isStreamLive());
        videoStatusMap.putString("playerState", this.getPlayerStateString(videoStatus.getPlayerState()));
        videoStatusMap.putBoolean("isAdPlaying", videoStatus.isAdPlaying());

        return videoStatusMap;
    }

    private String getPlayerStateString(int state) {

        switch (state) {
            case VideoStatus.PLAYER_STATE_IDLE:
                return "IDLE";
            case VideoStatus.PLAYER_STATE_PLAYING:
                return "PLAYING";
            case VideoStatus.PLAYER_STATE_PAUSED:
                return "PAUSED";
            case VideoStatus.PLAYER_STATE_BUFFERING:
                return "BUFFERING";
            default:
                return "UNKNOWN";
        }
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
