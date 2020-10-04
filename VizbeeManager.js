import { NativeModules, NativeEventEmitter, Platform } from "react-native"

const VizbeeManagerNative = NativeModules.VizbeeSenderManager
const VizbeeManagerNativeEmitter = new NativeEventEmitter(VizbeeManagerNative)

class VizbeeManager {

    constructor() {
        this.subs = {}
        this.sub_id = 0
    }

    SmartPrompt() {
        VizbeeManagerNative.SmartHelp();
    }

    // // Start the smart play
    // SmartPlay(videoInfo) {
    //     VizbeeControllerPriv.SmartPlay(videoInfo)
    // }

    // // Respond to the request for video Info
    // RespondVideoInfoRequest(videoInfo) {
    //     VizbeeControllerPriv.RespondVideoInfoRequest(videoInfo)
    // }

    // // Respond to the request for video Info
    // RespondErrorInfoRequest(errorStr) {
    //     VizbeeControllerPriv.RespondErrorInfoRequest(errorStr)
    // }

    // dismiss() {
    //     VizbeeControllerPriv.destroy();
    // }

    // //Gets the current connection state to a receiver
    // GetCurrentState() {
    //     VizbeeControllerPriv.GetCurrentState();        
    // }

    // //Configure the UI
    // SetUIConfig(uiConfig) {
    //     if (Platform.OS == "ios") {
    //         VizbeeControllerPriv.SetUIConfig(uiConfig);
    //     } else {
    //         console.log("VizbeeController: SetUIConfig is ios only, for android set vizbee.xml as in README")
    //     }
    // }

    /**
     * Connect to one if the supported events
     *
     * @param {string} eventName The name of the event
     * @param {function} callback The callback used when the event is triggered
     * @param {object} [context] The function context, usually is `this` or `null`
     * 
     * @returns {number} Subscription
     */
    AddListener(eventName, callback, context) {
        this.subs[this.sub_id] = VizbeeManagerNativeEmitter.addListener(eventName, function (array) {
            callback.apply(context || null, array)
        })
        return this.sub_id++
    }

    /**
     * Disconnects from listening an event
     *
     * @param {number} subscription The subscription returned by `AddListener`
     */
    RemoveListener(subscription) {
        let sub = this.subs[subscription]
        if (sub != null) {
            VizbeeManagerNativeEmitter.removeSubscription(sub);
        }
        delete this.subs[subscription]
    }

    /**
     * Disconnects all the listeners from a certain event
     *
     * @param {string} eventName The event name to remove all the listeners from
     * 
     * For the supported events see `AddListener`
     */
    RemoveAllListeners(eventName) {
        VizbeeManagerNativeEmitter.removeAllListeners(eventName)
    }
}

export default new VizbeeManager()
