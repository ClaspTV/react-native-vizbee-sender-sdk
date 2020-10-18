package tv.vizbee.rnsender;

import android.app.Activity;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;

import tv.vizbee.api.VizbeeContext;

public class VizbeeNativeManager extends ReactContextBaseJavaModule {

  private static final String LOG_TAG = VizbeeNativeManager.class.getName();
  
  private final ReactApplicationContext reactContext;

  public VizbeeNativeManager(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "VizbeeNativeManager";
  }

  @ReactMethod
  public void smartPrompt() {
    Log.d(LOG_TAG, "SmartPrompt");

    Activity activity = this.reactContext.getCurrentActivity();
    if (activity == null) {
        Log.e(LOG_TAG, "Null activity");
        return;
    }

    // Renamed old smartHelp API to new smartPrompt
    Log.d(LOG_TAG, "Invoking smartPrompt");
    VizbeeContext.getInstance().smartHelp(activity);
  }

  @ReactMethod
  public void smartPlay(ReadableMap vizbeeVideoMap, Callback didPlayOnTVCallback, Callback doPlayOnPhoneCallback){

    Log.d(LOG_TAG, "Invoking smartPlay");
    VizbeeVideo vizbeeVideo = new VizbeeVideo(vizbeeVideoMap);
    
    Activity activity = this.reactContext.getCurrentActivity();
    if (activity == null) {
        Log.e(LOG_TAG, "Null Activity");
        return;
    }

    boolean didPlayOnTV = VizbeeContext.getInstance().smartPlay(activity, vizbeeVideo, (long)(1000 * vizbeeVideo.getStartPositionInSeconds()));
    if (didPlayOnTV) {

      Log.i(LOG_TAG, "Smart play success in casting content");
      didPlayOnTVCallback.invoke();

    } else {

      Log.e(LOG_TAG, "Failed to cast content");
      doPlayOnPhoneCallback.invoke();
    }
  }
}