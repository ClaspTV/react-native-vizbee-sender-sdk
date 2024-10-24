import VizbeeUICardConfiguration from './VizbeeUICardConfiguration.js';

/**
 * Configuration class for customizing the Smart Install card UI.
 * 
 * This class extends VizbeeUICardConfiguration to provide specific customization
 * options for the card that guides users through the app installation process.
 * It inherits all basic customization properties (title, subtitle, ctaText)
 * from its parent class.
 *
 * Use this configuration when you want to customize the appearance of the card
 * that promotes app installation on the target device for an enhanced viewing
 * experience.
 *
 * Note: It is recommended to use cloud configuration instead of API customization
 * for card content. API customization should only be used for specific use cases
 * that require dynamic content.
 *
 * @extends VizbeeUICardConfiguration
 * @example
 * const config = new VizbeeSmartInstallCardConfiguration();
 * config.title = "<screen_device_type> Detected";
 * config.subtitle = "Do you want to automatically install the <app_name> app on '<screen_friendly_name>'?";
 * config.ctaText = "Install App";
 */
export default class VizbeeSmartInstallCardConfiguration extends VizbeeUICardConfiguration {
    constructor() {
        super();
    }
}