import { NativeModules } from "react-native"

const VizbeeNativeManager = NativeModules.VizbeeNativeManager

class VizbeeManager {

    smartPrompt() {
        VizbeeNativeManager.smartPrompt();
    }

    smartPlay(vizbeeVideo, didPlayOnTVCallback, doPlayOnPhoneCallback) {
         VizbeeNativeManager.smartPlay(vizbeeVideo, didPlayOnTVCallback, doPlayOnPhoneCallback);
    }
}

export default new VizbeeManager()
