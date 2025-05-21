import { NativeModules, NativeEventEmitter, Platform } from "react-native";
import VizbeeConstants from "./VizbeeConstants";
import VizbeeSignInInfo from "./VizbeeSignInInfo";
import VizbeeSignInDelegate from "./VizbeeSignInDelegate";
import VizbeeUIConfiguration from "./uiConfig/VizbeeUIConfiguration";
import logger, { LogLevel } from "./logger/VizbeeLogger";

const VizbeeNativeManager = NativeModules.VizbeeNativeManager || {};
const VizbeeNativeEmitter = new NativeEventEmitter(VizbeeNativeManager);

class VizbeeManager {

    constructor() {
        this.subs = {}
        this.sub_id = 0

        this.signInDelegate = undefined;
        this._registerSignInListeners();

        this.uiConfiguration = new VizbeeUIConfiguration();
    }

    //------------------
    // Logging APIs
    //------------------

    enableLogging(enable) {
        if (enable) {
            logger.setLevel(LogLevel.Debug);
            logger.enable();
        } else {
            logger.disable();
        }
    }

    //------------------
    // Flow APIs
    //------------------

    smartPrompt() {
        VizbeeNativeManager.smartPrompt();
    }

    smartPromptWithOptions(smartPromptOptions) {
        VizbeeNativeManager.smartPromptWithOptions(smartPromptOptions);
    }

    smartCast() {
        VizbeeNativeManager.smartCast();
    }
    
    /**
     * Intelligently determines whether to play on phone or offer TV options to user and play on TV
     * @param {Object} vizbeeVideo - The video metadata object
     * @param {Function} didPlayOnTVCallback - Callback when played on TV
     * @param {Function} doPlayOnPhoneCallback - Callback to play on phone
     * @param {SmartPlayOptions} [smartPlayOptions] - Optional configuration for smartPlay behavior
     */
    smartPlay(vizbeeVideo, didPlayOnTVCallback, doPlayOnPhoneCallback, smartPlayOptions = undefined) {
        VizbeeNativeManager.smartPlay(vizbeeVideo, didPlayOnTVCallback, doPlayOnPhoneCallback, smartPlayOptions);
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
    // Cast Icon State APIs
    //------------------

    async getCastIconState() {
        return VizbeeNativeManager.getCastIconState();
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
    
    //-----------------
    // UI
    //-----------------

    /**
     * Returns the UI configuration for Vizbee SDK.
     * This method should only be called after the SDK has been initialized.
     *
     * @return UIConfiguration object if SDK is initialized, nil otherwise
     */
    getUIConfiguration() {
        return this.uiConfiguration;
    }

    //------------------
    // Analytics
    //------------------

    addAnalyticsAttributes(attributes) {
        VizbeeNativeManager.addAnalyticsAttributes(attributes);
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
          // Check if subscription has the modern remove() method
          if (typeof sub.remove === "function") {
            sub.remove();
          } else {
            // Fallback for older RN versions
            try {
              VizbeeNativeEmitter.removeSubscription(sub);
            } catch (error) {
              // Handle any potential errors silently
              console.info("Error removing subscription:", error);
            }
          }
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
            // To maintain backward compatibility, 
            // we first check if the signInDelegate has the synchronous getSignInInfo method
            // implemented. If not, we call the asynchronous getSignInInfoAsync method.
            let signInInfo = this.signInDelegate.getSignInInfo();
            if (signInInfo instanceof VizbeeSignInInfo) {
                VizbeeNativeManager.onGetSignInInfo(signInInfo);
            } else {
                this.signInDelegate.getSignInInfoAsync((signInInfoFromDelegate) => {
                    if (signInInfoFromDelegate instanceof VizbeeSignInInfo) {
                        VizbeeNativeManager.onGetSignInInfo(signInInfoFromDelegate);
                    }
                });
            }
        }
    }
}

export default new VizbeeManager()
