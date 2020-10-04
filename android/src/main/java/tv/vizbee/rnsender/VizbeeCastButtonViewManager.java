package tv.vizbee.rnsender;

import android.support.annotation.Nullable;

import java.util.Map;
import java.util.HashMap;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

public class VizbeeCastButtonViewManager extends SimpleViewManager<CastButtonView> {

    private static final String REACT_CLASS = "VizbeeCastButtonView";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected VizbeeCastButtonView createViewInstance(ThemedReactContext reactContext) {
        return new VizbeeCastButtonView(reactContext);
    }
}