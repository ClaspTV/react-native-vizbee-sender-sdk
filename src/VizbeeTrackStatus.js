import VizbeeTrackInfo from "./VizbeeTrackInfo";

export default class VizbeeTrackStatus {
   
    constructor() {
        this.availableTracks = [];
        this.currentTrack = undefined;
    }

    static fromJson(json) {
        let obj = new VizbeeTrackStatus();
        obj.availableTracks = json?.availableTracks.map(trackJson => VizbeeTrackInfo.fromJson(trackJson));
        obj.currentTrack = VizbeeTrackInfo.fromJson(json?.currentTrack);
        return obj;
    }
}