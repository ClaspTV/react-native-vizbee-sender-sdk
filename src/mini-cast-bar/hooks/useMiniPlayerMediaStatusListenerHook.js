import { useEffect, useState } from "react";
import {
  VizbeeManager,
  VizbeePlayerState,
} from "../../../";
import { ButtonType } from "../constants";
import { LOG_TAG } from "../constants";

export const useMiniPlayerMediaStatusListenerHook = ({
  isVisible,
  setIsVisible,
  playActionButtonType,
  shouldDisablePlayPauseButtonForLive,
  buttonImgPlay,
  buttonImgPause,
  buttonImgStop,
}) => {
  // Define state
  const [mediaStatus, setMediaStatus] = useState({});
  const [isButtonDisabled, setButtonDisabled] = useState(true);
  const [buttonImg, setButtonImg] = useState(null);
  const [buttonType, setButtonType] = useState(null);

  // useEffect hook
  useEffect(() => {
    console.debug(LOG_TAG, "Add VZB_MEDIA_STATUS Listener");
    const mediaStatusListener = VizbeeManager.addListener(
      "VZB_MEDIA_STATUS",
      videoPlayerStatusCallback
    );

    return () => {
      console.debug(LOG_TAG, "Remove VZB_MEDIA_STATUS Listener");
      VizbeeManager.removeAllListeners(mediaStatusListener);
    };
  }, []);

  /**
   * Update the state of the mini player controls
   *
   * @param {Object} newMediaStatus - Media status containing video info
   * @returns {void}
   */
  const updatePlayerState = (newMediaStatus) => {
    console.debug(LOG_TAG, `New Media Status = ${newMediaStatus}`);
    if (newMediaStatus?.playerState === VizbeePlayerState.Playing) {
      setButtonDisabled(false);

      if (newMediaStatus?.isLive && playActionButtonType !== "playpause") {
        setButtonType(ButtonType.STOP);
        setButtonImg(buttonImgStop);
      } else {
        if (newMediaStatus?.isLive && shouldDisablePlayPauseButtonForLive) {
          setButtonDisabled(true);
        }

        setButtonType(ButtonType.PAUSE);
        setButtonImg(buttonImgPause);
      }
      return;
    }

    if (newMediaStatus?.playerState === VizbeePlayerState.Paused || newMediaStatus?.playerState === VizbeePlayerState.Loading || newMediaStatus?.playerState === VizbeePlayerState.Buffering) {
      if (newMediaStatus.isLive && shouldDisablePlayPauseButtonForLive) {
        setButtonDisabled(true);
      } else {
        setButtonDisabled(false);
      }

      setButtonType(ButtonType.PLAY);
      setButtonImg(buttonImgPlay);
    }

    // update the button state to disabled
    if (mediaStatus.isAdPlaying) {
      setButtonDisabled(true);
    }
  };

  /**
   * Callback for the VZB_MEDIA_STATUS Listener
   *
   * @param {Object} newMediaStatus - Media status containing video info
   */
  const videoPlayerStatusCallback = (newMediaStatus) => {
    console.debug(
      LOG_TAG,
      `Media status current state :: ${newMediaStatus.playerState}`
    );
    if (newMediaStatus?.guid && newMediaStatus?.guid?.length != 0) {
      setMediaStatus(newMediaStatus);
      switch (newMediaStatus.playerState) {
        case VizbeePlayerState.Error:
        case VizbeePlayerState.Stopped:
        case VizbeePlayerState.Stopped_On_Disconnect:
        case VizbeePlayerState.Ended:
        // Receiving started event with empty title and subtitle so remo
        case VizbeePlayerState.Started:
          resetStateToDefault();
          break;
        default:
          setIsVisible(true);
          updatePlayerState(newMediaStatus);
      }
    }
  };

  /**
   * Resets states to default
   */
  const resetStateToDefault = () => {
    console.debug(LOG_TAG, "Reset to default state");
    setIsVisible(false);
    setButtonDisabled(true);
    setMediaStatus({});
    setButtonType(null);
    setButtonImg(null);
  };

  return {
    isVisible,
    setIsVisible,
    mediaStatus,
    isButtonDisabled,
    buttonImg,
    buttonType,
  };
};
