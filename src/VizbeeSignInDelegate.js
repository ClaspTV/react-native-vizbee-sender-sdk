export default class VizbeeSignInDelegate {

    getSignInInfo() {}

    getSignInInfoAsync(signInfoCallback) {
        if(typeof signInfoCallback == 'function') {
            signInfoCallback();
        }
    }
}