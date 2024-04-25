package tv.vizbee.rnsender;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.uimanager.annotations.ReactProp;
import android.util.Log;
import android.app.Activity;


public class VizbeeCastBarViewManager extends SimpleViewManager<VizbeeCastBarView> {

    private static final String REACT_CLASS = "VizbeeCastBarView";
    private static final String LOG_TAG = "VZBRNSDK_VizbeeCastBarViewManager";

    private ThemedReactContext reactContext;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected VizbeeCastBarView createViewInstance(ThemedReactContext reactContext) {
        this.reactContext = reactContext;
        return new VizbeeCastBarView(reactContext);
    }


    @Override
    public void onDropViewInstance(VizbeeCastBarView view) {
        super.onDropViewInstance(view);
    }

    
    public void getActive(int reactTag, Callback callback) {
        Activity currentActivity = reactContext.getCurrentActivity();
        if (currentActivity != null) {
            VizbeeCastBarView view = currentActivity.findViewById(reactTag);
            if (view != null) {
                boolean active = view.getActive();
                callback.invoke(active);
            }
        }
        Log.v(LOG_TAG, "getActive - " + reactTag);
    }

    public void getMinHeight(int reactTag, Callback callback) {
        Activity currentActivity = reactContext.getCurrentActivity();
        if (currentActivity != null) {
            VizbeeCastBarView view = currentActivity.findViewById(reactTag);
            if (view != null) {
                float minHeight = view.getMinHeight();
                callback.invoke(minHeight);
            }
        }
        Log.v(LOG_TAG, "getMinHeight - " + reactTag);
    }

    @ReactMethod
    public void setOnVisibilityChange(VizbeeCastBarView view, final Callback callback) {
        Log.v(LOG_TAG, "setOnVisibilityChange");
        view.setOnVisibilityChange(callback);
    }

    @ReactProp(name = "height", defaultFloat = 64f)
    public void setHeight(VizbeeCastBarView view, float height) {
         Log.v(LOG_TAG, "setHeight - " + height);
        // Implementation not needed for setHeight method
    }
}
