import VizbeeUICardConfiguration from './VizbeeUICardConfiguration.js';

/**
 * Configuration class for customizing the Guided Smart Install card UI.
 * 
 * This class extends VizbeeUICardConfiguration to provide specific customization
 * options for the card that offers step-by-step guidance during app installation.
 * It inherits all basic customization properties (title, subtitle, ctaText)
 * from its parent class.
 *
 * Use this configuration when you want to customize the appearance of the card
 * that provides a more detailed, guided approach to installing the app on target
 * devices. This card type is particularly useful when users need additional
 * assistance during the installation process.
 *
 * Note: It is recommended to use cloud configuration instead of API customization
 * for card content. API customization should only be used for specific use cases
 * that require dynamic content.
 *
 * @extends VizbeeUICardConfiguration
 * @example
 * const config = new VizbeeGuidedSmartInstallCardConfiguration();
 * config.title = "<screen_device_type> Detected";
 * config.subtitle = "Follow the steps to install the <app_name> app on your <screen_device_type>";
 * config.ctaText = "Continue";
 */
export default class VizbeeGuidedSmartInstallCardConfiguration extends VizbeeUICardConfiguration {
    constructor() {
        super();
    }
}