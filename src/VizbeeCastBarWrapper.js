import React, { useState } from "react";
import { requireNativeComponent } from "react-native";
import PropTypes from "prop-types";

const VizbeeCastBarView = requireNativeComponent("VizbeeCastBarView");

const VizbeeCastBarWrapper = ({ height, onVisibilityChange }) => {
  const [viewHeight, setViewHeight] = useState(0);

  const onChange = (event) => {
    setViewHeight(event.nativeEvent.shouldAppear ? height || 64 : 0);
    onVisibilityChange && onVisibilityChange(event.nativeEvent.shouldAppear);
  };

  return (
    <VizbeeCastBarView height={viewHeight} onVisibilityChange={onChange} />
  );
};

VizbeeCastBarWrapper.propTypes = {
  height: PropTypes.number,
  onVisibilityChange: PropTypes.func,
};

export default VizbeeCastBarWrapper;
