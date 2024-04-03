export default class VizbeeSmartPromptOptions {
    
    constructor() {
        this.enabledSubflows = 
        SmartPromptSubflow.CastAuthorization | SmartPromptSubflow.CastIntroduction | SmartPromptSubflow.SmartInstall;
    }
}

export const SmartPromptSubflow = {
    CastAuthorization: 1 << 1,
    CastIntroduction: 1 << 2,
    SmartInstall: 1 << 3
};