package tv.vizbee.rnsender;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import androidx.annotation.Nullable;
import com.facebook.react.uimanager.annotations.ReactProp;
import android.util.Log;

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

    @ReactProp(name = "color")
    public void setColor(VizbeeCastButtonView view, @Nullable String color) {
        Log.d(REACT_CLASS,"setColor color = " + color + " view =" + view);
        view.setColor(color);
    }
}