import VizbeeUICardConfiguration from './VizbeeUICardConfiguration.js';

/**
 * Configuration class for customizing the Multi-Device Smart Install card UI.
 * 
 * This class extends VizbeeUICardConfiguration to provide specific customization
 * options for the card that promotes app installation across multiple devices.
 * It inherits all basic customization properties (title, subtitle, ctaText)
 * from its parent class.
 *
 * Use this configuration when you want to customize the appearance of the card
 * that encourages users to install the app on various platforms (like TV, mobile,
 * tablet) for a more comprehensive and enhanced viewing experience.
 *
 * Note: It is recommended to use cloud configuration instead of API customization
 * for card content. API customization should only be used for specific use cases
 * that require dynamic content.
 *
 * @extends VizbeeUICardConfiguration
 * @example
 * const config = new VizbeeMultiDeviceSmartInstallCardConfiguration();
 * config.title = "Streaming Devices Detected";
 * config.subtitle = "Do you want to install the <app_name> app on a streaming device near you?";
 */
export default class VizbeeMultiDeviceSmartInstallCardConfiguration extends VizbeeUICardConfiguration {
    constructor() {
        super();
    }
}