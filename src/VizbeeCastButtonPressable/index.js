import React, { useRef } from "react";
import {
  NativeModules,
  findNodeHandle,
  Pressable,
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
      console.log("handlePress", viewTag, VizbeeCastButtonViewManager);
      if (Platform.OS === "ios") {
        VizbeeCastButtonViewManager.simulateButtonClick(viewTag);
      } else {
        // Call the native method to simulate button press
        UIManager.dispatchViewManagerCommand(
          viewTag,
          UIManager.getViewManagerConfig("VizbeeCastButtonView").Commands
            .simulateButtonClick,
          []
        );
      }
    }
  };

  let styles = { height: style.height || 24, width: style.width || 24 };
  return (
    <Pressable onPress={handlePress} style={style}>
      <VizbeeCastButton
        ref={buttonRef}
        style={styles}
        tintColor={style.tintColor}
        enabled={false}
      />
    </Pressable>
  );
};

export default VizbeeCastButtonPressable;
