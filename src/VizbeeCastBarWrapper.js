import React, { useState, useEffect, memo } from "react";
import { requireNativeComponent, Platform, View } from "react-native";
import PropTypes from "prop-types";

// Require the native component
const VizbeeCastBar = requireNativeComponent("VizbeeCastBarView");

/**
 * Wrapper component for the Vizbee Cast Bar View.
 * This component manages the height and visibility of the cast bar view
 * across iOS and Android platforms.
 *
 * @param {Object} props - Component props
 * @param {boolean} [props.isVisible=true] - Controls whether the cast bar should be displayed
 * @param {number} [props.height=64] - The height of the cast bar in density-independent pixels
 * @param {function} [props.onVisibilityChange] - Callback when visibility changes
 * @param {string} [props.testID] - Test identifier for automated testing
 * @returns {JSX.Element|null} - Cast bar component or null when hidden
 */
const VizbeeCastBarWrapper = ({
  isVisible = true,
  height = 64,
  onVisibilityChange,
  testID = "vizbee-cast-bar",
}) => {
  const [viewHeight, setViewHeight] = useState(0);

  useEffect(() => {
    if (!isVisible) {
      setViewHeight(0);
    }
  }, [isVisible]);

  // Event handler for native visibility change events
  const handleVisibilityChange = (event) => {
    const shouldAppear = event?.nativeEvent?.shouldAppear ?? false;
    setViewHeight(shouldAppear ? height : 0);

    if (onVisibilityChange) {
      onVisibilityChange(shouldAppear);
    }
  };

  // Don't render anything if not visible
  if (!isVisible) {
    return null;
  }

  // Platform-specific rendering
  return (
    <View
      style={{
        height: viewHeight,
        backgroundColor: "transparent",
        overflow: "hidden",
      }}
    >
      {Platform.OS === "ios" ? (
        <VizbeeCastBar
          testID={`${testID}-ios`}
          height={height}
          onVisibilityChange={handleVisibilityChange}
        />
      ) : Platform.OS === "android" ? (
        <VizbeeCastBar
          testID={`${testID}-android`}
          height={viewHeight}
          onVisibilityChange={handleVisibilityChange}
        />
      ) : null}
    </View>
  );
};

VizbeeCastBarWrapper.propTypes = {
  isVisible: PropTypes.bool,
  height: PropTypes.number,
  onVisibilityChange: PropTypes.func,
  testID: PropTypes.string,
};

// Memoize to prevent unnecessary rerenders
export default memo(VizbeeCastBarWrapper);
