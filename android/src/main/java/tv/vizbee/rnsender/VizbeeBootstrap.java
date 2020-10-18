package tv.vizbee.rnsender;

import android.app.Application;

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

  public void initialize(Application applicationContext, String vizbeeAppId, VizbeeOptions options) {
      VizbeeContext.getInstance().init(applicationContext, vizbeeAppId, new VizbeeAppAdapter(), options);
  }

}