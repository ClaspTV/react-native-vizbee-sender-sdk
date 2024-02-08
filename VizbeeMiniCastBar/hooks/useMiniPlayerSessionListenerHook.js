
import { useEffect, useState } from "react";
import {VizbeeManager} from "react-native-vizbee-sender-sdk";

export const useMiniPlayerSessionListenerHook = () => {

  // Define state
  const [isVisible, setIsVisible] = useState(false);

  // useEffect hook
  useEffect(()=>{
    console.debug("Add VZB_SESSION_STATUS Listener");
    const sessionStatusListener = VizbeeManager.addListener(
      "VZB_SESSION_STATUS",
      sessionStatusCallback
    );

    return () => {
      console.debug("Remove VZB_SESSION_STATUS Listener");
      VizbeeManager.removeListener(sessionStatusListener);
    };
  },[])

  /**
   * Update the state of the mini player controls
   *
   * @param {Object} sessionStatus - status containing session info
   * @returns {void}
   */
  const sessionStatusCallback = (sessionStatus) => {
    global.console.info(
      `HomeScreen::_handleVizbeeSessionStatusChange - ${JSON.stringify(
        sessionStatus
      )}`
    );
    if (sessionStatus?.connectionState !== "CONNECTED") {
        setIsVisible(false)
    }
  };


  return {isVisible,setIsVisible}
}