import { NativeModules, NativeEventEmitter, Platform } from "react-native";
import VizbeeConstants from "./VizbeeConstants";
import VizbeeSignInInfo from "./VizbeeSignInInfo";
import VizbeeSignInDelegate from "./VizbeeSignInDelegate";
import VizbeeSmartPromptOptions from "./VizbeeSmartPromptOptions";

const VizbeeNativeManager = NativeModules.VizbeeNativeManager || {};
const VizbeeNativeEmitter = new NativeEventEmitter(VizbeeNativeManager);

class VizbeeManager {

    constructor() {
        this.subs = {}
        this.sub_id = 0

        this.signInDelegate = undefined;
        this._registerSignInListeners();
    }

    //------------------
    // Flow APIs
    //------------------

    smartPrompt() {
        VizbeeNativeManager.smartPrompt();
    }

    smartPromptWithOptions(smartPromptOptions) {
        console.log(`VizbeeSmartPromptOptions ${smartPromptOptions}`);
        VizbeeNativeManager.smartPromptWithOptions(smartPromptOptions);
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
    // Sign in APIs
    //------------------

    setSignInDelegate(signInDelegate) {
        if (signInDelegate instanceof VizbeeSignInDelegate) {
            this.signInDelegate = signInDelegate;
        }
    }

    removeSignInDelegate() {
        this.signInDelegate = undefined;
    }

    getSignInDelegate() {
        return this.signInDelegate;
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

    supportsVolumeControl(callback) {
        VizbeeNativeManager.supportsVolumeControl(callback);
    }

    // value between 0 and 1
    setVolume(volume) {
        VizbeeNativeManager.setVolume(volume);
    }

    getVolume(callback) {
        VizbeeNativeManager.getVolume(callback);
    }

    mute() {
        VizbeeNativeManager.mute();
    }

    unmute() {
        VizbeeNativeManager.unmute();
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

    //------------------
    // Sign in Helpers
    //------------------

    _registerSignInListeners() {
        // listen for sign in events
        this.addListener(
            VizbeeConstants.VZB_INVOKE_GET_SIGNIN_INFO,
            this._invokeAndHandleGetSignInInfo,
            this
        );
    }
    
    _invokeAndHandleGetSignInInfo() {
        if (this.signInDelegate) {
            let signInInfo = this.signInDelegate.getSignInInfo();
            if (signInInfo instanceof VizbeeSignInInfo) {
                VizbeeNativeManager.onGetSignInInfo(signInInfo);
            }
        }
    }
}

export default new VizbeeManager()
