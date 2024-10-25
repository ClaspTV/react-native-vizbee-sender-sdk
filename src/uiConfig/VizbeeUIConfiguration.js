
import { NativeModules } from "react-native";

const VizbeeNativeManager = NativeModules.VizbeeNativeManager || {};

/**
* Configuration class for managing UI configurations in the Vizbee SDK.
*/
export default class VizbeeUIConfiguration {
 
    /**
     * Sets a card configuration for a specific card type.
     * 
     * @param {Object} cardConfiguration - The configuration to be set
     * @param {string} cardType - The type of card this configuration applies to
     */
    setCardConfiguration(cardConfiguration, cardType) {
        VizbeeNativeManager.setUICardConfiguration(cardConfiguration, cardType)
    }

    /**
    * Removes the card configuration for a specific card type.
    * 
    * @param {string} cardType - The type of card whose configuration should be removed
    */
    removeCardConfiguration(cardType) {
        VizbeeNativeManager.removeUICardConfiguration(cardType)
    }
 }