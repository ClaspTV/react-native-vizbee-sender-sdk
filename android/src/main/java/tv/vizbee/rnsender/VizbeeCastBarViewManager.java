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
 * Supports React Native versions 0.60 - 0.81 with both old and new architecture.
 */
public class VizbeeCastBarViewManager extends ViewGroupManager<VizbeeCastBarView> {
    private static final String REACT_CLASS = "VizbeeCastBarView";
    private static final String LOG_TAG = "VZBRNSDK_VizbeeCastBarViewManager";

    private final ReactApplicationContext reactContext;

    public VizbeeCastBarViewManager(ReactApplicationContext context) {
        this.reactContext = context;
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
     * Direct event constants 
     */
    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
            Log.d(LOG_TAG, "Exporting direct event constants for new architecture");
            return MapBuilder.<String, Object>builder()
                .put("topVisibilityChange", MapBuilder.of("registrationName", "onVisibilityChange"))
                .build();
    }

    @Override
    public void onDropViewInstance(VizbeeCastBarView view) {
        Log.d(LOG_TAG, "Dropping view instance");
        super.onDropViewInstance(view);
    }
}