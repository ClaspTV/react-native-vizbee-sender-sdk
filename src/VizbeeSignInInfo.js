/**
 * @class VizbeeSignInInfo
 * @description Class representing sign-in information for Vizbee
 */
export default class VizbeeSignInInfo {
    /**
     * @constructor
     * @description Initializes a new instance of VizbeeSignInInfo
     */
    constructor() {
        /**
         * @property {string|undefined} userId - The user's ID
         */
        this.userId = undefined;

        /**
         * @property {string|undefined} userLogin - The user's login
         */
        this.userLogin = undefined;

        /**
         * @property {string|undefined} userFullName - The user's full name
         */
        this.userFullName = undefined;

        /**
         * @property {string|undefined} accessToken - The access token
         */
        this.accessToken = undefined;

        /**
         * @property {string|undefined} refreshToken - The refresh token
         */
        this.refreshToken = undefined;

        /**
         * @property {Object} customData - Custom data associated with the sign-in
         */
        this.customData = {};
    }
}