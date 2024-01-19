import { useState } from "react";
import {
  VizbeeManager,
  VizbeePlayerState,
} from "react-native-vizbee-sender-sdk";
import { ButtonType } from "react-native-vizbee-sender-sdk/VizbeeMiniCastBar/constants";

export const useMiniPlayerHook = ({
  playActionButtonType,
  shouldDisablePlayPauseButtonForLive,
  buttonImgPlay,
  buttonImgPause,
  buttonImgStop,
}) => {
  // Define state
  const [isVisible, setIsVisible] = useState(false);
  const [mediaStatus, setMediaStatus] = useState({});
  const [castingTo, setCastingTo] = useState("TV");
  const [disableButton, setDisableButton] = useState(true);
  const [buttonImg, setButtonImg] = useState(null);
  const [buttonType, setButtonType] = useState(null);

  /**
   * Get connected device friendly name
   */
  const getConnectedDeviceInfo = () => {
    console.debug("Add VZB_MEDIA_STATUS Listener");
    VizbeeManager.getSessionConnectedDevice()
      .then((connectedDeviceInfo) => {
        setCastingTo(connectedDeviceInfo?.connectedDeviceFriendlyName || "TV");
      })
      .catch(() => {
        setCastingTo("TV");
      });
  };

  /**
   * Update the state of the mini player controls
   *
   * @param {Object} newMediaStatus - Media status containing video info
   * @returns {void}
   */
  const updatePlayerState = (newMediaStatus) => {
    if (newMediaStatus?.playerState === VizbeePlayerState.Playing) {
      setDisableButton(false);

      if (newMediaStatus?.isLive && playActionButtonType !== "playpause") {
        setButtonType(ButtonType.STOP);
        setButtonImg(buttonImgStop);
      } else {
        if (newMediaStatus?.isLive && shouldDisablePlayPauseButtonForLive) {
          setDisableButton(true);
        }

        setButtonType(ButtonType.PAUSE);
        setButtonImg(buttonImgPause);
      }
      return;
    }

    if (newMediaStatus?.playerState === VizbeePlayerState.Paused) {
      if (newMediaStatus.isLive && shouldDisablePlayPauseButtonForLive) {
        setDisableButton(true);
      } else {
        setDisableButton(false);
      }

      setButtonType(ButtonType.PLAY);
      setButtonImg(buttonImgPlay);
    }

    // update the button state to disabled
    if (mediaStatus.isAdPlaying) {
      setDisableButton(true);
    }
  };

  /**
   * Callback for the VZB_MEDIA_STATUS Listener
   *
   * @param {Object} newMediaStatus - Media status containing video info
   */
  const videoPlayerStatusCallback = (newMediaStatus) => {
    console.debug(
      `Media status current state :: ${newMediaStatus.playerState}`
    );
    if (newMediaStatus?.guid && newMediaStatus?.guid?.length != 0) {
      setMediaStatus(newMediaStatus);
      switch (newMediaStatus.playerState) {
        case VizbeePlayerState.Error:
        case VizbeePlayerState.Stopped:
        case VizbeePlayerState.Stopped_On_Disconnect:
        case VizbeePlayerState.Ended:
          resetStateToDefault();
          break;
        default:
          setIsVisible(true);
          getConnectedDeviceInfo();
          updatePlayerState(newMediaStatus);
      }
    }
  };

  /**
   * Resets states to default
   */
  const resetStateToDefault = () => {
    setIsVisible(false);
    setDisableButton(true);
    setMediaStatus({});
    setCastingTo("TV");
    setButtonType(null);
    setButtonImg(null);
  };

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
  const onButtonPress = () => {
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

  return {
    isVisible,
    mediaStatus,
    disableButton,
    buttonImg,
    buttonType,
    castingTo,
    videoPlayerStatusCallback,
    onMiniPlayerViewPress,
    onButtonPress,
  };
};
