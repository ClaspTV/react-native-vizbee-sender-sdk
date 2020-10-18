package tv.vizbee.rnsender;

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
    public void onDropViewInstance(VizbeeCastButtonView buttonView) {
        super.onDropViewInstance(buttonView);
    }
}