
import {useEffect, useState } from "react";
import {
  VizbeeManager,
  VizbeePlayerState,
} from "react-native-vizbee-sender-sdk";
import { ButtonType } from "react-native-vizbee-sender-sdk/VizbeeMiniCastBar/constants";

export const useMiniPlayerMediaStatusListenerHook = ({
  isVisible,
  setIsVisible,
    playActionButtonType,
    shouldDisablePlayPauseButtonForLive,
    buttonImgPlay,
    buttonImgPause,
    buttonImgStop
}) => {

     // Define state
  const [mediaStatus, setMediaStatus] = useState({});
  const [isButtonDisabled, setButtonDisabled] = useState(true);
  const [buttonImg, setButtonImg] = useState(null);
  const [buttonType, setButtonType] = useState(null);

    // useEffect hook
  useEffect(() => {
    console.debug("Add VZB_MEDIA_STATUS Listener");
    const mediaStatusListener = VizbeeManager.addListener(
      "VZB_MEDIA_STATUS",
      videoPlayerStatusCallback
    );

    return () => {
      console.debug("Remove VZB_MEDIA_STATUS Listener");
      VizbeeManager.removeListener(mediaStatusListener);
    };
  }, []);




  /**
   * Update the state of the mini player controls
   *
   * @param {Object} newMediaStatus - Media status containing video info
   * @returns {void}
   */
  const updatePlayerState = (newMediaStatus) => {
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

    if (newMediaStatus?.playerState === VizbeePlayerState.Paused) {
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
  const resetStateToDefault = () =>{
    setIsVisible(false);
    setButtonDisabled(true);
    setMediaStatus({});
    setButtonType(null);
    setButtonImg(null);
  }

  return {isVisible,setIsVisible, mediaStatus, isButtonDisabled, buttonImg, buttonType}
}