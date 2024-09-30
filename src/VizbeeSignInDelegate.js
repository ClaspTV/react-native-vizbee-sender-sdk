/**
 * @class VizbeeSignInDelegate
 * @description Delegate class for handling Vizbee sign-in operations
 */
export default class VizbeeSignInDelegate {
    /**
     * @deprecated
     * @method getSignInInfo
     * @description Synchronous method to get sign-in information (deprecated)
     * @returns {void}
     */
    getSignInInfo() {}

    /**
     * @method getSignInInfoAsync
     * @description Asynchronous method to get sign-in information
     * @param {function} signInfoCallback - Callback function to receive sign-in information
     */
    getSignInInfoAsync(signInfoCallback) {
        if(typeof signInfoCallback == 'function') {
            signInfoCallback();
        }
    }
}