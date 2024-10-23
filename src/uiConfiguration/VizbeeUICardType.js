/**
* Enumeration of different card types supported by the Vizbee UI SDK.
* Each card type represents a specific UI component used in different user flows.
* 
* @readonly
* @enum {string}
*/
export const VizbeeUICardType = {
    /**
     * Card for handling cast authorization flows.
     * Used when requesting casting permissions from users.
     */
    CAST_AUTHORIZATION: 'CAST_AUTHORIZATION',
 
    /**
     * Card for introducing casting functionality to users.
     * Typically shown when users are new to casting or need guidance
     * on casting features.
     */
    CAST_INTRODUCTION: 'CAST_INTRODUCTION', 
 
    /**
     * Card for smart app installation process.
     * Used when the app needs to be installed on the target device
     * for an enhanced viewing experience.
     */
    SMART_INSTALL: 'SMART_INSTALL',
 
    /**
     * Card for promoting app installation across multiple devices.
     * Used when encouraging users to install the app on various platforms
     * (like TV, mobile, tablet) for a comprehensive viewing experience.
     */
    MULTI_DEVICE_SMART_INSTALL: 'MULTI_DEVICE_SMART_INSTALL',
 
    /**
     * Card providing step-by-step guidance for app installation.
     * Offers a more detailed, guided approach to installing the app
     * on target devices when users need additional assistance.
     */
    GUIDED_SMART_INSTALL: 'GUIDED_SMART_INSTALL'
 };