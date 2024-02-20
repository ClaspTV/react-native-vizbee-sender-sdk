import { useEffect, useState } from "react";
import { VizbeeManager } from "../../../../";
import { LOG_TAG } from "../constants";

/**
 * Custom hook to listen for session status changes for the mini cast bar.
 *
 * @returns {Object} Object containing state variables and functions to handle mini cast bar show/hide.
 */
export const useMiniCastBarSessionListenerHook = () => {
  // Define state
  const [isVisible, setIsVisible] = useState(false);

  // useEffect hook
  useEffect(() => {
    console.debug(`${LOG_TAG} Add VZB_SESSION_STATUS Listener`);
    const sessionStatusListener = VizbeeManager.addListener(
      "VZB_SESSION_STATUS",
      sessionStatusCallback
    );

    return () => {
      console.debug(`${LOG_TAG} Remove VZB_SESSION_STATUS Listener`);
      VizbeeManager.removeAllListeners(sessionStatusListener);
    };
  }, []);

  /**
   * Callback function for the VZB_SESSION_STATUS Listener.
   *
   * @param {Object} sessionStatus - Status containing session information.
   */
  const sessionStatusCallback = (sessionStatus) => {
    console.debug(
      `${LOG_TAG} Vizbee Session Status Change - ${JSON.stringify(
        sessionStatus
      )}`
    );
    if (sessionStatus?.connectionState !== "CONNECTED") {
      setIsVisible(false);
    }
  };

  return { isVisible, setIsVisible };
};
