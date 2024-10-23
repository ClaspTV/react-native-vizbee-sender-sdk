import VizbeeUICardConfiguration from './VizbeeUICardConfiguration.js';

/**
 * Configuration class for customizing the Cast Introduction card UI.
 * 
 * This class extends VizbeeUICardConfiguration to provide specific customization
 * options for the card that introduces casting functionality to users. It inherits
 * all basic customization properties (title, subtitle, ctaText) from its parent class.
 *
 * Use this configuration when you want to customize the appearance of the card
 * that helps users understand casting features, typically shown to new users
 * or when introducing casting capabilities.
 *
 * Note: It is recommended to use cloud configuration instead of API customization
 * for card content. API customization should only be used for specific use cases
 * that require dynamic content.
 *
 * @extends VizbeeUICardConfiguration
 * @example
 * const config = new VizbeeCastIntroductionCardConfiguration();
 * config.title = "Watch on TV";
 * config.subtitle = "Use <icon_cast> icon to cast videos from <app_name> app to nearby streaming devices and smart TVs.";
 * config.ctaText = "Get Started";
 */
export default class VizbeeCastIntroductionCardConfiguration extends VizbeeUICardConfiguration {
    constructor() {
        super();
    }
}