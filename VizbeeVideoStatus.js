import VizbeeTrackStatus from "./VizbeeTrackStatus";

export default class VizbeeVideoStatus {

    constructor() {
        this.guid = undefined;
        this.title = undefined;
        this.subTitle = undefined;
        this.image
        this.isStreamLive = false;
        this.streamPosition = 0;
        this.streamDuration = 0;
        this.isAdPlaying = false;
        this.adPosition = 0;
        this.adDuration = 0;
        this.playerState = VizbeePlayerState.IDLE;
        this.trackStatus = new VizbeeTrackStatus();
    }

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

    hasState(state) {
        return this.playerState === state;
    }

    _getPlayerState(inputString) {
        
        let allStates = Object.values(VizbeePlayerState);
        if (allStates === undefined) {
            allStates = []
        }
        if (inputString && allStates.includes(inputString)) {
            return inputString;
        }

        return VizbeePlayerState.IDLE;
    }
}

export const VizbeePlayerState = {
    IDLE : "Idle",
    STARTED: "Started",
    LOADING : "Loading",
    PLAYING: "Playing",
    PAUSED: "Paused",
    BUFFERING: "Buffering",
    ERROR: "Error",
    STOPPED: "Stopped",
    ENDED: "Ended",
}