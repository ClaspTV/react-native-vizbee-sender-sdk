package tv.vizbee.rnsender;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import androidx.annotation.NonNull;
import com.facebook.react.uimanager.annotations.ReactProp;
import android.util.Log;
import androidx.annotation.Nullable;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.bridge.ReadableArray;
import java.util.Map;

public class VizbeeCastButtonViewManager extends SimpleViewManager<VizbeeCastButtonView> {

    private static final String REACT_CLASS = "VizbeeCastButtonView";
    private static final String LOG_TAG = "VZBRNSDK_VizbeeCastButtonViewManager";
    
    // Custom command IDs
    private static final int COMMAND_SIMULATE_BUTTON_CLICK = 1;

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

     @Override
    public Map<String, Integer> getCommandsMap() {
        // Define the commands and their IDs
        return MapBuilder.of(
            "simulateButtonClick", COMMAND_SIMULATE_BUTTON_CLICK
        );
    }

    @Override
    public void receiveCommand(VizbeeCastButtonView view, int commandId, @Nullable ReadableArray args) {
        // Handle the command based on its ID
        switch (commandId) {
            case COMMAND_SIMULATE_BUTTON_CLICK:
                // Call the method to simulate button click
                view.simulateButtonClick();
                break;
            default:
                // Handle unknown command
                break;
        }
    }
}