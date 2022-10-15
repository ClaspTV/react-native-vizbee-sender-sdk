import { NativeModules, NativeEventEmitter, Platform } from "react-native";

const VizbeeNativeManager = NativeModules.VizbeeNativeManager;
const VizbeeNativeEmitter = new NativeEventEmitter(VizbeeNativeManager);

class VizbeeManager {

    constructor() {
        this.subs = {}
        this.sub_id = 0
    }

    //------------------
    // Flow APIs
    //------------------

    smartPrompt() {
        VizbeeNativeManager.smartPrompt();
    }

    smartCast() {
        VizbeeNativeManager.smartCast();
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

    disconnect() {
        VizbeeNativeManager.disconnect();
    }
    
    //------------------
    // Video APIs
    //------------------

    play() {
        VizbeeNativeManager.play();
    }

    pause() {
        VizbeeNativeManager.pause();
    }

    seek(position) {
        VizbeeNativeManager.seek(position);
    }

    stop() {
        VizbeeNativeManager.stop();
    }

    setActiveTrack(trackInfo) {
        VizbeeNativeManager.setActiveTrack(trackInfo);
    }

    resetActiveTrack() {
        VizbeeNativeManager.resetActiveTrack();
    }

    mute() {
        VizbeeNativeManager.mute();
    }

    unmute() {
        VizbeeNativeManager.unmute();
    }

    //------------------
    // MiniCastController APIs
    //------------------

    addMiniCastController(bottomMargin, height) {
        VizbeeNativeManager.addMiniCastController(bottomMargin, height);
    }

    setMiniCastControllerBackgroundColor(backgroundColor) {
        VizbeeNativeManager.setMiniCastControllerBackgroundColor(backgroundColor);
    }

    setMiniCastControllerPlaybackButtonColor(buttonColor) {
        VizbeeNativeManager.setMiniCastControllerPlaybackButtonColor(buttonColor)
    }
    
    showMiniCastController() {
        VizbeeManager.showMiniCastController();
    }

    hideMiniCastController() {
        VizbeeManager.hideMiniCastController();
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
