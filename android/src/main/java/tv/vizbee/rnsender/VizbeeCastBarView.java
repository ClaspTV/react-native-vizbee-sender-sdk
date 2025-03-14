package tv.vizbee.rnsender;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Choreographer;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import tv.vizbee.api.CastBarFragment;
import tv.vizbee.api.CastBarVisibilityListener;

/**
 * A custom FrameLayout that manages its own lifecycle for the CastBarFragment.
 * This component handles attaching and detaching the fragment automatically.
 */
public class VizbeeCastBarView extends FrameLayout implements CastBarVisibilityListener {
    private static final String LOG_TAG = "VZBRNSDK_VizbeeCastBarView";
    private static final String FRAGMENT_TAG = "cast_bar_fragment";
    private static final int ATTACHMENT_DELAY_MS = 500; // 500ms delay

    private final ThemedReactContext reactContext;
    private CastBarFragment castBarFragment;
    private int width;
    private int height; // Default height of 64
    private Choreographer.FrameCallback frameCallback;
    private boolean isFragmentAttached = false;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public VizbeeCastBarView(@NonNull ThemedReactContext reactContext) {
        super(reactContext);
        Log.d(LOG_TAG, "Constructor called");
        this.reactContext = reactContext;

        float density = getResources().getDisplayMetrics().density;
        height = (int) (64 * density);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(LOG_TAG, "onAttachedToWindow");
        // To avoid rapid attach/detach calls, we delay the fragment attachment
        mainHandler.postDelayed(this::attachFragmentIfNeeded, ATTACHMENT_DELAY_MS);
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d(LOG_TAG, "onDetachedFromWindow");
        super.onDetachedFromWindow();
    }

    /**
     * Attaches the CastBarFragment if it hasn't been attached yet and conditions are met
     */
    private void attachFragmentIfNeeded() {
        if (isFragmentAttached) {
            Log.d(LOG_TAG, "Fragment already attached, skipping attachment");
            return;
        }
        
        try {
            FragmentActivity activity = (FragmentActivity) reactContext.getCurrentActivity();
            
            if (!isValidForTransaction(activity)) {
                return;
            }

            int viewId = getId();
            if (viewId <= 0 || getWindowToken() == null) {
                Log.d(LOG_TAG, "Invalid view ID or window token");
                return;
            }

            // Create the fragment
            castBarFragment = new CastBarFragment();
            castBarFragment.setCallback(this);
            
            // It is not possible to add a fragment to a view with height zero of frame layout
            // So, we have to handle height in a javascript layer
            Log.d(LOG_TAG, "Attaching CastBarFragment with ID: " + viewId);
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            if(fragmentManager == null) {
                Log.d(LOG_TAG, "Fragment manager is null");
                return;
            }
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(viewId, castBarFragment, FRAGMENT_TAG);
            transaction.commit();
            
        } catch (Exception e) {
            String message = (e != null) ? e.getMessage() : "unknown error";
            Log.e(LOG_TAG, "Error attaching fragment: " + message, e);
        } finally {
            isFragmentAttached = true;
            requestLayout();
        }
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        Log.d(LOG_TAG, "VizbeeCastBarView - requestLayout");
        
        // Only post the measureAndLayout runnable if the fragment is attached
        if (isFragmentAttached && castBarFragment != null) {
            post(measureAndLayout);
        } else {
            Log.d(LOG_TAG, "Skipping measureAndLayout - fragment not attached");
        }
    }

    private final Runnable measureAndLayout = new Runnable() {
        @Override
        public void run() {
            // Double-check the fragment is still attached when this runs
            if (isFragmentAttached && castBarFragment != null) {
                measure(
                    MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
                );
                Log.d(LOG_TAG, "VizbeeCastBarView - measureAndLayout");
                layout(getLeft(), getTop(), getRight(), getBottom());
            }
        }
    };

    /**
     * Checks if the current state is valid for a fragment transaction
     */
    private boolean isValidForTransaction(FragmentActivity activity) {
        // Check if activity is valid
        if (activity == null) {
            Log.e(LOG_TAG, "Activity not found");
            return false;
        }
        
        if (activity.isFinishing() || activity.isDestroyed()) {
            Log.d(LOG_TAG, "Activity is finishing or destroyed");
            return false;
        }
        
        // Check if fragment manager is valid
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager == null || fragmentManager.isDestroyed()) {
            Log.d(LOG_TAG, "FragmentManager is null or destroyed");
            return false;
        }
        
        return true;
    }

    /**
     * Set the height for this layout
     */
    public void setHeight(int height) {
        float density = getResources().getDisplayMetrics().density;
        this.height = (int) (height * density);
        Log.d(LOG_TAG, "Setting height: " + this.height);
        requestLayout();
    }

    /**
     * Notifies React Native about the visibility change of the CastBar
     */
    private void onCastBarVisibilityChange(boolean shouldAppear) {
        if (reactContext == null) {
            Log.d(LOG_TAG, "Cannot emit visibility change - React context is null");
            return;
        }
        
        try {
            WritableMap event = Arguments.createMap();
            event.putBoolean("shouldAppear", shouldAppear);
            Log.d(LOG_TAG, "Emitting visibility change event: shouldAppear=" + shouldAppear);
            reactContext
                .getJSModule(RCTEventEmitter.class)
                .receiveEvent(getId(), "onChange", event);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error emitting visibility change: " + e.getMessage());
        }
    }

    /**
     * CastBarVisibilityListener implementation
     */
    @Override
    public void onCastBarShown() {
        Log.d(LOG_TAG, "CastBar is visible");
        onCastBarVisibilityChange(true);
    }

    @Override
    public void onCastBarHidden() {
        Log.d(LOG_TAG, "CastBar is hidden");
        onCastBarVisibilityChange(false);
    }
}