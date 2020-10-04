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

        m_button = new RemoteButton(themedReactContext);

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int lr_padding = (int) (8 * displayMetrics.density);
        int bt_padding = (int) (6 * displayMetrics.density);

        m_button.setPadding(lr_padding, bt_padding, lr_padding, bt_padding);

        this.addView(m_button);
    }
}