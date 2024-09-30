import { NativeModules, NativeEventEmitter, Platform } from "react-native";
import VizbeeConstants from "./VizbeeConstants";
import VizbeeSignInInfo from "./VizbeeSignInInfo";
import VizbeeSignInDelegate from "./VizbeeSignInDelegate";

const VizbeeNativeManager = NativeModules.VizbeeNativeManager || {};
const VizbeeNativeEmitter = new NativeEventEmitter(VizbeeNativeManager);

/**
 * @class VizbeeManager
 * @description Manages Vizbee functionality including smart prompts, casting, video control, and sign-in operations.
 */
class VizbeeManager {
    /**
     * @constructor
     * @description Initializes the VizbeeManager and sets up sign-in listeners.
     */
    constructor() {
        this.subs = {}
        this.sub_id = 0

        this.signInDelegate = undefined;
        this._registerSignInListeners();
    }

    //------------------
    // Flow APIs
    //------------------

    /**
     * @method smartPrompt
     * @description Triggers a smart prompt.
     */
    smartPrompt() {
        VizbeeNativeManager.smartPrompt();
    }

    /**
     * @method smartPromptWithOptions
     * @param {Object} smartPromptOptions - Options for the smart prompt.
     * @description Triggers a smart prompt with specified options.
     */
    smartPromptWithOptions(smartPromptOptions) {
        VizbeeNativeManager.smartPromptWithOptions(smartPromptOptions);
    }

    /**
     * @method smartCast
     * @description Initiates a smart cast operation.
     */
    smartCast() {
        VizbeeNativeManager.smartCast();
    }
    
    /**
     * @method smartPlay
     * @param {Object} vizbeeVideo - The video object to be played.
     * @param {Function} didPlayOnTVCallback - Callback function when video plays on TV.
     * @param {Function} doPlayOnPhoneCallback - Callback function to play video on phone.
     * @description Initiates smart play functionality for a given video.
     */
    smartPlay(vizbeeVideo, didPlayOnTVCallback, doPlayOnPhoneCallback) {
        VizbeeNativeManager.smartPlay(vizbeeVideo, didPlayOnTVCallback, doPlayOnPhoneCallback);
    }

    //------------------
    // Session APIs
    //------------------

    /**
     * @method getSessionState
     * @returns {Promise} A promise that resolves with the current session state.
     * @description Asynchronously retrieves the current session state.
     */
    async getSessionState() {
        return VizbeeNativeManager.getSessionState();
    }

    /**
     * @method getSessionConnectedDevice
     * @returns {Promise} A promise that resolves with the connected device information.
     * @description Asynchronously retrieves information about the connected device.
     */
    async getSessionConnectedDevice() {
        return VizbeeNativeManager.getSessionConnectedDevice();
    }

    /**
     * @method disconnect
     * @description Disconnects the current session.
     */
    disconnect() {
        VizbeeNativeManager.disconnect();
    }

    //------------------
    // Sign in APIs
    //------------------

    /**
     * @method setSignInDelegate
     * @param {VizbeeSignInDelegate} signInDelegate - The sign-in delegate to be set.
     * @description Sets the sign-in delegate if it's an instance of VizbeeSignInDelegate.
     */
    setSignInDelegate(signInDelegate) {
        if (signInDelegate instanceof VizbeeSignInDelegate) {
            this.signInDelegate = signInDelegate;
        }
    }

    /**
     * @method removeSignInDelegate
     * @description Removes the current sign-in delegate.
     */
    removeSignInDelegate() {
        this.signInDelegate = undefined;
    }

    /**
     * @method getSignInDelegate
     * @returns {VizbeeSignInDelegate|undefined} The current sign-in delegate.
     * @description Retrieves the current sign-in delegate.
     */
    getSignInDelegate() {
        return this.signInDelegate;
    }

    //------------------
    // Video APIs
    //------------------

    /**
     * @method play
     * @description Starts or resumes video playback.
     */
    play() {
        VizbeeNativeManager.play();
    }

