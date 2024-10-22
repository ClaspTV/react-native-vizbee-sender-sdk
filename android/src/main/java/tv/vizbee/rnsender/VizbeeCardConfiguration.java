package tv.vizbee.rnsender;

import tv.vizbee.api.uiConfig.*;

public class VizbeeCardConfiguration {
    
    public static final String LOG_TAG = VizbeeVideo.class.getName();

    private String title;
    private String subtitle;
    private String ctaText;

    public VizbeeCardConfiguration(ReadableMap cardConfiguration) {
        
        title = cardConfiguration.hasKey("title") ? cardConfiguration.getString("title") : "";
        subtitle = cardConfiguration.hasKey("subtitle") ? cardConfiguration.getString("subtitle") : "";
        ctaText = cardConfiguration.hasKey("ctaText") ? cardConfiguration.getString("ctaText") : "";
    }

    public CardConfiguration getCardConfigurationForCardType(String cardType) {

        CardConfiguration cardConfiguration = nil;
        if (cardType.equals("CAST_INTRODUCTION")) {
            
            CastIntroductionCardConfiguration ciCardConfiguration = new CastIntroductionCardConfiguration();
            ciCardConfiguration.title = title;
            ciCardConfiguration.subtitle = subtitle;
            ciCardConfiguration.ctaText = ctaText;

            cardConfiguration = ciCardConfiguration;
        }

        return cardConfiguration;
    }
}
