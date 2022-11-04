package tv.vizbee.rnsender;

import android.util.Log;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.uimanager.ThemedReactContext;

import tv.vizbee.api.RemoteButton;

public class VizbeeCastButtonView extends LinearLayout implements LifecycleEventListener {

    public static final String TAG = VizbeeCastButtonView.class.getName();
    private RemoteButton m_button;

    public VizbeeCastButtonView(ThemedReactContext themedReactContext) {
        super(themedReactContext);

        m_button = new RemoteButton(themedReactContext.getCurrentActivity());
        this.addView(m_button);
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
        Log.i(TAG, "onHostResume");
    }

    @Override
    public void onHostPause() {
        Log.i(TAG, "onHostPause");
    }

    @Override
    public void onHostDestroy() {
        Log.i(TAG, "onHostDestroy");
    }
}
