
import React from "react";
import { View, Image, TouchableOpacity, Text , Appearance} from "react-native";
import { useMiniPlayerSessionListenerHook,useMiniPlayerMediaStatusListenerHook,useMiniPlayerStyleHook,useMiniPlayerSessionInfoHook, useMiniPlayerActionsHook } from "./hooks";
import { getTitleFromVideoInfo,getSubTitleFromVideoInfo, getApperanceColor } from "./helpers";
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
  buttonImgStop
}) => {

  const isDark = Appearance.getColorScheme() === "dark";

  const defaultApperance = getApperanceColor(isDark);

  const {getDefaultStyle} = useMiniPlayerStyleHook({
    backgroundLayerStyle,
    progressViewStyle,
    titleStyle,
    subTitleStyle,
    buttonStyle,
    disableButtonStyle,
    defaultApperance
    });
  
  const defaultStyles = getDefaultStyle()

  const {isVisible,setIsVisible} = useMiniPlayerSessionListenerHook();

  const {
    mediaStatus, 
    isButtonDisabled, 
    buttonImg, 
    buttonType} = useMiniPlayerMediaStatusListenerHook({
      isVisible,
      setIsVisible,
    playActionButtonType,
    shouldDisablePlayPauseButtonForLive,
    buttonImgPlay,
    buttonImgPause,
    buttonImgStop
    });

    const { castingTo } = useMiniPlayerSessionInfoHook({isVisible})

    const {onMiniPlayerViewPress,onButtonPress} = useMiniPlayerActionsHook()

  const {title, subTitle, imageURL, streamPosition, streamDuration, isLive } = mediaStatus;

  // Calculte the streamPosition percentage for progress view
  const percentage = (streamPosition / streamDuration) * 100;


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
          <Text style={defaultStyles?.titleText} numberOfLines={1}>
            {getTitleFromVideoInfo({title, castingTo})}
          </Text>
          <Text style={defaultStyles?.subTitleText} numberOfLines={1}>
            {getSubTitleFromVideoInfo({title, subTitle, castingTo})}
          </Text>
        </View>
        {buttonImg && buttonType ? (
          <TouchableOpacity
            onPress={onButtonPress(buttonType)}
            style={
              isButtonDisabled ? defaultStyles.disableButton : defaultStyles.button
            }
            disabled={isButtonDisabled}
          >
            <Image
              source={buttonImg}
              style={
                isButtonDisabled
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
    thumbnailImageSourceDefault: PropTypes.number,
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
