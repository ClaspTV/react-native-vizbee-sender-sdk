/**
 * Base configuration class for customizing Vizbee UI cards.
 * Provides common customization options that apply to all card types.
 * 
 * Note: It is recommended to use cloud configuration instead of API customization
 * for card content. API customization should only be used for specific use cases
 * that require dynamic content.
 * 
 * @property {string|null} title - The main title text displayed on the card
 * @property {string|null} subtitle - Supporting text providing additional context
 * @property {string|null} ctaText - Text for the call-to-action button
 */
export default class VizbeeUICardConfiguration {
    constructor() {
        this.title = null;
        this.subtitle = null;
        this.ctaText = null;
    }
}