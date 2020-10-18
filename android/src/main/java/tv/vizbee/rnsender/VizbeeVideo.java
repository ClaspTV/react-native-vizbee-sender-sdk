package tv.vizbee.rnsender;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import static com.facebook.react.bridge.ReadableType.Map;

public class VizbeeVideo {

    public static final String LOG_TAG = VizbeeVideo.class.getName();

    private String guid;

    // metadata
    private String title;
    private String subtitle;
    private String imageUrl;
    private boolean isLive;

    // streamInfo
    private String streamUrl;
    // this.tracks = {};
    private double startPositionInSeconds = 0;

    // custom 
    private Map<String, Object> customProperties = new HashMap<String, Object>();

    public java.lang.String getGuid() {
        return guid;
    }

    public java.lang.String getTitle() {
        return title;
    }

    public java.lang.String getSubtitle() {
        return subtitle;
    }

    public java.lang.String getImageUrl() {
        return imageUrl;
    }

    public boolean isLive() {
        return isLive;
    }

    public java.lang.String getStreamUrl() {
        return streamUrl;
    }

    public double getStartPositionInSeconds() {
        return startPositionInSeconds;
    }

    public void setStartPositionInSeconds(double position) {
        this.startPositionInSeconds = position;
    }

    public Map<String, Object> getCustomProperties() {
        return customProperties;
    }

    public VizbeeVideo(ReadableMap vizbeeVideo) {

        guid = vizbeeVideo.hasKey("guid") ? vizbeeVideo.getString("guid") : "";
        
        title = vizbeeVideo.hasKey("title") ? vizbeeVideo.getString("title") : "";
        subtitle = vizbeeVideo.hasKey("subtitle") ? vizbeeVideo.getString("subtitle") : "";
        imageUrl = vizbeeVideo.hasKey("imageUrl") ? vizbeeVideo.getString("imageUrl") : "";
        isLive = vizbeeVideo.hasKey("isLive") ? vizbeeVideo.getBoolean("isLive") : false;

        streamUrl = vizbeeVideo.hasKey("streamUrl") ? vizbeeVideo.getString("streamUrl") : "";
        startPositionInSeconds = vizbeeVideo.hasKey("startPositionInSeconds") ? vizbeeVideo.getDouble("startPositionInSeconds") : 0;

        customProperties = new HashMap<String, Object>();
        if (vizbeeVideo.hasKey("customProperties")) {
            customProperties = vizbeeVideo.getMap("customProperties").toHashMap();
        }
    }

    public WritableMap toWritableMap() {

        WritableMap resultMap = Arguments.createMap();
        
        resultMap.putString("guid", guid);

        resultMap.putString("title", title);
        resultMap.putString("subtitle", subtitle);
        resultMap.putString("imageUrl", imageUrl);
        resultMap.putBoolean("isLive", isLive);

        resultMap.putString("streamUrl", streamUrl);
        resultMap.putDouble("startPositionInSeconds", startPositionInSeconds);
        
        resultMap.putMap("customProperties", toWritableMap(customProperties));

        return resultMap;
    }

    private WritableMap toWritableMap(Map<String, Object> properties) {

        WritableMap resultMap = Arguments.createMap();

        try {
            JSONObject json = new JSONObject(properties.toString());
            if (json != null) {
                resultMap = convertJsonToMap(json);
            }
            else {
                Log.e(LOG_TAG, "toWritableMap error: invalid custom properties");
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "toWritableMap error: ", e.getCause());
        }

        return resultMap;
    }

    private static WritableMap convertJsonToMap(JSONObject jsonObject) throws JSONException {

        WritableMap map = new WritableNativeMap();

        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                map.putMap(key, convertJsonToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                map.putArray(key, convertJsonToArray((JSONArray) value));
            } else if (value instanceof  Boolean) {
                map.putBoolean(key, (Boolean) value);
            } else if (value instanceof  Integer) {
                map.putInt(key, (Integer) value);
            } else if (value instanceof  Double) {
                map.putDouble(key, (Double) value);
            } else if (value instanceof String)  {
                map.putString(key, (String) value);
            } else {
                map.putString(key, value.toString());
            }
        }
        return map;
    }

    private static WritableArray convertJsonToArray(JSONArray jsonArray) throws JSONException {

        WritableArray array = new WritableNativeArray();

        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            if (value instanceof JSONObject) {
                array.pushMap(convertJsonToMap((JSONObject) value));
            } else if (value instanceof  JSONArray) {
                array.pushArray(convertJsonToArray((JSONArray) value));
            } else if (value instanceof  Boolean) {
                array.pushBoolean((Boolean) value);
            } else if (value instanceof  Integer) {
                array.pushInt((Integer) value);
            } else if (value instanceof  Double) {
                array.pushDouble((Double) value);
            } else if (value instanceof String)  {
                array.pushString((String) value);
            } else {
                array.pushString(value.toString());
            }
        }
        return array;
    }

}