import React, {
  useState,
  useRef,
  useEffect,
  useMemo,
  useCallback,
} from "react";
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
 * @param {Object} props - Component props
 * @param {number} props.height - The height of the cast bar view
 * @param {boolean} props.isVisible - Whether the cast bar is visible
 * @param {function} props.onVisibilityChange - Callback triggered when visibility changes
 * @returns {JSX.Element|null} - React element representing the Vizbee Cast Bar or null
 */
const VizbeeCastBarWrapper = ({
  height = 64,
  onVisibilityChange,
  isVisible = true,
  testID = "vizbee-cast-bar",
}) => {
  const [viewHeight, setViewHeight] = useState(0);
  const castBarRef = useRef(null);
  const [screenWidth, setScreenWidth] = useState(
    Dimensions.get("window").width
  );

  // Check if the new architecture is being used (for early error detection)
  useEffect(() => {
    if (global.__turboModuleProxy != null) {
      console.error(
        "VizbeeCastBar is not supported with New Architecture enabled. " +
          "Please use the old architecture version."
      );
    }
  }, []);

  // Handle orientation changes
  useEffect(() => {
    const handleOrientationChange = ({ window }) => {
      setScreenWidth(window.width);
    };

    const dimensionsSubscription = Dimensions.addEventListener(
      "change",
      handleOrientationChange
    );

    return () => {
      dimensionsSubscription?.remove();
    };
  }, []);

  // Create Android fragment
  useEffect(() => {
    if (Platform.OS === "android" && castBarRef.current) {
      const viewId = findNodeHandle(castBarRef.current);
      if (viewId && UIManager.VizbeeCastBarView?.Commands?.create) {
        UIManager.dispatchViewManagerCommand(
          viewId,
          UIManager.VizbeeCastBarView.Commands.create,
          [viewId]
        );
      }
    }
  }, []);

  // Event handler for visibility change
  const handleVisibilityChange = useCallback(
    (event) => {
      const shouldAppear = event.nativeEvent.shouldAppear;
      console.log("Cast bar visibility changed:", shouldAppear);
      onVisibilityChange?.(shouldAppear);
      setViewHeight(height);
    },
    [onVisibilityChange]
  );

  // Platform-specific styles
  const containerStyle = useMemo(
    () => ({
      height: isVisible ? viewHeight : 0,
      width: screenWidth,
      overflow: "hidden",
    }),
    [viewHeight, screenWidth, isVisible]
  );

  // Platform-specific cast bar properties
  const castBarProps = useMemo(() => {
    if (Platform.OS === "ios") {
      return {
        height: isVisible ? height : 64,
        onVisibilityChange: handleVisibilityChange,
        ref: castBarRef,
      };
    } else if (Platform.OS === "android") {
      return {
        style: {
          height: PixelRatio.getPixelSizeForLayoutSize(isVisible ? height : 0),
          width: PixelRatio.getPixelSizeForLayoutSize(screenWidth),
        },
        ref: castBarRef,
        onVisibilityChange: handleVisibilityChange,
      };
    }
    return {};
  }, [height, screenWidth, isVisible, handleVisibilityChange]);

  // Don't render on unsupported platforms
  if (Platform.OS !== "ios" && Platform.OS !== "android") {
    return null;
  }

  return (
    <View style={containerStyle} testID={testID}>
      <VizbeeCastBar {...castBarProps} />
    </View>
  );
};

// Prop types validation
VizbeeCastBarWrapper.propTypes = {
  height: PropTypes.number,
  isVisible: PropTypes.bool,
  onVisibilityChange: PropTypes.func,
  testID: PropTypes.string,
};

export default VizbeeCastBarWrapper;
