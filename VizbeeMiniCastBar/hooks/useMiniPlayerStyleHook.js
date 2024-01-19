import { StyleSheet } from "react-native";

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
}) => {
  const defaultStyles = StyleSheet.create({
    backgroundLayer: {
      alignItems: "center",
      height: backgroundLayerStyle?.height ?? 64,
      width: backgroundLayerStyle?.width ?? "100%",
      backgroundColor: backgroundLayerStyle?.backgroundColor ?? "#FFFFFF",
    },
    progressBarBackground: {
      backgroundColor: progressViewStyle?.backgroundColor ?? "#000000",
      height: progressViewStyle?.height ?? 2,
      width: "100%",
    },
    progressBar: {
      backgroundColor: progressViewStyle?.color ?? "red",
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
      color: "#000000",
      fontSize: 14,
      ...titleStyle,
    },
    subTitleText: {
      color: "#000000",
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
      tintColor: buttonStyle?.color ?? "#000000",
    },
    disableButton: {
      margin: 5,
      backgroundColor: "#cccccc",
      borderRadius: 25,
      ...disableButtonStyle,
    },
    disableButtonImg: {
      aspectRatio: 1,
      height: "100%",
      tintColor: disableButtonStyle?.color ?? "#808080",
    },
  });

  return defaultStyles;
};
