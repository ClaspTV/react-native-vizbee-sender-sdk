package tv.vizbee.rnsender;

import android.util.Log;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.uimanager.ThemedReactContext;

import tv.vizbee.api.RemoteButton;

public class VizbeeCastButtonView extends LinearLayout implements LifecycleEventListener {

    private static final String LOG_TAG = "VZBRNSDK_VizbeeCastButtonView";

    private RemoteButton m_button;

    public VizbeeCastButtonView(ThemedReactContext themedReactContext) {
        super(themedReactContext);

        m_button = new RemoteButton(themedReactContext.getCurrentActivity());
        this.addView(m_button);
    }

    public void setTintColor(String tintColor){
        Log.v(LOG_TAG, "setTintColor - " + tintColor);
        if (null != m_button && null != tintColor) {
            m_button.setDrawableTintColor(tintColor);
        } else {
            Log.w(LOG_TAG, "setTintColor - failed to set the tint color " + tintColor);
        }
    }

     public void setEnabled(boolean clickable) {
        Log.v(LOG_TAG, "setEnabled - " + clickable);
        if (m_button != null) {
            m_button.setEnabled(clickable);
        } else {
            Log.w(LOG_TAG, "setEnabled - Remote button is not initialized");
        }
    }

    public void simulateButtonClick() {
        // Check if the remote button is initialized
        if (m_button != null && m_button.getVisibility() == VISIBLE) {
            Log.v(LOG_TAG, "simulateButtonClick - Remote button is visible " + m_button.getVisibility());
            // Programmatically perform a click on the remote button
            m_button.performClick();
        } else if (m_button != null && m_button.getVisibility() == VISIBLE){
            Log.v(LOG_TAG, "simulateButtonClick - Remote button is not visible");
        } else {
            Log.w(LOG_TAG, "simulateButtonClick - Remote button is not initialized");
        }
    }

    // PROBLEM: 
    // If a sub view 'm_button' of native view's visibility is set to GONE (`setVisibility(GONE))`
    // before it render on the screen and later if we set it's visibility to VISIBLE (`setVisibility(VISIBLE)`) 
    // it will not show on the screen as its width and height are both zero, but its visibility is VISIBLE.
    //
    // SOLUTION: 
    // Re-measure the size and layout from the requestLayout() override.
    //
    // REFERENCES:
    // https://github.com/homeeondemand/react-native-mapbox-navigation/commit/4fceba358224cb3bc5ee9a649320723c4dea0ea8
    // https://github.com/facebook/react-native/issues/17968#issuecomment-457236577
    // https://github.com/facebook/react-native/issues/5531
    
    @Override
    public void requestLayout() {
        super.requestLayout();
        Log.v(LOG_TAG, "VizbeeCastButtonView - requestLayout");
        post(measureAndLayout);
    }

    private final Runnable measureAndLayout = new Runnable() {
        @Override
        public void run() {
            measure(
                MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY)
            );
            Log.v(LOG_TAG, "VizbeeCastButtonView - measureAndLayout");
            layout(getLeft(), getTop(), getRight(), getBottom());
        }
    };

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void onHostResume() {
        Log.i(LOG_TAG, "onHostResume");
    }

    @Override
    public void onHostPause() {
        Log.i(LOG_TAG, "onHostPause");
    }

    @Override
    public void onHostDestroy() {
        Log.i(LOG_TAG, "onHostDestroy");
    }
}
