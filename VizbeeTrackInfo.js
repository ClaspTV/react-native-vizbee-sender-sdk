export default class VizbeeTrackInfo {

    // NOTE
    // Doesn't have type, subtype and customData (not needed)

    constructor() {
        this.identifier = -1;
        this.contentIdentifier = undefined;
        this.contentType = undefined;
        this.name = undefined;
        this.languageCode = undefined;
    }

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