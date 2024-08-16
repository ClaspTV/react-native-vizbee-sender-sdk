export default class VizbeeSignInDelegate {

    // @deprecated
    getSignInInfo() {}

    getSignInInfoAsync(signInfoCallback) {
        if(typeof signInfoCallback == 'function') {
            signInfoCallback();
        }
    }
}