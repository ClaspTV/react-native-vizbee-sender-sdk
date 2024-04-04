import React from "react";
import { NativeModules, TouchableHighlight } from "react-native";
import { VizbeeCastButton } from "../../";

const VizbeeNativeManager = NativeModules.VizbeeNativeManager || {};

const VizbeeCastButtonPressable = ({ style }) => {
  const handlePress = () => {
    VizbeeNativeManager.smartCast();
  };

  let containerHeight = Object.keys(style)
    .filter((objKey) => objKey !== "height" && objKey !== "width")
    .reduce((newObj, key) => {
      newObj[key] = style[key];
      return newObj;
    }, {});

  let styles = { height: style.height || 24, width: style.width || 24 };

  return (
    <TouchableHighlight onPress={handlePress} style={containerHeight}>
      <VizbeeCastButton
        style={styles}
        tintColor={style.tintColor}
        enabled={false}
      />
    </TouchableHighlight>
  );
};

export default VizbeeCastButtonPressable;
