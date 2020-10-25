package tv.vizbee.rnsender;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Nullable;

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

public class VizbeeNativeManager extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private static final String LOG_TAG = VizbeeNativeManager.class.getName();
  
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
    // Smart APIs
    //----------------

    @ReactMethod
    public void smartPrompt() {

        Log.v(LOG_TAG, "Invoking smartPrompt");

        Activity activity = this.reactContext.getCurrentActivity();
        if (activity == null) {
            Log.e(LOG_TAG, "SmartPlay - null activity");
            return;
        }

        // Renamed old smartHelp API to new smartPrompt
        VizbeeContext.getInstance().smartHelp(activity);
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
        boolean didPlayOnTV = VizbeeContext.getInstance().smartPlay(activity, vizbeeVideo, (long)(1000 * vizbeeVideo.getStartPositionInSeconds()));
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

    // TODO: Remove
    // 
    // @ReactMethod
    // public void triggerSessionStateEvent() {

    //     VizbeeSessionManager sessionManager = VizbeeContext.getInstance().getSessionManager();
    //     if (null != sessionManager) {
    //         this.notifySessionState(sessionManager.getSessionState());
    //     }
    // }

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
        map.putString("deviceType", screen.getScreenType().getTypeName());
        map.putString("deviceFriendlyName", screen.getScreenInfo().getFriendlyName());
        map.putString("deviceModel", screen.getScreenInfo().getModel());
        return map;
    }

    //----------------
    // App lifecycle
    //----------------

    private SessionStateListener sessionStateListener;
    private SessionState lastUpdatedState = 0; // UNKNOWN

    private void addSessionStateListener() {

        // sanity
        this.removeSessionStateListener();

        VizbeeSessionManager sessionManager = VizbeeContext.getInstance().getSessionManager();
        if (null != sessionManager) {

            this.sessionStateListener = new SessionStateListener() {
                
                @Override
                public void onSessionStateChanged(int newState) {
                    VizbeeNativeManager.this.notifySessionState(newState);
                }
            };
            sessionManager.addSessionStateListener(this.sessionStateListener);

            this.notifySessionState(sessionManager.getSessionState());
        }
    }

    private void removeSessionStateListener() {

        VizbeeSessionManager sessionManager = VizbeeContext.getInstance().getSessionManager();
        if (null != sessionManager) {
            if (null != this.sessionStateListener) {
                sessionManager.removeSessionStateListener(this.sessionStateListener);
            }
        }
        this.sessionStateListener = null;
    }

    private void notifySessionState(int newState) {

        if (newState == lastUpdatedState) {
            Log.w(LOG_TAG, "Ignoring duplicate state update");
            return;
        }
        lastUpdatedState = newState;
        
        String state = this.getSessionStateString(newState);
        WritableMap stateMap = Arguments.createMap();
        stateMap.putString("state", state);

        WritableMap deviceMap = this.getSessionConnectedDeviceMap();
        if (null != deviceMap) {
            stateMap.merge(deviceMap);
        }

        this.sendEvent("VZB_SESSION_STATE", stateMap);
    }

    @Override
    public void onHostResume() {
        this.addSessionStateListener();
    }

    @Override
    public void onHostPause() {
        this.removeSessionStateListener();
    }

    @Override
    public void onHostDestroy() {
    }

    //----------------
    // Bridge Events
    //----------------

    private void sendEvent(String eventName, @Nullable WritableMap params) {
        getReactApplicationContext()
                  .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                  .emit(eventName, params);
    }
}