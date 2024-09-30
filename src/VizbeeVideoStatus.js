import VizbeeTrackStatus from "./VizbeeTrackStatus";

/**
 * @class VizbeeVideoStatus
 * @description Represents the status of a video in the Vizbee system
 */
export default class VizbeeVideoStatus {
    /**
     * @constructor
     */
    constructor() {
        /** @type {string|undefined} */
        this.guid = undefined;
        /** @type {string|undefined} */
        this.title = undefined;
        /** @type {string|undefined} */
        this.subTitle = undefined;
        /** @type {string|undefined} */
        this.image = undefined;
        /** @type {boolean} */
        this.isStreamLive = false;
        /** @type {number} */
        this.streamPosition = 0;
        /** @type {number} */
        this.streamDuration = 0;
        /** @type {boolean} */
        this.isAdPlaying = false;
        /** @type {number} */
        this.adPosition = 0;
        /** @type {number} */
        this.adDuration = 0;
        /** @type {string} */
        this.playerState = VizbeePlayerState.Idle;
        /** @type {VizbeeTrackStatus} */
        this.trackStatus = new VizbeeTrackStatus();
    }

    /**
     * @static
     * @param {string} inputString - The input player state string
     * @returns {string} - The validated player state
     * @private
     */
    static _getPlayerState(inputString) {
        let allStates = Object.values(VizbeePlayerState);
        if (allStates === undefined) {
            allStates = []
        }
        if (inputString && allStates.includes(inputString)) {
            return inputString;
        }
        return VizbeePlayerState.Idle;
    }

    /**
     * @static
     * @param {Object} dict - The JSON object to create VizbeeVideoStatus from
     * @returns {VizbeeVideoStatus} - A new VizbeeVideoStatus instance
     */
    static fromJson(dict) {
        let obj = new VizbeeVideoStatus();
        obj.guid = dict?.guid;
        obj.title = dict?.title;
        obj.subTitle = dict?.subTitle;
        obj.isStreamLive = dict?.isStreamLive ? dict?.isStreamLive : false;
        obj.streamPosition = dict?.streamPosition ? dict?.streamPosition : 0;
        obj.streamDuration = dict?.streamDuration ? dict?.streamDuration : 0;
        obj.isAdPlaying = dict?.isAdPlaying ? dict?.isAdPlaying : false;
        obj.adPosition = dict?.adPosition ? dict?.adPosition : 0;
        obj.adDuration = dict?.adDuration ? dict?.adDuration : 0;
        obj.playerState = VizbeeVideoStatus._getPlayerState(dict?.playerState);
        obj.trackStatus = VizbeeTrackStatus.fromJson(dict?.trackStatus);
        return obj;
    }

    /**
     * @param {string} state - The state to check
     * @returns {boolean} - Whether the current player state matches the given state
     */
    hasState(state) {
        return this.playerState === state;
    }
}

/**
 * @enum {string}
 */
export const VizbeePlayerState = {
    Idle : "Idle",
    Started: "Started",
    Loading : "Loading",
    Playing: "Playing",
    Paused: "Paused",
    Buffering: "Buffering",
    Error: "Error",
    Stopped: "Stopped",
    Stopped_On_Disconnect: "Stopped_On_Disconnect",
    Ended: "Ended",
}