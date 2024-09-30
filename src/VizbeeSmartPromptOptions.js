/**
 * @class VizbeeSmartPromptOptions
 * @description Class representing options for Vizbee smart prompts
 */
export default class VizbeeSmartPromptOptions {
    /**
     * @constructor
     * @description Initializes a new instance of VizbeeSmartPromptOptions
     */
    constructor() {
        /**
         * @property {number} enabledSubflows - Bitwise combination of enabled subflows
         */
        this.enabledSubflows = 
            VizbeeSmartPromptSubflow.CastAuthorization | 
            VizbeeSmartPromptSubflow.CastIntroduction | 
            VizbeeSmartPromptSubflow.SmartInstall;
    }
}

/**
 * @enum {number}
 * @description Enum representing different subflows for smart prompts
 */
export const VizbeeSmartPromptSubflow = {
    CastAuthorization: 1 << 1,
    CastIntroduction: 1 << 2,
    SmartInstall: 1 << 3
};