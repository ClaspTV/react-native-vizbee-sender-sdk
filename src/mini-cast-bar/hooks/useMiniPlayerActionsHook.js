import { VizbeeManager } from "../../../";
import { ButtonType } from "../constants";
import { LOG_TAG } from "../constants";

export const useMiniPlayerActionsHook = () => {
  /**
   * Called on Miniplayer View press
   */
  const onMiniPlayerViewPress = () => {
    console.debug(LOG_TAG, `Miniplayer View pressed`);
    VizbeeManager.smartCast();
  };

  /**
   * Called on Miniplayer Control press
   */
  const onButtonPress = (buttonType) =>
    function () {
      console.debug(LOG_TAG, `Miniplayer control pressed :: ${buttonType}.`);
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

  return { onMiniPlayerViewPress, onButtonPress };
};
