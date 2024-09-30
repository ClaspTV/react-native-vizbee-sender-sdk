/**
 * @class VizbeeTrackInfo
 * @description Represents information about a track in a Vizbee video
 */
export default class VizbeeTrackInfo {

    /**
     * @constructor
     */
    constructor() {
        /** @type {number} */
        this.identifier = -1;
        /** @type {string|undefined} */
        this.contentIdentifier = undefined;
        /** @type {string|undefined} */
        this.contentType = undefined;
        /** @type {string|undefined} */
        this.name = undefined;
        /** @type {string|undefined} */
        this.languageCode = undefined;
    }

    /**
     * @static
     * @param {Object} json - The JSON object to create VizbeeTrackInfo from
     * @returns {VizbeeTrackInfo} - A new VizbeeTrackInfo instance
     */
    static fromJson(json) {
        let obj = new VizbeeTrackInfo();
        obj.identifier = json?.identifier;
        obj.contentIdentifier = json?.contentIdentifier;
        obj.contentType = json?.contentType;
        obj.name = json?.name;
        obj.languageCode = json?.languageCode;
        return obj;
    }
}