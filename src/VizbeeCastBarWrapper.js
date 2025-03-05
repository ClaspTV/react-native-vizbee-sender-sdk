import React, { useState, useRef, useEffect, memo } from "react";
import {
  requireNativeComponent,
  Platform,
  View,
  Dimensions,
  PixelRatio,
  findNodeHandle,
  UIManager,
} from "react-native";
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
  const [screenWidth, setScreenWidth] = useState(
    Dimensions.get("window").width
  );
  const castBarRef = useRef(null);

  // Check for new architecture support once
  useEffect(() => {
    if (global.__turboModuleProxy != null) {
      throw new Error(
        "VizbeeCastBar is not supported with New Architecture enabled. " +
          "Please use the old architecture version."
      );
    }
  }, []);

  // Handle screen dimension changes
  useEffect(() => {
    const handleOrientationChange = ({ window }) => {
      setScreenWidth(window.width);
    };

    const dimensionsSubscription = Dimensions.addEventListener(
      "change",
      handleOrientationChange
    );

    return () => dimensionsSubscription.remove();
  }, []);

  // Create fragment for Android platform
  useEffect(() => {
    if (Platform.OS === "android" && castBarRef.current && isVisible) {
      const viewId = findNodeHandle(castBarRef.current);
      UIManager.dispatchViewManagerCommand(
        viewId,
        UIManager.VizbeeCastBarView.Commands.create,
        [viewId]
      );
    }
    if (!isVisible) {
      setViewHeight(0);
    }
  }, [isVisible]);

  // Event handler for native visibility change events
  const handleVisibilityChange = (event) => {
    const shouldAppear = event.nativeEvent.shouldAppear;
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
      testID={`${testID}-container`}
      style={{
        height: viewHeight,
        width: screenWidth,
        overflow: "hidden",
      }}
    >
      {Platform.OS === "ios" ? (
        <VizbeeCastBar
          testID={`${testID}-ios`}
          height={viewHeight}
          onVisibilityChange={handleVisibilityChange}
          ref={castBarRef}
        />
      ) : Platform.OS === "android" ? (
        <VizbeeCastBar
          testID={`${testID}-android`}
          style={{
            height: PixelRatio.getPixelSizeForLayoutSize(height),
            width: PixelRatio.getPixelSizeForLayoutSize(screenWidth),
          }}
          ref={castBarRef}
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
