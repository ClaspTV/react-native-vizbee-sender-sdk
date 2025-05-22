/**
 * VizbeeSmartPlayOptions.js
 * Provides configuration options for Vizbee's smartPlay functionality
 */

/**
 * Enum for Smart Play Card Visibility options
 * @readonly
 * @enum {number}
 */
export const VizbeeSmartPlayCardVisibility = {
  SHOW_HIDE_BASED_ON_CONFIGURATION: 0,
  FORCE_SHOW: 1,
  FORCE_HIDE: 2
};

/**
 * SmartPlayOptions configuration for controlling smartPlay behavior
 */
export default class VizbeeSmartPlayOptions {
  /**
   * Create new SmartPlayOptions
   * @param {boolean} [isFromSmartNotification=false] - Whether this play request is coming from a smart notification
   * @param {VizbeeSmartPlayCardVisibility} [smartPlayCardVisibility=SHOW_HIDE_BASED_ON_CONFIGURATION] - Controls visibility of the smart play card
   */
  constructor() {
    /**
     * Whether this play request is coming from a smart notification
     * @type {boolean}
     */
    this.isFromSmartNotification = false;
    
    /**
     * Controls visibility of the smart play card
     * @type {VizbeeSmartPlayCardVisibility}
     */
    this.smartPlayCardVisibility = VizbeeSmartPlayCardVisibility.SHOW_HIDE_BASED_ON_CONFIGURATION;
  }
}