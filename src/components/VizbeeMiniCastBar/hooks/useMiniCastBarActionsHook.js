import { VizbeeManager } from "../../../../";
import { ButtonType, LOG_TAG } from "../constants";

/**
 * Custom hook to handle actions for the mini cast bar.
 *
 * @returns {Object} Object containing functions for handling mini cast bar actions.
 */
export const useMiniCastBarActionsHook = () => {
  /**
   * Called when the Mini Cast Bar view is pressed.
   */
  const onMiniCastBarViewPress = () => {
    console.debug(`${LOG_TAG} Mini Cast Bar View pressed`);
    VizbeeManager.smartCast();
  };

  /**
   * Called when a button on the Mini Cast Bar is pressed.
   *
   * @param {string} buttonType - The type of button pressed.
   */
  const onButtonPress = (buttonType) => {
    console.debug(`${LOG_TAG} Mini Cast Bar control pressed :: ${buttonType}.`);
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

  return { onMiniCastBarViewPress, onButtonPress };
};
