package tv.vizbee.rnsender;

import tv.vizbee.api.uiConfig.cardConfig.CardConfiguration;
import tv.vizbee.api.uiConfig.cardConfig.smartHelp.CastIntroductionCardConfiguration;
import tv.vizbee.api.uiConfig.cardConfig.smartHelp.SmartInstallCardConfiguration;
import tv.vizbee.api.uiConfig.cardConfig.smartHelp.GuidedSmartInstallCardConfiguration;
import tv.vizbee.api.uiConfig.cardConfig.smartHelp.MultiDeviceSmartInstallCardConfiguration;

import com.facebook.react.bridge.ReadableMap;

public class VizbeeUICardConfiguration {

    public static final String LOG_TAG = VizbeeVideo.class.getName();

    private String title;
    private String subtitle;
    private String ctaText;

    public VizbeeUICardConfiguration(ReadableMap cardConfiguration) {

        title = cardConfiguration.hasKey("title") ? cardConfiguration.getString("title") : null;
        subtitle = cardConfiguration.hasKey("subtitle") ? cardConfiguration.getString("subtitle") : null;
        ctaText = cardConfiguration.hasKey("ctaText") ? cardConfiguration.getString("ctaText") : null;
    }

    public CardConfiguration getCardConfigurationForType(String cardType) {

        // create a specific card instance and set specific properties
        CardConfiguration cardConfiguration = null;
        if (cardType.equals("CAST_INTRODUCTION")) {
            cardConfiguration = new CastIntroductionCardConfiguration();
        } else if (cardType.equals("SMART_INSTALL")) {
            cardConfiguration = new SmartInstallCardConfiguration();
        } else if (cardType.equals("GUIDED_SMART_INSTALL")) {
            cardConfiguration = new GuidedSmartInstallCardConfiguration();
        } else if (cardType.equals("MULTI_DEVICE_SMART_INSTALL")) {
            cardConfiguration = new MultiDeviceSmartInstallCardConfiguration();
        }

        // common attributes
        if (null != cardConfiguration) {
            cardConfiguration.setTitle(title);
            cardConfiguration.setSubtitle(subtitle);
            cardConfiguration.setCtaText(ctaText);
        }

        return cardConfiguration;
    }
}
