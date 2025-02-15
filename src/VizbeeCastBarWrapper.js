import React, { useState, useRef, useEffect } from "react";
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
 * This component manages the height and visibility of the cast bar view.
 *
 * @param {number} height - The height of the cast bar view.
 * @param {function} onVisibilityChange - Callback function triggered when the visibility of the cast bar changes.
 * @returns {JSX.Element|null} - React element representing the Vizbee Cast Bar Wrapper or null.
 */
const VizbeeCastBarWrapper = ({ height = 64, onVisibilityChange }) => {
  const [viewHeight, setViewHeight] = useState(0);
  const ref = useRef(null);
  const [screenWidth, setScreenWidth] = useState(
    Dimensions.get("window").width
  );

  useEffect(() => {
    const handleOrientationChange = () => {
      const { width } = Dimensions.get("window");
      setScreenWidth(width);
    };

    const dimensionsSubscription = Dimensions.addEventListener(
      "change",
      handleOrientationChange
    );

    // Clean up the event listener on component unmount
    return () => {
      dimensionsSubscription?.remove();
    };
  }, []);

  useEffect(() => {
    // Create fragment for Android platform
    if (Platform.OS === "android" && null != ref.current) {
      const viewId = findNodeHandle(ref.current);
      createFragment(viewId);
    }
  }, []);

  // Function to create fragment for Android platform
  const createFragment = (viewId) =>
    UIManager.dispatchViewManagerCommand(
      viewId,
      UIManager.VizbeeCastBarView.Commands.create,
      [viewId]
    );

  // Event handler for visibility change
  const onChange = (event) => {
    setViewHeight(event.nativeEvent.shouldAppear ? height : 0);
    onVisibilityChange && onVisibilityChange(event.nativeEvent.shouldAppear);
  };

  const isNewArch = () => {
    // Check for TurboModules
    const hasTurboModule = global.__turboModuleProxy != null;

    return hasTurboModule;
  };

  if (isNewArch()) {
    throw new Error(
      "VizbeeCastBar is not supported with New Architecture enabled. " +
        "Please use the old architecture version."
    );
  }

  return (
    <View
      style={{
        height: viewHeight,
        width: screenWidth,
        overflow: "hidden",
      }}
    >
      {/* Render the VizbeeMiniCastBar component */}
      {Platform.OS === "ios" ? (
        <VizbeeCastBar
          height={viewHeight}
          onVisibilityChange={onChange}
          ref={ref}
        />
      ) : Platform.OS === "android" ? (
        <VizbeeCastBar
          style={{
            height: PixelRatio.getPixelSizeForLayoutSize(height), // Converts dpi to px, provide desired height
            width: PixelRatio.getPixelSizeForLayoutSize(screenWidth), // Converts dpi to px, provide desired width
          }}
          ref={ref}
          onVisibilityChange={onChange}
        />
      ) : (
        <></>
      )}
    </View>
  );
};

// Prop types validation
VizbeeCastBarWrapper.propTypes = {
  height: PropTypes.number,
  onVisibilityChange: PropTypes.func,
};

export default VizbeeCastBarWrapper;
