import {requireNativeComponent, Platform} from 'react-native';
import VizbeeManager from './VizbeeManager';
import VizbeeVideo from './VizbeeVideo';
import VizbeeVideoStatus, {VizbeePlayerState} from './VizbeeVideoStatus';
import VizbeeTrackStatus from './VizbeeTrackStatus';
import VizbeeTrackInfo from './VizbeeTrackInfo';
import VizbeeSignInDelegate from './VizbeeSignInDelegate';
import VizbeeSignInInfo from './VizbeeSignInInfo';

/*
Vizbee RN SDK can be used in common JS player code which is used across iOS, Android, Web etc. 
This platform check ensures the right dependencies and exports for each platform. 
Speciically, we do not support web and should not have a dependency on requireNativeComponent...
or export the VizbeeCastButton.
*/

if (Platform.OS === 'web') {

    module.exports = {
        VizbeeManager,
        VizbeeVideo,
        VizbeeVideoStatus,
        VizbeePlayerState,
        VizbeeTrackStatus,
        VizbeeTrackInfo,
        VizbeeSignInDelegate,
        VizbeeSignInInfo,
    }
} else {
    const VizbeeCastButton = requireNativeComponent('VizbeeCastButtonView');

    module.exports = {
        VizbeeManager,
        VizbeeCastButton,
        VizbeeVideo,
        VizbeeVideoStatus,
        VizbeePlayerState,
        VizbeeTrackStatus,
        VizbeeTrackInfo,
        VizbeeSignInDelegate,
        VizbeeSignInInfo,
    }
}