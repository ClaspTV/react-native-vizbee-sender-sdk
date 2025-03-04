package tv.vizbee.rnsender;

import android.util.Log;
import android.view.Choreographer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.Map;

import tv.vizbee.api.CastBarFragment;
import tv.vizbee.api.CastBarVisibilityListener;

/**
 * A ViewGroupManager for managing Vizbee CastBarView in React Native.
 * This manager handles the creation of a custom FrameLayout that holds a CastBarFragment.
 * It also provides methods for updating the layout and handling visibility changes.
 */
public class VizbeeCastBarViewManager extends ViewGroupManager<FrameLayout> implements CastBarVisibilityListener {
    private static final String REACT_CLASS = "VizbeeCastBarView";
    private static final String LOG_TAG = "VZBRNSDK_CastBarViewManager";
    private static final String FRAGMENT_TAG = "cast_bar_fragment";
    private static final int COMMAND_CREATE = 1;

    private final ReactApplicationContext reactContext;
    private int propWidth;
    private int propHeight;
    private int reactNativeViewId;
    private CastBarFragment castBarFragment;

    public VizbeeCastBarViewManager(ReactApplicationContext context) {
        this.reactContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    /**
     * Return a FrameLayout which will later hold the Fragment
     */
    @NonNull
    @Override
    public FrameLayout createViewInstance(@NonNull ThemedReactContext reactContext) {
        return new FrameLayout(reactContext);
    }

    /**
     * Map the "create" command to an integer
     */
    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of("create", COMMAND_CREATE);
    }

    /**
     * Handle commands (called from JS)
     */
    @Override
    public void receiveCommand(@NonNull FrameLayout root, int commandId, @Nullable ReadableArray args) {
        super.receiveCommand(root, commandId, args);

        if (args != null && args.size() > 0) {
            reactNativeViewId = args.getInt(0);
        }

        if (commandId == COMMAND_CREATE) {
            createFragment(root, reactNativeViewId);
        }
    }

    @ReactPropGroup(names = {"width", "height"}, customType = "Style")
    public void setStyle(FrameLayout view, int index, Integer value) {
        if (index == 0) {
            propWidth = value;
        } else if (index == 1) {
            propHeight = value;
        }
    }

    /**
     * Replace your React Native view with a custom fragment
     */
    private void createFragment(FrameLayout root, int reactNativeViewId) {
        try {
            // Get the parent view
            ViewGroup parentView = (ViewGroup) root.findViewById(reactNativeViewId);
            if (parentView == null) {
                Log.e(LOG_TAG, "Parent view not found");
                return;
            }
            
            setupLayout(parentView);

            // Get the activity
            FragmentActivity activity = (FragmentActivity) reactContext.getCurrentActivity();
            if (!isValidForTransaction(activity, root)) {
                return;
            }

            // Create the fragment
            castBarFragment = new CastBarFragment();
            castBarFragment.setCallback(this);
            
            Log.v(LOG_TAG, "Executing fragment transaction");
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager.beginTransaction()
                .replace(reactNativeViewId, castBarFragment, FRAGMENT_TAG)
                .commitNow();
                
        } catch (Exception e) {
            // Don't rethrow, just log a warning
            String message = (e != null) ? e.getMessage() : "unknown error";
            Log.w(LOG_TAG, "Error attaching fragment: " + e.getMessage() + e);
        }
    }

    /**
     * Checks if the current state is valid for a fragment transaction
     */
    private boolean isValidForTransaction(FragmentActivity activity, View root) {
        // Check if activity is valid
        if (activity == null) {
            Log.e(LOG_TAG, "Activity not found");
            return false;
        }
        
        if (activity.isFinishing() || activity.isDestroyed()) {
            Log.i(LOG_TAG, "Activity is finishing or destroyed");
            return false;
        }
        
        // Check if fragment manager is valid
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager == null || fragmentManager.isDestroyed()) {
            Log.i(LOG_TAG, "FragmentManager is null or destroyed");
            return false;
        }
        
        return true;
    }
    
    /**
     * Sets up layout for the given ViewGroup by posting frame callbacks.
     */
    private void setupLayout(ViewGroup view) {
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                manuallyLayoutChildren(view);
                view.getViewTreeObserver().dispatchOnGlobalLayout();
                Choreographer.getInstance().postFrameCallback(this);
            }
        });
    }

    /**
     * Manually lays out the children of the given View with propWidth and propHeight.
     */
    private void manuallyLayoutChildren(View view) {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(propWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(propHeight, View.MeasureSpec.EXACTLY));

        view.layout(0, 0, propWidth, propHeight);
    }

    /**
     * Notifies React Native about the visibility change of the CastBar.
     */
    private void onCastBarVisibilityChange(boolean shouldAppear) {
        WritableMap event = Arguments.createMap();
        event.putBoolean("shouldAppear", shouldAppear);
        reactContext
            .getJSModule(RCTEventEmitter.class)
            .receiveEvent(reactNativeViewId, "onChange", event);
    }

    /**
     * Returns the map of exported custom bubbling event type constants for the CastBarView.
     */
    @Override
    public Map<String, Object> getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
            .put("onChange", MapBuilder.of(
                "phasedRegistrationNames",
                MapBuilder.of("bubbled", "onVisibilityChange")
            ))
            .build();
    }

    /**
     * Notifies the manager when the CastBar becomes visible.
     */
    @Override
    public void onCastBarShown() {
        Log.v(LOG_TAG, "CastBar is visible");
        onCastBarVisibilityChange(true);
    }

    /**
     * Notifies the manager when the CastBar becomes hidden.
     */
    @Override
    public void onCastBarHidden() {
        Log.v(LOG_TAG, "CastBar is hidden");
        onCastBarVisibilityChange(false);
    }
}