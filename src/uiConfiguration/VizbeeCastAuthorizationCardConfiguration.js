import VizbeeUICardConfiguration from './VizbeeUICardConfiguration.js';

/**
 * Configuration class for customizing the Cast Authorization card UI.
 * 
 * This class extends VZBCardConfiguration to provide specific customization
 * options for the card shown during cast authorization flows. It inherits all
 * basic customization properties (title, subtitle, ctaText) from its parent class.
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
 * const config = new VizbeeCastAuthorizationCardConfiguration();
 * config.title = "Allow Casting";
 * config.subtitle = "Cast your favorite content to your TV";
 * config.ctaText = "Open Settings";
 */
export default class VizbeeCastAuthorizationCardConfiguration extends VizbeeUICardConfiguration {
    constructor() {
        super();
    }
}