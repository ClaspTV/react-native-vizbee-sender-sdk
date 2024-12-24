package tv.vizbee.rnsender;

import android.os.Handler;
import android.util.Log;
import android.view.Choreographer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
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
public class VizbeeCastBarViewManager extends ViewGroupManager < FrameLayout > implements CastBarVisibilityListener {
    private static final String REACT_CLASS = "VizbeeCastBarView";
    private static final String LOG_TAG = "VZBRNSDK_VizbeeCastBarViewManager";

    private final int COMMAND_CREATE = 1;

    private ReactApplicationContext reactContext;

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
    public FrameLayout createViewInstance(ThemedReactContext reactContext) {
        return new FrameLayout(reactContext);
    }

    /**
     * Map the "create" command to an integer
     */
    @Nullable
    @Override
    public Map < String, Integer > getCommandsMap() {
        return MapBuilder.of("create", COMMAND_CREATE);
    }

    /**
     * Handle "create" command (called from JS) and call createFragment method
     */
    @Override
    public void receiveCommand(@NonNull FrameLayout root, int commandId, @Nullable ReadableArray args) {
        super.receiveCommand(root, commandId, args);
        reactNativeViewId = args.getInt(0);

        switch (commandId) {
            case COMMAND_CREATE:
                createFragment(root, reactNativeViewId);
                break;
            default:
                {}
        }
    }

    @ReactPropGroup(names = {
        "width",
        "height"
    }, customType = "Style")
    public void setStyle(FrameLayout view, int index, Integer value) {
        if (index == 0) {
            propWidth = value;
        }

        if (index == 1) {
            propHeight = value;
        }
    }

    /**
     * Replace your React Native view with a custom fragment
     */
    private void createFragment(FrameLayout root, int reactNativeViewId) {
        ViewGroup parentView = (ViewGroup) root.findViewById(reactNativeViewId);
        setupLayout(parentView);

        FragmentActivity activity = (FragmentActivity) reactContext.getCurrentActivity();
        if (activity == null) {
            Log.e(LOG_TAG, "activity - not found");
            return;
        }

        castBarFragment = new CastBarFragment();
        castBarFragment.setCallback(this);
        Log.v(LOG_TAG, "castBarFragment - replacing framelayout with castBarFragment");
        activity.getSupportFragmentManager()
            .beginTransaction()
            .replace(reactNativeViewId, castBarFragment, String.valueOf(reactNativeViewId))
            .commit();
    }
    
    /**
     * Sets up layout for the given ViewGroup by continuously posting frame callbacks.
     * This ensures that the children are properly laid out.
     *
     * @param view The ViewGroup whose layout needs to be set up.
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
     * propWidth and propHeight are coming from React Native props.
     *
     * @param view The View whose children need to be manually laid out.
     */
    private void manuallyLayoutChildren(View view) {
        int width = propWidth;
        int height = propHeight;

        view.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));

        view.layout(0, 0, width, height);
    }

    /**
     * Notifies React Native about the visibility change of the CastBar.
     *
     * @param shouldAppear Indicates whether the CastBar should appear or disappear.
     */
    private void onCastBarVisibilityChange(Boolean shouldAppear) {
        WritableMap event = Arguments.createMap();
        event.putBoolean("shouldAppear", shouldAppear);
        reactContext
            .getJSModule(RCTEventEmitter.class)
            .receiveEvent(reactNativeViewId, "onChange", event);
    }

    /**
     * Returns the map of exported custom bubbling event type constants for the CastBarView.
     *
     * @return The map containing the event type constants.
     */
    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder().put(
            "onChange",
            MapBuilder.of(
                "phasedRegistrationNames",
                MapBuilder.of("bubbled", "onVisibilityChange")
            )
        ).build();
    }

    /**
     * Notifies the manager when the CastBar becomes visible.
     * This method is part of the CastBarVisibilityListener interface.
     * It logs the visibility change and invokes onCastBarVisibilityChange with true.
     */
    @Override
    public void onCastBarShown() {
        // Method called when the CastBar becomes visible
        Log.v(LOG_TAG, "castBar is visible");
        onCastBarVisibilityChange(true);
    }

    /**
     * Notifies the manager when the CastBar becomes hidden.
     * This method is part of the CastBarVisibilityListener interface.
     * It logs the visibility change and invokes onCastBarVisibilityChange with false.
     */
    @Override
    public void onCastBarHidden() {
        // Method called when the CastBar becomes hidden
        Log.v(LOG_TAG, "castBar is hidden");
        onCastBarVisibilityChange(false);
    }
}