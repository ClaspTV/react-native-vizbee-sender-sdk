package tv.vizbee.rnsender;

import android.support.annotation.Nullable;

import java.util.Map;
import java.util.HashMap;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

public class VizbeeCastButtonViewManager extends SimpleViewManager<VizbeeCastButtonView> {

    private static final String REACT_CLASS = "VizbeeCastButtonView";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected VizbeeCastButtonView createViewInstance(ThemedReactContext reactContext) {
        return new VizbeeCastButtonView(reactContext);
    }

    @Override
    public void onDropViewInstance(CastButtonView buttonView) {
        super.onDropViewInstance(buttonView);
    }

    @Override
    @Nullable
    public Map getExportedViewConstants() {
        Map<String, Object> map = new HashMap<>();
        map.put("ScaleNone", "0");
        map.put("ScaleToFill", "1");
        map.put("ScaleAspectFit", "2");
        map.put("ScaleAspectFill", "3");
        return map;
    }
}