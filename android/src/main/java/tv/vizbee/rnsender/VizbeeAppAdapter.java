package tv.vizbee.rnsender;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import tv.vizbee.api.ISmartPlayAdapter;
import tv.vizbee.api.ScreenType;
import tv.vizbee.api.VideoMetadata;
import tv.vizbee.api.VideoStreamInfo;
import tv.vizbee.utils.ICommandCallback;
import tv.vizbee.utils.VizbeeError;

public class VizbeeAppAdapter implements ISmartPlayAdapter {

    public static final String LOG_TAG = VizbeeAppAdapter.class.getName();

    // adapter APIs

    @Override
    public void getVideoInfoByGUID(@NonNull String guid, ICommandCallback<Object> iCommandCallback) {

        Log.d(LOG_TAG, "getVideoInfoByGUID");
        iCommandCallback.onFailure(VizbeeError.newError("getVideoInfoByGUID: not implemented"));
    }

    @Override
    public void getMetadataFromVideo(@NonNull Object appVideoObject, ICommandCallback<VideoMetadata> iCommandCallback) {

        Log.d(LOG_TAG, "getMetadataFromVideo");

        if (!(appVideoObject instanceof VizbeeVideo)) {
            iCommandCallback.onFailure(VizbeeError.newError("getMetadataFromVideo appVideoObject could not be cast to VizbeeVideo"));
            Log.e(LOG_TAG, "getMetadataFromVideo:error appVideoObject could not be cast to VizbeeVideo");
            return;
        }

        VizbeeVideo vizbeeVideo = (VizbeeVideo) appVideoObject;

        VideoMetadata videoMetadata = new VideoMetadata();
        videoMetadata.setGUID(vizbeeVideo.getGuid());
        videoMetadata.setTitle(vizbeeVideo.getTitle());
        videoMetadata.setSubtitle(vizbeeVideo.getSubtitle());
        videoMetadata.setImageURL(vizbeeVideo.getImageUrl());
        videoMetadata.setLive(vizbeeVideo.isLive());
        videoMetadata.setCustomMetadata(new JSONObject(vizbeeVideo.getCustomProperties()));

        iCommandCallback.onSuccess(videoMetadata);
        Log.d(LOG_TAG, "getMetadataFromVideo:completed");
    }

    @Override
    public void getStreamingInfoFromVideo(@NonNull Object appVideoObject, ScreenType screenType, ICommandCallback<VideoStreamInfo> iCommandCallback) {

        Log.d(LOG_TAG, "getStreamingInfoFromVideo");

        if (!(appVideoObject instanceof VizbeeVideo)) {
            iCommandCallback.onFailure(VizbeeError.newError("getStreamingInfoFromVideo appVideoObject could not be cast to VizbeeVideo"));
            Log.e(LOG_TAG, "getStreamingInfoFromVideo:error appVideoObject could not be cast to VizbeeVideo");
            return;
        }

        VizbeeVideo vizbeeVideo = (VizbeeVideo) appVideoObject;

        VideoStreamInfo videoStreamInfo = new VideoStreamInfo();
        videoStreamInfo.setGUID(vizbeeVideo.getGuid());
        videoStreamInfo.setVideoURL(vizbeeVideo.getStreamUrl());
        videoStreamInfo.setCustomStreamInfo(new JSONObject(vizbeeVideo.getCustomProperties()));

        iCommandCallback.onSuccess(videoStreamInfo);
    }

    @Override
    public void playOnLocalDevice(Context context, Object object, long position, boolean autoPlay) {
        Log.d(LOG_TAG, "playOnLocalDevice");
    }
}