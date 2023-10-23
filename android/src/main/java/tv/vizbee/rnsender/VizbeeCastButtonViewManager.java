package tv.vizbee.rnsender;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.common.MapBuilder;
import java.util.Map;
import android.util.Log;

public class VizbeeCastButtonViewManager extends SimpleViewManager<VizbeeCastButtonView> {

    private static final String REACT_CLASS = "VizbeeCastButtonView";

    ReactApplicationContext reactContext;

    public final int COMMAND_UPDATE_COLOR = 1;

    public VizbeeCastButtonViewManager (ReactApplicationContext reactContext) {
      this.reactContext = reactContext;
    }

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

    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of("setColor", COMMAND_UPDATE_COLOR);
    }

    @Override
    public void receiveCommand(
    @NonNull VizbeeCastButtonView root,
    String commandId,
    @Nullable ReadableArray args
    ) {
        super.receiveCommand(root, commandId, args);
        Log.d(REACT_CLASS, "receiveCommand root = " + root + "commandId = " +  commandId + "args = " + args);
        int commandIdInt = Integer.parseInt(commandId);
        String color = args.getString(0);
        
        switch (commandIdInt) {
        case COMMAND_UPDATE_COLOR:
            updateColor(root, color);
        break;
        default: {}
        }
    }

    public void updateColor(VizbeeCastButtonView view, String color) {
       Log.d(REACT_CLASS,"updateColor color = " + color + " view =" +  view);
       view.setColor(color);
    }

    @ReactProp(name = "color")
    public void setColor(VizbeeCastButtonView view, @Nullable String color) {
        Log.d(REACT_CLASS,"setColor color = " + color + " view =" + view);
        view.setColor(color);
    }
}