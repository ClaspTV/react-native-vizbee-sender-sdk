import VizbeeTrackInfo from "./VizbeeTrackInfo";

/**
 * @class VizbeeTrackStatus
 * @description Represents the status of tracks in a Vizbee video
 */
export default class VizbeeTrackStatus {
   
    /**
     * @constructor
     */
    constructor() {
        /** @type {VizbeeTrackInfo[]} */
        this.availableTracks = [];
        /** @type {VizbeeTrackInfo|undefined} */
        this.currentTrack = undefined;
    }

    /**
     * @static
     * @param {Object} json - The JSON object to create VizbeeTrackStatus from
     * @returns {VizbeeTrackStatus} - A new VizbeeTrackStatus instance
     */
    static fromJson(json) {
        let obj = new VizbeeTrackStatus();
        obj.availableTracks = json?.availableTracks.map(trackJson => VizbeeTrackInfo.fromJson(trackJson));
        obj.currentTrack = VizbeeTrackInfo.fromJson(json?.currentTrack);
        return obj;
    }
}