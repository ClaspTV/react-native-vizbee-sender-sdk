import { useEffect, useState } from "react";
import { VizbeeManager } from "../../../../";
import { LOG_TAG } from "../constants";

/**
 * Custom hook to retrieve session information for the mini cast bar.
 *
 * @param {Object} props - Props passed to the hook.
 * @param {boolean} props.isVisible - Current visibility state of the mini cast bar.
 * @returns {Object} Object containing casted device name.
 */
export const useMiniCastBarSessionInfoHook = ({ isVisible }) => {
  // Define state
  const [castingTo, setCastingTo] = useState("TV");

  useEffect(() => {
    if (isVisible) {
      getConnectedDeviceInfo();
    } else {
      setCastingTo("TV");
    }
  }, [isVisible]);

  /**
   * Get connected device friendly name.
   */
  const getConnectedDeviceInfo = () => {
    console.debug(`${LOG_TAG} Get connected device info called...`);
    VizbeeManager.getSessionConnectedDevice()
      .then((connectedDeviceInfo) => {
        setCastingTo(connectedDeviceInfo?.connectedDeviceFriendlyName || "TV");
      })
      .catch(() => {
        setCastingTo("TV");
      });
  };

  return { castingTo };
};
