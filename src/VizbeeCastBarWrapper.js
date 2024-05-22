import React, { useState, useRef, useEffect } from "react";
import {
  requireNativeComponent,
  Platform,
  View,
  Dimensions,
} from "react-native";
import PropTypes from "prop-types";

// Require the native component
const VizbeeMiniCastBar =
  Platform.OS === "ios"
    ? requireNativeComponent("VizbeeMiniCastBarView")
    : null;

/**
 * Wrapper component for the Vizbee Mini Cast Bar View.
 * This component manages the height and visibility of the mini cast bar view.
 *
 * @param {number} height - The height of the mini cast bar view.
 * @param {function} onVisibilityChange - Callback function triggered when the visibility of the mini cast bar changes.
 * @returns {JSX.Element|null} - React element representing the Vizbee Mini Cast Bar Wrapper or null if not on iOS.
 */
const VizbeeCastBarWrapper = ({ height = 64, onVisibilityChange }) => {
  if (Platform.OS !== "ios") {
    return null; // Render nothing if not on iOS
  }

  const [viewHeight, setViewHeight] = useState(0);
  const ref = useRef(null);
  const screenWidth = Dimensions.get("window").width;

  // Event handler for visibility change
  const onChange = (event) => {
    setViewHeight(event.nativeEvent.shouldAppear ? height : 0);
    onVisibilityChange && onVisibilityChange(event.nativeEvent.shouldAppear);
  };

  return (
    <View
      style={{
        height: viewHeight,
        width: screenWidth,
        overflow: "hidden",
      }}
    >
      {/* Render the VizbeeMiniCastBar component */}
      <VizbeeMiniCastBar
        height={viewHeight}
        onVisibilityChange={onChange}
        ref={ref}
      />
    </View>
  );
};

// Prop types validation
VizbeeCastBarWrapper.propTypes = {
  height: PropTypes.number,
  onVisibilityChange: PropTypes.func,
};

export default VizbeeCastBarWrapper;
