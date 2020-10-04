
package tv.vizbee.rnsender;

import android.app.Activity;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import tv.vizbee.api.VizbeeContext;
import tv.vizbee.api.session.SessionState;
import tv.vizbee.api.session.SessionStateListener;
import tv.vizbee.api.session.VizbeeScreen;
import tv.vizbee.api.session.VizbeeSession;
import tv.vizbee.api.session.VizbeeSessionManager;
import tv.vizbee.utils.ICommandCallback;
import tv.vizbee.utils.VizbeeError;

public class RNVizbeeSenderSdkModule extends ReactContextBaseJavaModule {

  private static final String TAG = RNVizbeeSenderSdkModule.class.getName();
  
  private final ReactApplicationContext reactContext;

  public RNVizbeeSenderSdkModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNVizbeeSenderSdk";
  }

  @ReactMethod
  public void SmartHelp() {
    Log.d(TAG, "SmartHelp");

    Activity activity = this.reactContext.getCurrentActivity();
    if (activity == null) {
        Log.e(TAG, "Null activity");
        return;
    }

    Log.d(TAG, "Invoking smartHelp = ");
    VizbeeContext.getInstance().smartHelp(activity);
  }

}