    /**
     * @method pause
     * @description Pauses video playback.
     */
    pause() {
        VizbeeNativeManager.pause();
    }

    /**
     * @method seek
     * @param {number} position - The position to seek to in the video.
     * @description Seeks to a specific position in the video.
     */
    seek(position) {
        VizbeeNativeManager.seek(position);
    }

    /**
     * @method stop
     * @description Stops video playback.
     */
    stop() {
        VizbeeNativeManager.stop();
    }

    /**
     * @method setActiveTrack
     * @param {Object} trackInfo - Information about the track to be set as active.
     * @description Sets the active track for the video.
     */
    setActiveTrack(trackInfo) {
        VizbeeNativeManager.setActiveTrack(trackInfo);
    }

    /**
     * @method resetActiveTrack
     * @description Resets the active track to default.
     */
    resetActiveTrack() {
        VizbeeNativeManager.resetActiveTrack();
    }

    /**
     * @method supportsVolumeControl
     * @param {Function} callback - Callback function to receive the result.
     * @description Checks if volume control is supported.
     */
    supportsVolumeControl(callback) {
        VizbeeNativeManager.supportsVolumeControl(callback);
    }

    /**
     * @method setVolume
     * @param {number} volume - Volume level to set (between 0 and 1).
     * @description Sets the volume level.
     */
    setVolume(volume) {
        VizbeeNativeManager.setVolume(volume);
    }

    /**
     * @method getVolume
     * @param {Function} callback - Callback function to receive the current volume.
     * @description Retrieves the current volume level.
     */
    getVolume(callback) {
        VizbeeNativeManager.getVolume(callback);
    }

    /**
     * @method mute
     * @description Mutes the audio.
     */
    mute() {
        VizbeeNativeManager.mute();
    }

    /**
     * @method unmute
     * @description Unmutes the audio.
     */
    unmute() {
        VizbeeNativeManager.unmute();
    }
    
    //------------------
    // Analytics
    //------------------

    /**
     * @method addAnalyticsAttributes
     * @param {Object} attributes - Analytics attributes to add.
     * @description Adds analytics attributes.
     */
    addAnalyticsAttributes(attributes) {
        VizbeeNativeManager.addAnalyticsAttributes(attributes);
    }
    
    //------------------
    // Listeners
    //------------------

    /**
     * @method addListener
     * @param {string} eventName - Name of the event to listen for.
     * @param {Function} callback - Callback function to handle the event.
     * @param {Object} [context] - Context in which to call the callback.
     * @returns {number} Subscription ID for the listener.
     * @description Adds an event listener.
     */
    addListener(eventName, callback, context) {
        this.subs[this.sub_id] = VizbeeNativeEmitter.addListener(eventName, function (map) {
            callback.call(context || null, map)
        })
        return this.sub_id++
    }

    /**
     * @method removeListener
     * @param {number} subscription - Subscription ID of the listener to remove.
     * @description Removes a specific event listener.
     */
    removeListener(subscription) {
        let sub = this.subs[subscription]
        if (sub != null) {
            VizbeeNativeEmitter.removeSubscription(sub);
        }
        delete this.subs[subscription]
    }

    /**
     * @method removeAllListeners
     * @param {string} eventName - Name of the event to remove all listeners for.
     * @description Removes all listeners for a specific event.
     */
    removeAllListeners(eventName) {
        VizbeeNativeEmitter.removeAllListeners(eventName)
    }

    //------------------
    // Sign in Helpers
    //------------------

    /**
     * @private
     * @method _registerSignInListeners
     * @description Registers listeners for sign-in events.
     */
    _registerSignInListeners() {
        // listen for sign in events
        this.addListener(
            VizbeeConstants.VZB_INVOKE_GET_SIGNIN_INFO,
            this._invokeAndHandleGetSignInInfo,
            this
        );
    }
    
    /**
     * @private
     * @method _invokeAndHandleGetSignInInfo
     * @description Invokes and handles the get sign-in info process.
     */
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