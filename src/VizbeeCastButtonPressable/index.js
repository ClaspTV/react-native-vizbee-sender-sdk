import React, { useRef } from "react";
import {
  NativeModules,
  findNodeHandle,
  TouchableHighlight,
  Platform,
  UIManager,
} from "react-native";
import { VizbeeCastButton } from "../../";

const VizbeeCastButtonViewManager = NativeModules.VizbeeCastButtonView || {};

// Usage:
// Everything in ViewStyle Interface is supported in the style props
// Eg: <VizbeeCastButtonPressable style={{"height": 24, width: "24"}}/>

const VizbeeCastButtonPressable = ({ style }) => {
  const castButtonRef = useRef();
  const handlePress = () => {
    // Get the view tag using findNodeHandle
    const viewTag = findNodeHandle(castButtonRef.current);

    if (viewTag != null) {
      // Call the method with the view tag
      if (Platform.OS === "ios") {
        VizbeeCastButtonViewManager.simulateButtonClick(viewTag);
      } else if (Platform.OS === "android") {
        // Call the native method to simulate button press
        UIManager.dispatchViewManagerCommand(
          viewTag,
          UIManager.getViewManagerConfig("VizbeeCastButtonView").Commands
            .simulateButtonClick,
          []
        );
      } else {
        console.error("Not a supported platform");
      }
    }
  };

  let castButtonWrapperStyle = Object.keys(style)
    .filter((objKey) => objKey !== "height" && objKey !== "width")
    .reduce((newObj, key) => {
      newObj[key] = style[key];
      return newObj;
    }, {});
  let castButtonStyles = {
    height: style.height || 24,
    width: style.width || 24,
  };

  return (
    <TouchableHighlight
      onPress={handlePress}
      style={castButtonWrapperStyle}
      underlayColor={"transparent"}
    >
      <VizbeeCastButton
        ref={castButtonRef}
        style={castButtonStyles}
        tintColor={style.tintColor}
        disabled={true}
      />
    </TouchableHighlight>
  );
};

export default VizbeeCastButtonPressable;
