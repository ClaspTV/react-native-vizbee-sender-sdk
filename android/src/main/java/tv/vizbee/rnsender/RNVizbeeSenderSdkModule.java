
package tv.vizbee.rnsender;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class RNVizbeeSenderSdkModule extends ReactContextBaseJavaModule {

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