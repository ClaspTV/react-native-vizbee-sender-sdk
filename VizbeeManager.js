import { NativeModules, NativeEventEmitter, Platform } from "react-native";

const VizbeeNativeManager = NativeModules.VizbeeNativeManager;
const VizbeeNativeEmitter = new NativeEventEmitter(VizbeeNativeManager);

class VizbeeManager {

    constructor() {
        this.subs = {}
        this.sub_id = 0
    }

    //------------------
    // Smart APIs
    //------------------

    smartPrompt() {
        VizbeeNativeManager.smartPrompt();
    }

    smartPlay(vizbeeVideo, didPlayOnTVCallback, doPlayOnPhoneCallback) {
        VizbeeNativeManager.smartPlay(vizbeeVideo, didPlayOnTVCallback, doPlayOnPhoneCallback);
    }

    //------------------
    // Session APIs
    //------------------

    async getSessionState() {
        return VizbeeNativeManager.getSessionState();
    }

    async getSessionConnectedDevice() {
        return VizbeeNativeManager.getSessionConnectedDevice();
    }

    //------------------
    // Listeners
    //------------------

    addListener(eventName, callback, context) {
        this.subs[this.sub_id] = VizbeeNativeEmitter.addListener(eventName, function (map) {
            callback.call(context || null, map)
        })
        return this.sub_id++
    }

    removeListener(subscription) {
        let sub = this.subs[subscription]
        if (sub != null) {
            VizbeeNativeEmitter.removeSubscription(sub);
        }
        delete this.subs[subscription]
    }

    removeAllListeners(eventName) {
        VizbeeNativeEmitter.removeAllListeners(eventName)
    }
}

export default new VizbeeManager()
