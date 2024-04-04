package tv.vizbee.rnsender;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import androidx.annotation.NonNull;
import com.facebook.react.uimanager.annotations.ReactProp;
import android.util.Log;

public class VizbeeCastButtonViewManager extends SimpleViewManager<VizbeeCastButtonView> {

    private static final String REACT_CLASS = "VizbeeCastButtonView";
    private static final String LOG_TAG = "VZBRNSDK_VizbeeCastButtonViewManager";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected VizbeeCastButtonView createViewInstance(ThemedReactContext reactContext) {
        return new VizbeeCastButtonView(reactContext);
    }

    @Override
    public void onDropViewInstance(VizbeeCastButtonView buttonView) {
        super.onDropViewInstance(buttonView);
    }

    @ReactProp(name = "tintColor")
    public void setTintColor(VizbeeCastButtonView view, @NonNull String tintColor) {
        Log.v(LOG_TAG, "setTintColor - " + tintColor);
        view.setTintColor(tintColor);
    }

     @ReactProp(name = "enabled")
    public void setEnabled(VizbeeCastButtonView view, boolean clickable) {
        view.setEnabled(clickable);
    }
}