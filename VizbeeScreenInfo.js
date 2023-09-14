export default class VizbeeScreenInfo {

    constructor() {
        this.deviceId = undefined;
        this.friendlyName = undefined;
        this.typeName = undefined
        this.model = undefined
    }

    static fromJson(json) {
        let obj = new VizbeeScreenInfo();
        obj.deviceId = json?.deviceId;
        obj.friendlyName = json?.friendlyName;
        obj.typeName = json?.typeName;
        obj.model = json?.model;
        return obj;
    }
}