package tv.vizbee.rnsender;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Objects;

import tv.vizbee.api.VizbeeContext;
import tv.vizbee.api.session.SessionState;
import tv.vizbee.api.session.SessionStateListener;
import tv.vizbee.api.session.VideoClient;
import tv.vizbee.api.session.VideoStatus;
import tv.vizbee.api.session.VizbeeScreen;
import tv.vizbee.api.session.VizbeeSession;
import tv.vizbee.api.session.VizbeeSessionManager;

public class VizbeeMiniCastController extends Fragment implements
SessionStateListener, VideoClient.VideoStatusListener, View.OnClickListener {

    public static final String LOG_TAG = VizbeeMiniCastController.class.getName();

    private View fragmentView;
    private ImageView thumbnailView;
    private TextView titleLabelView;
    private TextView subtitleLabelView;
    private ImageView playbackButton;
    private ProgressBar progressBar;

    private VideoClient videoClient;

    private int lastPlayerState = 0;
    private String lastThumbnail = "";
    private int thumbnailSize = 64;

    private String buttonColor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(LOG_TAG, "onActivityCreated");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.v(LOG_TAG, "onCreateView");
        return inflater.inflate(R.layout.layout_vizbee_mini_cast_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.v(LOG_TAG, "onViewCreated");

        thumbnailView = view.findViewById(R.id.thumbnail_view);
        lastThumbnail = "";
        titleLabelView = view.findViewById(R.id.title_view);
        subtitleLabelView = view.findViewById(R.id.subtitle_view);
        playbackButton = view.findViewById(R.id.button);
        progressBar = view.findViewById(R.id.progress_bar);
        fragmentView = view;
        fragmentView.setOnClickListener(this);
        fragmentView.setVisibility(GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "onResume");
        addSessionStateListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "onDestroy");
        removeVideoStatusListener();
        removeSessionStateListener();
    }

    void setBackgroundColor(String backgroundColor) {
        int cardBackgroundColor = ContextCompat.getColor(getContext(), R.color.player_controls_bg);
        if (!TextUtils.isEmpty(backgroundColor)) {
            cardBackgroundColor = Color.parseColor(backgroundColor);
        }
        fragmentView.setBackgroundColor(cardBackgroundColor);
    }

    void setButtonColor(String buttonColor) {
        this.buttonColor = buttonColor;
    }

    void show() {
        if (fragmentView.getVisibility() != VISIBLE) {
            fragmentView.setVisibility(VISIBLE);
        }
    }

    void hide() {
        if (null != fragmentView && fragmentView.getVisibility() != GONE) {
            fragmentView.setVisibility(GONE);
        }
        clear();
    }

    private void updateView(VideoStatus videoStatus) {

        if (null != fragmentView && null != videoStatus) {

            // set title & subtitle
            if (null != videoStatus.getTitle()) {

                titleLabelView.setText(videoStatus.getTitle());
                String subTitle = videoStatus.getSubTitle();
                if (TextUtils.isEmpty(subTitle)) {
                    subTitle = getCastingToMessage();
                }
                subtitleLabelView.setText(subTitle);
            } else {
                // show fallback text when unable to resolve video during join scenario
                titleLabelView.setText(getCastingToMessage());
                subtitleLabelView.setText("");
            }

            // set image
            String thumbnailUrl = videoStatus.getImageUrl();
            if (!Objects.equals(thumbnailUrl, lastThumbnail)) {
                if (!TextUtils.isEmpty(thumbnailUrl)) {
                    if (null != thumbnailView) {
                        Glide.with(this).load(thumbnailUrl).into(thumbnailView);
                    }
                    lastThumbnail = thumbnailUrl;
                }
            }

            // update progress
            int streamDuration;
            if (videoStatus.isStreamLive()) {
                streamDuration = 1;
            } else {
                streamDuration = (int) videoStatus.getStreamDuration();
            }
            progressBar.setMax(streamDuration);

            int streamPosition;
            if (videoStatus.isStreamLive()) {
                streamPosition = 1;
            } else {
                streamPosition = (int) videoStatus.getStreamPosition();
            }
            progressBar.setProgress(streamPosition);
        }
    }

    private String getCastingToMessage() {

        String castingToMessage = "";
        VizbeeSessionManager sessionManager = VizbeeContext.getInstance().getSessionManager();
        if (null != sessionManager &&
            null != sessionManager.getCurrentSession() &&
            null != sessionManager.getCurrentSession().getVizbeeScreen()) {
            VizbeeScreen screen = sessionManager.getCurrentSession().getVizbeeScreen();
            castingToMessage = getResources().getString(R.string.cast_casting_to_device, screen.getScreenInfo().getFriendlyName());
        }
        return castingToMessage;
    }

    private void clear() {
        if(null != fragmentView) {
            thumbnailView.setImageResource(0);
            titleLabelView.setText("");
            subtitleLabelView.setText("");
            progressBar.setProgress(0);
            progressBar.setMax(0);
            playbackButton.setOnClickListener(null);
        }
        lastPlayerState = 0;
    }

    @Override
    public void onSessionStateChanged(int newState) {

        if (newState == SessionState.CONNECTED) {
            addVideoStatusListener();
            lastPlayerState = 0;
            videoClient = getSessionVideoClient();
        } else if (newState == SessionState.NOT_CONNECTED
            || newState == SessionState.NO_DEVICES_AVAILABLE ) {
            videoClient = null;
            removeVideoStatusListener();
            hide();
        }
    }

    @Override
    public void onVideoStatusUpdated(VideoStatus videoStatus) {
        if (null != videoStatus) {
            show();
            updateView(videoStatus);
            updateButtonState(videoStatus.getPlayerState());
        }
    }

    private void updateButtonState(int playerState) {
        if (playerState == VideoStatus.PLAYER_STATE_PLAYING ||
            playerState == VideoStatus.PLAYER_STATE_PAUSED) {
            setPlaybackButtonState(playerState);
        } else if (playerState == VideoStatus.PLAYER_STATE_IDLE) {
            hide();
        }

        lastPlayerState = playerState;
    }

    private void setPlaybackButtonState(int playerState) {
        if (null != playbackButton) {
            if (playerState != lastPlayerState) {
                if (null != getContext()) {
                    int playbackButtonColor = ContextCompat.getColor(getContext(), R.color.media_route_button_bg);
                    if (!TextUtils.isEmpty(buttonColor)) {
                        playbackButtonColor = Color.parseColor(buttonColor);
                    }
                    if (VideoStatus.PLAYER_STATE_PLAYING == playerState) {
                        playbackButton.setImageResource(R.drawable.cast_ic_mini_controller_pause);
                        DrawableCompat.setTint(playbackButton.getDrawable(), playbackButtonColor);
                        playbackButton.setOnClickListener(createPlaybackButtonClickListener(true));

                    } else if (VideoStatus.PLAYER_STATE_PAUSED == playerState) {
                        playbackButton.setImageResource(R.drawable.cast_ic_mini_controller_play);
                        DrawableCompat.setTint(playbackButton.getDrawable(), playbackButtonColor);
                        playbackButton.setOnClickListener(createPlaybackButtonClickListener(false));
                    }
                }
            }
        }
    }

    private View.OnClickListener createPlaybackButtonClickListener(boolean isPlaying) {

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != videoClient) {
                    if (isPlaying) {
                        videoClient.pause();
                    } else {
                        videoClient.play();
                    }
                }
            }
        };
    }

    @Override
    public void onClick(View view) {
        VizbeeContext.getInstance().getSessionManager().onCastIconClicked(requireActivity());
    }

    private int lastUpdatedState = 0; // UNKNOWN

    private void addSessionStateListener() {

        // sanity
        this.removeSessionStateListener();

        VizbeeSessionManager sessionManager = VizbeeContext.getInstance().getSessionManager();
        if (null != sessionManager) {

            Log.i(LOG_TAG, "Adding session state listener");
            sessionManager.addSessionStateListener(this);

            // force first update
            onSessionStateChanged(sessionManager.getSessionState());
        }
    }

    private void removeSessionStateListener() {

        VizbeeSessionManager sessionManager = VizbeeContext.getInstance().getSessionManager();
        if (null != sessionManager) {
            Log.i(LOG_TAG, "Removing session state listener");
            sessionManager.removeSessionStateListener(this);
        }
    }

    //----------------
    // Video client listener
    //----------------

    private VideoClient getSessionVideoClient() {

        VizbeeSessionManager sessionManager = VizbeeContext.getInstance().getSessionManager();
        if (null == sessionManager) {
            return null;
        }

        VizbeeSession currentSession = sessionManager.getCurrentSession();
        if (null == currentSession) {
            return null;
        }

        VideoClient videoClient = currentSession.getVideoClient();
        return videoClient;
    }

    private void addVideoStatusListener() {

        // sanity
        this.removeVideoStatusListener();

        Log.v(LOG_TAG, "TRYING to add video status listener");
        VideoClient videoClient = getSessionVideoClient();
        if (null != videoClient) {
            videoClient.addVideoStatusListener(this);
            Log.i(LOG_TAG, "SUCCESS adding video status listener");

            // force first update
            onVideoStatusUpdated(videoClient.getVideoStatus());
        } else {

            Log.w(LOG_TAG, "FAILED to add video status listener");
        }
    }

    private void removeVideoStatusListener() {

        Log.v(LOG_TAG, "TRYING to remove video status listener");
        VideoClient videoClient = getSessionVideoClient();
        if (null != videoClient) {
            Log.i(LOG_TAG, "SUCCESS removing video status listener");
            videoClient.removeVideoStatusListener(this);
        }
    }
}
