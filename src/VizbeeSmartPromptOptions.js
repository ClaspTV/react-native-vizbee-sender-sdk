export default class VizbeeSmartPromptOptions {
    
    constructor() {
        this.enabledSubflows = 
            VizbeeSmartPromptSubflow.CastAuthorization | 
            VizbeeSmartPromptSubflow.CastIntroduction | 
            VizbeeSmartPromptSubflow.SmartInstall;
    }
}

export const VizbeeSmartPromptSubflow = {
    CastAuthorization: 1 << 1,
    CastIntroduction: 1 << 2,
    SmartInstall: 1 << 3
};