package tv.vizbee.rnsender;

import android.view.View;
import android.util.Log;
import android.widget.LinearLayout;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.uimanager.ThemedReactContext;

import androidx.annotation.NonNull;

import tv.vizbee.api.MiniCastBarFragment;

public class VizbeeCastBarView extends LinearLayout implements LifecycleEventListener {

    private static final String LOG_TAG = "VZBRNSDK_VizbeeCastBarView";

    public VizbeeCastBarView(ThemedReactContext themedReactContext) {
        super(themedReactContext);

    }

    private void setup() {
        // Implement setup method
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Implement onDetachedFromWindow method
    }

    public boolean getActive() {
        // Implement isActive method
        return false;
    }

    public float getMinHeight() {
        // Implement getMinHeight method
        return 0f;
    }

    public void setOnVisibilityChange(Callback onVisibilityChange) {
        // Implement handleVisibilityChange method
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
