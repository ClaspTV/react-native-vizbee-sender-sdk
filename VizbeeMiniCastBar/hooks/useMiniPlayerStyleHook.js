import { useEffect } from "react";

/**
   * Define default styles for the SDK component
   */
export const useMiniPlayerStyleHook = ({
  backgroundLayerStyle,
  progressViewStyle,
  titleStyle,
  subTitleStyle,
  buttonStyle,
  disableButtonStyle, 
  defaultApperance
}) => {  

  useEffect(()=>{
    getDefaultStyle();
  },[defaultApperance])

  const getDefaultStyle = () => {
    console.log({backgroundLayerStyle,
      progressViewStyle,
      titleStyle,
      subTitleStyle,
      buttonStyle,
      disableButtonStyle})
    return {
    backgroundLayer: {
      alignItems: "center",
      height:backgroundLayerStyle?.height ?? 64,
      width: backgroundLayerStyle?.width ?? "100%",
      backgroundColor: backgroundLayerStyle?.backgroundColor ?? defaultApperance.viewBackgroundColor,
    },
    progressBarBackground: {
      backgroundColor: progressViewStyle?.backgroundColor ?? defaultApperance.progressViewBackgroundColor,
      height: progressViewStyle?.height ?? 2,
      width: "100%",
    },
    progressBar: {
      backgroundColor: progressViewStyle?.color ?? defaultApperance.progressBarColor,
      height: progressViewStyle?.height ?? 2,
    },
    contentView: {
      flex: 1,
      width: "100%",
      alignItems: "center",
      flexDirection: "row",
    },
    image: {
      aspectRatio: 1,
      height: "100%",
      maxHeight: backgroundLayerStyle?.height ?? 64,
      maxWidth: backgroundLayerStyle?.height ?? 64,
    },
    textContentView: {
      flex: 1,
      alignItems: "flex-start",
      paddingLeft: 10,
      paddingRight: 10,
    },
    titleText: {
      color: defaultApperance.titleTextColor,
      fontSize: 14,
      ...titleStyle,
    },
    subTitleText: {
      color: defaultApperance.subtitleTextColor,
      fontSize: 12,
      ...subTitleStyle,
    },
    button: {
      margin: 5,
      ...buttonStyle,
    },
    buttonImg: {
      aspectRatio: 1,
      height: "100%",
      tintColor: buttonStyle?.color ?? defaultApperance.buttonColor.active,
    },
    disableButton: {
      margin: 5,
      backgroundColor: defaultApperance.buttonColor.inactiveBackground,
      borderRadius: 25,
      ...disableButtonStyle,
    },
    disableButtonImg: {
      aspectRatio: 1,
      height: "100%",
      tintColor: disableButtonStyle?.color ?? defaultApperance.buttonColor.inactive,
    },
  };
}

  return {getDefaultStyle}
}