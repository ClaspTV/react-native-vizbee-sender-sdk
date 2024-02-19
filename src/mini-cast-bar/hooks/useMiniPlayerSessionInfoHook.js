import { useEffect, useState } from "react";
import { VizbeeManager } from "../../../";
import { LOG_TAG } from "../constants";

export const useMiniPlayerSessionInfoHook = ({ isVisible }) => {
  const [castingTo, setCastingTo] = useState("TV");

  useEffect(() => {
    if (isVisible) {
      getConnectedDeviceInfo();
    } else {
      setCastingTo("TV");
    }
  }, [isVisible]);

  /**
   * Get connected device friendly name
   */
  const getConnectedDeviceInfo = () => {
    console.debug(LOG_TAG, "Get connected device info called...");
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
