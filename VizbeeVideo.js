export default class VizbeeVideo {

    constructor() {

        this.guid = null;

        // metadata
        this.title = "";
        this.subtitle = "";
        this.imageUrl = "";
        this.isLive = false;

        // streamInfo
        this.streamUrl = null;
        this.tracks = {};
        this.startPositionInSeconds = 0;

        // custom 
        this.customProperties = {};
    }
}

