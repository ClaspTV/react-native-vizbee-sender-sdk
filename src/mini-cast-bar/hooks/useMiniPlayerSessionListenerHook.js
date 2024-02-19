import { useEffect, useState } from "react";
import { VizbeeManager } from "../../../";
import { LOG_TAG } from "../constants";

export const useMiniPlayerSessionListenerHook = () => {
  // Define state
  const [isVisible, setIsVisible] = useState(false);

  // useEffect hook
  useEffect(() => {
    console.debug(LOG_TAG, "Add VZB_SESSION_STATUS Listener");
    const sessionStatusListener = VizbeeManager.addListener(
      "VZB_SESSION_STATUS",
      sessionStatusCallback
    );

    return () => {
      console.debug(LOG_TAG, "Remove VZB_SESSION_STATUS Listener");
      VizbeeManager.removeAllListeners(sessionStatusListener);
    };
  }, []);

  /**
   * Update the state of the mini player controls
   *
   * @param {Object} sessionStatus - status containing session info
   * @returns {void}
   */
  const sessionStatusCallback = (sessionStatus) => {
    console.debug(
      LOG_TAG,
      `Vizbee Session Status Change - ${JSON.stringify(sessionStatus)}`
    );
    if (sessionStatus?.connectionState !== "CONNECTED") {
      setIsVisible(false);
    }
  };

  return { isVisible, setIsVisible };
};
