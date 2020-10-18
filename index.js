import {requireNativeComponent} from 'react-native';
import VizbeeManager from './VizbeeManager';
import VizbeeVideo from './VizbeeVideo';

const VizbeeCastButton = requireNativeComponent('VizbeeCastButtonView');

module.exports = {
    VizbeeManager,
    VizbeeCastButton,
    VizbeeVideo
}
