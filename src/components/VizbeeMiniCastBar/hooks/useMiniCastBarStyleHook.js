import { useEffect } from "react";

/**
 * Custom hook to define default styles for the SDK component.
 *
 * @param {Object} props - Props passed to the hook.
 * @param {Object} props.backgroundLayerStyle - Style for the background layer.
 * @param {Object} props.progressViewStyle - Style for the progress view.
 * @param {Object} props.titleStyle - Style for the title text.
 * @param {Object} props.subTitleStyle - Style for the subtitle text.
 * @param {Object} props.buttonStyle - Style for the buttons.
 * @param {Object} props.disableButtonStyle - Style for the disabled buttons.
 * @param {Object} props.defaultAppearance - Default appearance object containing colors and styles.
 * @returns {Object} Object containing default styles for the SDK component.
 */
export const useMiniCastBarStyleHook = ({
  backgroundLayerStyle,
  progressViewStyle,
  titleStyle,
  subTitleStyle,
  buttonStyle,
  disableButtonStyle,
  defaultAppearance,
}) => {
  useEffect(() => {
    getDefaultStyle();
  }, [defaultAppearance]);

  /**
   * Get the default styles for the SDK component.
   *
   * @returns {Object} Default styles for the SDK component.
   */
  const getDefaultStyle = () => {
    return {
      backgroundLayer: {
        alignItems: "center",
        height: backgroundLayerStyle?.height ?? 64,
        width: backgroundLayerStyle?.width ?? "100%",
        backgroundColor:
          backgroundLayerStyle?.backgroundColor ??
          defaultAppearance.viewBackgroundColor,
      },
      progressBarBackground: {
        backgroundColor:
          progressViewStyle?.backgroundColor ??
          defaultAppearance.progressViewBackgroundColor,
        height: progressViewStyle?.height ?? 2,
        width: "100%",
      },
      progressBar: {
        backgroundColor:
          progressViewStyle?.color ?? defaultAppearance.progressBarColor,
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
        color: defaultAppearance.titleTextColor,
        fontSize: 14,
        ...titleStyle,
      },
      subTitleText: {
        color: defaultAppearance.subtitleTextColor,
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
        tintColor: buttonStyle?.color ?? defaultAppearance.buttonColor.active,
      },
      disableButton: {
        margin: 5,
        backgroundColor: defaultAppearance.buttonColor.inactiveBackground,
        borderRadius: 25,
        ...disableButtonStyle,
      },
      disableButtonImg: {
        aspectRatio: 1,
        height: "100%",
        tintColor:
          disableButtonStyle?.color ?? defaultAppearance.buttonColor.inactive,
      },
    };
  };

  return { getDefaultStyle };
};
