import React, { useEffect } from "react";
import { View, Image, TouchableOpacity, Text } from "react-native";
import { VizbeeManager } from "react-native-vizbee-sender-sdk";
import { useMiniPlayerStyleHook } from "./hooks/useMiniPlayerStyleHook";
import { useMiniPlayerHook } from "./hooks/useMiniPlayerHook";
import { getTitleFromVideoInfo, getSubTitleFromVideoInfo } from "./helpers";
import PropTypes from "prop-types";
import { Images } from "./constants";

const VizbeeMiniCastBar = ({
  thumbnailImageSourceDefault,
  backgroundLayerStyle,
  progressViewStyle,
  titleStyle,
  subTitleStyle,
  buttonStyle,
  disableButtonStyle,
  playActionButtonType,
  shouldDisablePlayPauseButtonForLive,
  buttonImgPlay,
  buttonImgPause,
  buttonImgStop,
}) => {
  const defaultStyles = useMiniPlayerStyleHook({
    backgroundLayerStyle,
    progressViewStyle,
    titleStyle,
    subTitleStyle,
    buttonStyle,
    disableButtonStyle,
  });

  const {
    isVisible,
    mediaStatus,
    disableButton,
    buttonImg,
    buttonType,
    castingTo,
    videoPlayerStatusCallback,
    onMiniPlayerViewPress,
    onButtonPress,
  } = useMiniPlayerHook({
    playActionButtonType,
    shouldDisablePlayPauseButtonForLive,
    buttonImgPlay,
    buttonImgPause,
    buttonImgStop,
  });

  const { title, subTitle, imageURL, streamPosition, streamDuration, isLive } =
    mediaStatus;

  // Calculte the streamPosition percentage for progress view
  const percentage = (streamPosition / streamDuration) * 100;

  // Called on view load
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

  console.log(buttonImg, buttonType);
  return isVisible ? (
    <TouchableOpacity
      style={defaultStyles.backgroundLayer}
      onPress={onMiniPlayerViewPress}
    >
      {!isLive ? (
        <View style={defaultStyles.progressBarBackground}>
          <View
            style={{ ...defaultStyles.progressBar, width: `${percentage}%` }}
          />
        </View>
      ) : (
        <></>
      )}
      <View style={defaultStyles.contentView}>
        <Image
          source={imageURL ? { uri: imageURL } : thumbnailImageSourceDefault}
          style={defaultStyles.image}
          resizeMode="contain"
        />
        <View style={defaultStyles.textContentView}>
          <Text style={defaultStyles?.titleText}>
            {getTitleFromVideoInfo({ title, castingTo })}
          </Text>
          <Text style={defaultStyles?.subTitleText}>
            {getSubTitleFromVideoInfo({ title, subTitle, castingTo })}
          </Text>
        </View>
        {buttonImg && buttonType ? (
          <TouchableOpacity
            onPress={onButtonPress}
            style={
              disableButton ? defaultStyles.disableButton : defaultStyles.button
            }
            disabled={disableButton}
          >
            <Image
              source={buttonImg}
              style={
                disableButton
                  ? defaultStyles.disableButtonImg
                  : defaultStyles.buttonImg
              }
            />
          </TouchableOpacity>
        ) : (
          <></>
        )}
      </View>
    </TouchableOpacity>
  ) : (
    <></>
  );
};

/**
 * Default Props
 */
VizbeeMiniCastBar.propTypes = {
  thumbnailImageSourceDefault: PropTypes.number.isRequired,
  backgroundLayerStyle: PropTypes.object,
  progressViewStyle: PropTypes.object,
  titleStyle: PropTypes.object,
  subTitleStyle: PropTypes.object,
  buttonStyle: PropTypes.object,
  disableButtonStyle: PropTypes.object,
  playActionButtonType: PropTypes.string,
  shouldDisablePlayPauseButtonForLive: PropTypes.bool,
  buttonImgPlay: PropTypes.number,
  buttonImgPause: PropTypes.number,
  buttonImgStop: PropTypes.number,
};

VizbeeMiniCastBar.defaultProps = {
  thumbnailImageSourceDefault: Images.defaultThumbnail,
  backgroundLayerStyle: {},
  progressViewStyle: {},
  titleStyle: {},
  subTitleStyle: {},
  buttonStyle: {},
  disableButtonStyle: {},
  playActionButtonType: null,
  shouldDisablePlayPauseButtonForLive: false,
  buttonImgPlay: Images.play,
  buttonImgPause: Images.pause,
  buttonImgStop: Images.stop,
};

export default VizbeeMiniCastBar;
