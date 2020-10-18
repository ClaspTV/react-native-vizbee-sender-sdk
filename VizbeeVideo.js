export default class VizbeeVideo {

    constructor() {

        this.guid = null;

        // metadata
        this.title = "";
        this.subtitle = "";
        this.imageURL = "";
        this.isLive = false;

        // streamInfo
        this.streamURL = null;
        this.tracks = {};
        this.startPositionInSeconds = 0;

        // custom 
        this.customProperties = {};
    }
}

