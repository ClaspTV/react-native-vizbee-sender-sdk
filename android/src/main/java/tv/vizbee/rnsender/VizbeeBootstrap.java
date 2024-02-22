package tv.vizbee.rnsender;

import android.app.Application;

import androidx.annotation.StyleRes;

import org.json.JSONException;
import org.json.JSONObject;

import tv.vizbee.api.LayoutsConfig;
import tv.vizbee.api.VizbeeContext;
import tv.vizbee.api.VizbeeOptions;

public class VizbeeBootstrap {

  private static final String TAG = VizbeeBootstrap.class.getName();

  // singleton

  private static VizbeeBootstrap singleton = null;

  public static VizbeeBootstrap getInstance() {
    if (singleton == null) {
        singleton = new VizbeeBootstrap();
    }

    return singleton;
  }

  private VizbeeBootstrap() {}

  public void initialize(Application applicationContext, String vizbeeAppId) {
    initialize(applicationContext, vizbeeAppId, true);
  }

  public void initialize(Application applicationContext, String vizbeeAppId, boolean isProduction) {
      VizbeeOptions options = new VizbeeOptions.Builder().enableProduction(isProduction).build();
      initialize(applicationContext, vizbeeAppId, options);
  }

  public void initialize(Application applicationContext, String vizbeeAppId, JSONObject options) {

      // update layoutsconfig with the player card configuration
      LayoutsConfig layoutsConfig = LayoutsConfig.getDefaultLayoutForVizbeeFutura();
      try {
          if (options.has("playerCardConfiguration") &&
              null != options.getJSONObject("playerCardConfiguration")) {
              layoutsConfig.setPlayerCardConfiguration(options.getJSONObject("playerCardConfiguration"));
          }
      } catch (JSONException e) {
          e.printStackTrace();
      }

      // add custom metrics attributes to be tracked along with all vizbee events
      JSONObject customMetricsAttributes = new JSONObject();
      try {
          if (options.has("customMetricsAttributes") &&
              null != options.getJSONObject("customMetricsAttributes")) {
              customMetricsAttributes = options.getJSONObject("customMetricsAttributes");
          }
      } catch (JSONException e) {
          e.printStackTrace();
      }

      // set options
      VizbeeOptions vizbeeOptions = new VizbeeOptions.Builder()
          .setLayoutsConfig(layoutsConfig)
          .setCustomMetricsAttributes(customMetricsAttributes)
          .build();

      // initialize the sdk with the options
      initialize(applicationContext, vizbeeAppId, vizbeeOptions);
  }

  public void initialize(Application applicationContext, String vizbeeAppId, VizbeeOptions options) {
      VizbeeContext.getInstance().init(applicationContext, vizbeeAppId, new VizbeeAppAdapter(), options);
  }
}
