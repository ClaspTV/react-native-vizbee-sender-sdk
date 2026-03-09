package tv.vizbee.rnsender;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import tv.vizbee.api.CastBarFragment;
import tv.vizbee.api.CastBarVisibilityListener;

import android.widget.FrameLayout;

/**
 * A custom FrameLayout that manages its own lifecycle for the CastBarFragment.
 * This component handles attaching and detaching the fragment automatically.
 */
public class VizbeeCastBarView extends FrameLayout implements CastBarVisibilityListener {
    private static final String LOG_TAG = "VZBRNSDK_VizbeeCastBarView";
    private static final String FRAGMENT_TAG = "cast_bar_fragment";
    private static final int ATTACHMENT_DELAY_MS = 500;
    private static final int ATTACHMENT_RETRY_DELAY_MS = 500;
    private static final int MAX_ATTACH_RETRIES = 10;

    private final ThemedReactContext reactContext;
    private CastBarFragment castBarFragment;
    private int height;
    private boolean isFragmentAttached = false;
    private int attachRetryCount = 0;
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
        attachRetryCount = 0;
        // Delay to avoid rapid attach/detach calls
        mainHandler.postDelayed(this::attachFragmentIfNeeded, ATTACHMENT_DELAY_MS);
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d(LOG_TAG, "onDetachedFromWindow");
        // Cancel any pending attach retries
        mainHandler.removeCallbacksAndMessages(null);
        // Remove the fragment so it won't be restored after activity recreation
        detachFragment();
        super.onDetachedFromWindow();
    }

    /**
     * Attaches the CastBarFragment if it hasn't been attached yet and all conditions are met.
     * Will retry automatically if the view is not yet findable from the activity.
     */
    private void attachFragmentIfNeeded() {
        if (isFragmentAttached) {
            Log.d(LOG_TAG, "Fragment already attached, skipping");
            return;
        }

        // Ensure the view is truly attached to the window
        if (!isAttachedToWindow() || getWindowToken() == null) {
            Log.d(LOG_TAG, "View not attached to window yet, skipping");
            return;
        }

        // Ensure the view has a valid ID
        int viewId = getId();
        if (viewId <= 0 || viewId == View.NO_ID) {
            Log.d(LOG_TAG, "Invalid view ID, skipping");
            return;
        }

        try {
            FragmentActivity activity = (FragmentActivity) reactContext.getCurrentActivity();
            if (!isValidForTransaction(activity)) return;

            FragmentManager fm = activity.getSupportFragmentManager();

            // Guard: never touch FragmentManager while a transaction is already executing.
            // isStateSaved() returns true after onSaveInstanceState — committing would throw.
            // executePendingTransactions() flushes any async commits so we don't collide.
            if (fm.isStateSaved()) {
                Log.w(LOG_TAG, "FragmentManager state already saved, will retry");
                scheduleRetry();
                return;
            }

            // Flush any pending async transactions to avoid conflicts
            try {
                fm.executePendingTransactions();
            } catch (IllegalStateException e) {
                Log.w(LOG_TAG, "executePendingTransactions failed, will retry: " + e.getMessage());
                scheduleRetry();
                return;
            }

            // Critical check: verify the activity can actually find this view.
            // This mirrors what FragmentStateManager.createView() does internally,
            // so if this returns null the fragment transaction would crash with
            // "No view found for id" IllegalArgumentException.
            View containerInActivity = activity.findViewById(viewId);
            if (containerInActivity == null) {
                Log.w(LOG_TAG, "View ID " + viewId + " not findable from activity yet, will retry");
                scheduleRetry();
                return;
            }

            // Remove any stale fragment that may have been restored from saved state
            // with the same tag but pointing at a now-invalid view ID.
            Fragment stale = fm.findFragmentByTag(FRAGMENT_TAG);
            if (stale != null) {
                Log.d(LOG_TAG, "Removing stale fragment from previous instance");
                fm.beginTransaction()
                    .remove(stale)
                    .commitNowAllowingStateLoss();
            }

            castBarFragment = new CastBarFragment();
            castBarFragment.setCallback(this);

            Log.d(LOG_TAG, "Attaching CastBarFragment with ID: " + viewId);
            fm.beginTransaction()
                .replace(viewId, castBarFragment, FRAGMENT_TAG)
                .commitNowAllowingStateLoss();

            // Only mark as attached after a successful commit
            isFragmentAttached = true;
            attachRetryCount = 0;
            requestLayout();

        } catch (IllegalStateException e) {
            // Catch-all for "Can not perform this action" variants
            Log.e(LOG_TAG, "IllegalStateException attaching fragment, will retry: " + e.getMessage());
            scheduleRetry();
        } catch (Exception e) {
            String message = (e != null) ? e.getMessage() : "unknown error";
            Log.e(LOG_TAG, "Error attaching fragment: " + message, e);
        }
    }

    /**
     * Schedules a retry of attachFragmentIfNeeded with bounded retry count.
     */
    private void scheduleRetry() {
        if (attachRetryCount >= MAX_ATTACH_RETRIES) {
            Log.e(LOG_TAG, "Max attach retries (" + MAX_ATTACH_RETRIES + ") exceeded, giving up");
            return;
        }
        attachRetryCount++;
        Log.d(LOG_TAG, "Scheduling attach retry " + attachRetryCount + "/" + MAX_ATTACH_RETRIES);
        mainHandler.postDelayed(this::attachFragmentIfNeeded, ATTACHMENT_RETRY_DELAY_MS);
    }

    /**
     * Safely removes the CastBarFragment from the FragmentManager.
     */
    private void detachFragment() {
        if (!isFragmentAttached || castBarFragment == null) {
            return;
        }

        try {
            FragmentActivity activity = (FragmentActivity) reactContext.getCurrentActivity();
            if (!isValidForTransaction(activity)) {
                // Activity is gone — fragment will be destroyed with it, just clean up refs.
                isFragmentAttached = false;
                castBarFragment = null;
                return;
            }

            FragmentManager fm = activity.getSupportFragmentManager();

            // If state is already saved, we can still use commitNowAllowingStateLoss.
            // But if the FM is destroyed, skip entirely.
            if (fm.isDestroyed()) {
                Log.d(LOG_TAG, "FragmentManager destroyed, skipping detach");
                isFragmentAttached = false;
                castBarFragment = null;
                return;
            }

            fm.beginTransaction()
                .remove(castBarFragment)
                .commitNowAllowingStateLoss();

            Log.d(LOG_TAG, "CastBarFragment removed successfully");
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error removing fragment: " + e.getMessage());
        }

        isFragmentAttached = false;
        castBarFragment = null;
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        Log.d(LOG_TAG, "VizbeeCastBarView - requestLayout");

        if (isFragmentAttached && castBarFragment != null) {
            post(measureAndLayout);
        } else {
            Log.d(LOG_TAG, "Skipping measureAndLayout - fragment not attached");
        }
    }

    private final Runnable measureAndLayout = new Runnable() {
        @Override
        public void run() {
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
     * Checks if the current state is valid for a fragment transaction.
     */
    private boolean isValidForTransaction(FragmentActivity activity) {
        if (activity == null) {
            Log.e(LOG_TAG, "Activity not found");
            return false;
        }

        if (activity.isFinishing() || activity.isDestroyed()) {
            Log.d(LOG_TAG, "Activity is finishing or destroyed");
            return false;
        }

        FragmentManager fm = activity.getSupportFragmentManager();
        if (fm == null || fm.isDestroyed()) {
            Log.d(LOG_TAG, "FragmentManager is null or destroyed");
            return false;
        }

        return true;
    }

    /**
     * Sets the height for this layout (value in dp, converted to px internally).
     */
    public void setHeight(int height) {
        float density = getResources().getDisplayMetrics().density;
        this.height = (int) (height * density);
        Log.d(LOG_TAG, "Setting height: " + this.height);
        requestLayout();
    }

    /**
     * Notifies React Native about the visibility change of the CastBar.
     */
    private void onCastBarVisibilityChange(boolean shouldAppear) {
        if (reactContext == null) {
            Log.d(LOG_TAG, "Cannot emit visibility change - React context is null");
            return;
        }

        try {
            WritableMap event = Arguments.createMap();
            event.putBoolean("shouldAppear", shouldAppear);
            reactContext
                .getJSModule(RCTEventEmitter.class)
                .receiveEvent(getId(), "topVisibilityChange", event);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error emitting visibility change: " + e.getMessage());
        }
    }

    // --- CastBarVisibilityListener ---

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