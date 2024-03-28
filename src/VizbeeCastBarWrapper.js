import React, { useState } from "react";
import { requireNativeComponent } from "react-native";
import PropTypes from "prop-types";

const VizbeeCastBarView = requireNativeComponent("VizbeeCastBarView");

const VizbeeCastBarWrapper = ({
  height,
  onVisibilityChange,
  getMinHeight,
  getActive,
}) => {
  const [viewHeight, setViewHeight] = useState(0);

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
  height: PropTypes.number,
  onVisibilityChange: PropTypes.func,
  getMinHeight: PropTypes.func,
  getActive: PropTypes.func,
};

export default VizbeeCastBarWrapper;
