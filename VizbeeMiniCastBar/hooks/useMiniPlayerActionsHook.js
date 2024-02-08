import { VizbeeManager} from "react-native-vizbee-sender-sdk";
import { ButtonType } from "react-native-vizbee-sender-sdk/VizbeeMiniCastBar/constants";

export const useMiniPlayerActionsHook = () =>{
  /**
   * Called on Miniplayer View press
   */
  const onMiniPlayerViewPress = () => {
    console.debug(`Miniplayer View pressed`);
    VizbeeManager.smartCast();
  };

  /**
   * Called on Miniplayer Control press
   */
  const onButtonPress = (buttonType) => function (){
    console.debug(`Miniplayer control pressed :: ${buttonType}.`);
    switch (buttonType) {
      case ButtonType.PLAY:
        VizbeeManager.play();
        break;
      case ButtonType.PAUSE:
        VizbeeManager.pause();
        break;
      case ButtonType.STOP:
        VizbeeManager.stop();
        break;
      default:
        break;
    }
  };

    return {onMiniPlayerViewPress, onButtonPress}
}