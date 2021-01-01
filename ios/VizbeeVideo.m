//
//  VizbeeVideo.m
//  RNVizbeeSenderSdk
//

#import "VizbeeVideo.h"

/*

class OriginVideoMetadata {
    var drmTypeMap = [  "Any": VZBScreenDRMType.any,
                        "None": VZBScreenDRMType.none,
                        "PlayReady": VZBScreenDRMType.playReady,
                        "AdobeAccess": VZBScreenDRMType.adobeAccess,
                        "AES128": VZBScreenDRMType.AES128,
                        "WideVineModular": VZBScreenDRMType.wideVineModular,
                        "WideVineClassic": VZBScreenDRMType.wideVineClassic,
                        "Verimatrix": VZBScreenDRMType.verimatrix,
                        "Other": VZBScreenDRMType.other]
    
    var position: Double = 0
    var isAuthenticated: Bool = false
    var description: String = ""
    var guid: String = ""
    var drmType = VZBScreenDRMType.none
    var isLive: Bool = false
    var title: String = ""
    var imageUrl: String = ""
    var sourceUrl: String = ""
    var cuePoints: [Double] = []
    var customProps: Dictionary<String, AnyObject>  =  Dictionary<String, AnyObject> ()
  
    init(video: NSDictionary) {
        position = video["position"] as? Double ?? 0.0;
        isAuthenticated = video["isAuthenticated"] as? Bool ?? false;
        description = video["description"] as? String ?? "";
        guid = video["guid"] as? String ?? "";
        isLive = video["isLive"] as? Bool ?? false;
        title = video["title"] as? String ?? "";
        imageUrl = video["imageUrl"] as? String ?? "";
        sourceUrl = video["sourceUrl"] as? String ?? "";
    
        if let iDrmType = video["drmType"] {
            let sDrmType = iDrmType as! String
            if let cDrmType = drmTypeMap[sDrmType] {
                drmType = cDrmType
            }
        }
        
        cuePoints = video["cuePoints"] as? [Double] ?? []

        customProps = video["customProps"] as? Dictionary<String, AnyObject> ?? [:];
    }
    
    func getDrmStr(type: VZBScreenDRMType) -> String {
        for (key, value ) in drmTypeMap {
            if (type == value) {
                return key
            }
        }
        return "None"
    }
    
    public func toDictionary() -> NSDictionary {
        let result: NSDictionary = [
            "position" : position,
            "isAuthenticated" : isAuthenticated,
            "description" : description,
            "guid" : guid,
            "isLive" : isLive,
            "title" : title,
            "imageUrl" : imageUrl,
            "sourceUrl": sourceUrl,
            "drmType" : getDrmStr(type:drmType),
            "customProps" : customProps,
            "cuePoints" : cuePoints
        ]
        
        return result
    }
    
    init() {
    }
}

*/

@implementation VizbeeVideo

@end
