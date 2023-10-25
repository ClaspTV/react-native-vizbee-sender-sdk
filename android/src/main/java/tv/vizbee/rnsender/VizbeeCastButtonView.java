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
