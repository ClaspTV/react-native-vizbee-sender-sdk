package tv.vizbee.rnsender;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

/**
 * A ViewGroupManager for managing VizbeeCastBarView in React Native.
 */
public class VizbeeCastBarViewManager extends ViewGroupManager<VizbeeCastBarView> {
    private static final String REACT_CLASS = "VizbeeCastBarView";
    private static final String LOG_TAG = "VZBRNSDK_VizbeeCastBarViewManager";

    private final ReactApplicationContext reactContext;

    public VizbeeCastBarViewManager(ReactApplicationContext context) {
        this.reactContext = context;
        Log.d(LOG_TAG, "VizbeeCastBarViewManager initialized");
    }

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    /**
     * Return a VizbeeCastBarView instance
     */
    @NonNull
    @Override
    public VizbeeCastBarView createViewInstance(@NonNull ThemedReactContext reactContext) {
        Log.d(LOG_TAG, "Creating VizbeeCastBarView instance");
        return new VizbeeCastBarView(reactContext);
    }

    /**
     * Set height property
     */
    @ReactProp(name = "height", defaultInt = 64)
    public void setHeight(VizbeeCastBarView view, int height) {
        Log.d(LOG_TAG, "Setting height: " + height);
        view.setHeight(height);
    }

    /**
     * Returns the map of exported custom bubbling event type constants
     */
    @Override
    public Map<String, Object> getExportedCustomBubblingEventTypeConstants() {
        Log.d(LOG_TAG, "Exporting event constants");
        return MapBuilder.<String, Object>builder()
            .put("onChange", MapBuilder.of(
                "phasedRegistrationNames",
                MapBuilder.of("bubbled", "onVisibilityChange")
            ))
            .build();
    }
    
    @Override
    public void onDropViewInstance(VizbeeCastBarView view) {
        Log.d(LOG_TAG, "Dropping view instance");
        super.onDropViewInstance(view);
    }
}