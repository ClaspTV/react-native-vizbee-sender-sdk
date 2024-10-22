import { VizbeeUICardType } from './VizbeeUICardType.js';

class VizbeeUIConfiguration {
    constructor() {
        this._cardConfigurations = new Map();
    }

    /**
     * Sets the card configuration for a specific card type
     * @param {import('./CardConfiguration.js').CardConfiguration} cardConfiguration - The configuration to set
     * @param {VizbeeUICardType} forCardType - The type of card to configure
     */
    setCardConfiguration(cardConfiguration, forCardType) {
        this._cardConfigurations.set(forCardType, cardConfiguration);
    }

    /**
     * Gets the card configuration for a specific card type
     * @param {VizbeeUICardType} cardType - The type of card to get configuration for
     * @returns {import('./CardConfiguration.js').CardConfiguration|undefined} The configuration for the card type, or undefined if not found
     */
    cardConfigurationForCardType(cardType) {
        return this._cardConfigurations.get(cardType);
    }
}

export { VizbeeUIConfiguration };