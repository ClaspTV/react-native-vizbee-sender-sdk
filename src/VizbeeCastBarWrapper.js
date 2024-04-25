import React, { useState } from "react";
import { requireNativeComponent, Platform } from "react-native";
import PropTypes from "prop-types";

const VizbeeCastBarView = requireNativeComponent("VizbeeCastBarView");

const VizbeeCastBarWrapper = ({
  height,
  onVisibilityChange,
  getMinHeight,
  getActive,
}) => {
  const iosHeight = Platform.OS === "ios" ? height : undefined;

  const [viewHeight, setViewHeight] = useState(iosHeight);

  const onChange = (event) => {
    setViewHeight(event.nativeEvent.shouldAppear ? height || 64 : 0);
    onVisibilityChange && onVisibilityChange(event.nativeEvent.shouldAppear);
  };

  return (
    <VizbeeCastBarView
      height={viewHeight}
      onVisibilityChange={onChange}
      getMinHeight={getMinHeight}
      getActive={getActive}
    />
  );
};

VizbeeCastBarWrapper.propTypes = {
  height: Platform.OS === "ios" ? PropTypes.number : PropTypes.undefined,
  onVisibilityChange: PropTypes.func,
  getMinHeight: PropTypes.func,
  getActive: PropTypes.func,
};

export default VizbeeCastBarWrapper;
