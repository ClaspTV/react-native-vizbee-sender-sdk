import React, { useRef, useImperativeHandle } from 'react';
import { findNodeHandle, requireNativeComponent, UIManager, Platform } from 'react-native';

const VizbeeCastButtonWrapper = requireNativeComponent('VizbeeCastButtonView');
const { setColor} = UIManager.getViewManagerConfig('VizbeeCastButtonView').Commands;

const CastButtonView = (props, ref) => {
  const castBtnRef = useRef(null);
  useImperativeHandle(ref, () => ({
    setColor(color) {
      if (Platform.OS === 'android') {
        execCommand(setColor.toString(),[color]);
      } else {
        execCommand('setColor',[color]);
      }
    },
  }));

  function execCommand(key,args) {
    const viewId = findNodeHandle(castBtnRef.current);
    if (viewId) {
      UIManager.dispatchViewManagerCommand(viewId, key, args);
    }
  }
  
  return (
    <VizbeeCastButtonWrapper
      ref={castBtnRef}
      {...props}
      style={{ height: props.style.height, width: props.style.width }}
    />
  );
};

const VizbeeCastButton = React.forwardRef(CastButtonView);

export default VizbeeCastButton;
