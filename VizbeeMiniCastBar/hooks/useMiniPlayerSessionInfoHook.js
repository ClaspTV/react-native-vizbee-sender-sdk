import { useEffect,useState } from "react"
import {VizbeeManager,  } from "react-native-vizbee-sender-sdk";

export const useMiniPlayerSessionInfoHook = ({isVisible}) => {

    const [castingTo, setCastingTo] = useState("TV");

    useEffect(()=>
    {
        if (isVisible){
            getConnectedDeviceInfo();
        }else{
            setCastingTo("TV");
        }

    },[isVisible])

      /**
   * Get connected device friendly name
   */
const getConnectedDeviceInfo = () => {
    console.debug("Get connected device info");
    VizbeeManager.getSessionConnectedDevice()
      .then((connectedDeviceInfo) => {
        setCastingTo(connectedDeviceInfo?.connectedDeviceFriendlyName || "TV");
      })
      .catch(() => {
        setCastingTo("TV");
      });
  };

  return {castingTo}
}