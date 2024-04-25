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

const VizbeeCastButtonPressable = ({ style }) => {
  const buttonRef = useRef();

  const handlePress = () => {
    // Get the view tag using findNodeHandle
    const viewTag = findNodeHandle(buttonRef.current);

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

  let containerStyle = Object.keys(style)
    .filter((objKey) => objKey !== "height" && objKey !== "width")
    .reduce((newObj, key) => {
      newObj[key] = style[key];
      return newObj;
    }, {});

  let styles = { height: style.height || 24, width: style.width || 24 };

  return (
    <TouchableHighlight
      onPress={handlePress}
      style={containerStyle}
      underlayColor={"transparent"}
    >
      <VizbeeCastButton
        ref={buttonRef}
        style={styles}
        tintColor={style.tintColor}
        disabled={true}
      />
    </TouchableHighlight>
  );
};

export default VizbeeCastButtonPressable;
