package com.jadefrh.nightylogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCAudioTrack;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCMediaConfig;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientAudioTracksCallback;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientVideoTracksCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCSessionConnectionCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCSessionEventsCallback;
import com.quickblox.videochat.webrtc.exception.QBRTCException;
import com.quickblox.videochat.webrtc.view.QBRTCSurfaceView;
import com.quickblox.videochat.webrtc.view.QBRTCVideoTrack;

import org.webrtc.EglBase;
import org.webrtc.VideoRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jadefrh.nightylogin.InitActivity.MY_PREFS_NAME;
import static com.quickblox.videochat.webrtc.QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;

public class VideoActivity extends AppCompatActivity implements QBRTCClientSessionCallbacks, QBRTCSessionConnectionCallbacks, QBRTCClientVideoTracksCallbacks, QBRTCClientAudioTracksCallback {

    private String qbLogin;
    private String qbPassword = "Fa8grOp89bKgi76gFPsm";

    static final String APP_ID = "57763";
    static final String AUTH_KEY = "FAd4zvhKvsALGwD";
    static final String AUTH_SECRET = "f2DFnCfrkcmvepE";
    static final String ACCOUNT_KEY = "RyqpYix2Y54TzqeSACQs";

    private QBRTCClient rtcClient;
    private QBRTCSurfaceView surfaceView;

    private Integer quickblox_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Intent i = getIntent();
        quickblox_id = i.getIntExtra("quickblox_id", 0);
        Integer user_id = i.getIntExtra("user_id", 0);
        Integer call_max_duration = i.getIntExtra("call_max_duration", 0);
        Integer call_ring_duration = i.getIntExtra("call_ring_duration", 0);
        Integer call_delay_interaction = i.getIntExtra("call_delay_interaction", 0);

        System.out.println("quickblox : " + quickblox_id);

        SharedPreferences prefs = this.getSharedPreferences(MY_PREFS_NAME, this.MODE_PRIVATE);
        qbLogin = prefs.getString("user_email", null);

        System.out.println("LOGIN WITH: " + qbLogin);
        System.out.println("LOGIN WITH: " + qbPassword);

        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);

//        surfaceView = (QBRTCSurfaceView) findViewById(R.id.localView);
//        EglBase eglContext = QBRTCClient.getInstance(this).getEglContext();
//        surfaceView.init(eglContext.getEglBaseContext(), null);

        final QBUser user = new QBUser(qbLogin, qbPassword);

        // CREATE SESSION WITH USER
        // If you use create session with user data,
        // then the user will be logged in automatically
        QBAuth.createSession(user).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle bundle) {

                System.out.println("VIDEO: CREATESESSION ONSUCCESS");

                user.setId(session.getUserId());
                // INIT CHAT SERVICE
                QBChatService chatService = QBChatService.getInstance();

//                // LOG IN CHAT SERVICE
                chatService.login(user, new QBEntityCallback<QBUser>() {

                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        //success
                        initQBRTCClient();
                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        //error
                    }
                });
            }

            @Override
            public void onError(QBResponseException errors) {
                //error
                System.out.println("VIDEO: CREATESESSION ONERROR");
            }
        });

        //initQBRTCClient();

    }

    private void initQBRTCClient() {
        rtcClient = QBRTCClient.getInstance(this);

        System.out.println("VIDEO: INIT QBRTCCLIENT");
        QBChatService.getInstance().getVideoChatWebRTCSignalingManager()
                .addSignalingManagerListener(new QBVideoChatSignalingManagerListener() {
                    @Override
                    public void signalingCreated(QBSignaling qbSignaling, boolean createdLocally) {

                        System.out.println("VIDEO: ADDSIGNALMANAGERTRUC");

                        if (!createdLocally) {
                            rtcClient.addSignaling((QBWebRTCSignaling) qbSignaling);
                        }
                    }
                });

        rtcClient.prepareToProcessCalls();
        rtcClient.addSessionCallbacksListener(VideoActivity.this);

        QBRTCTypes.QBConferenceType qbConferenceType = QB_CONFERENCE_TYPE_VIDEO;

        // Initiate opponents list
        List<Integer> opponents = new ArrayList<Integer>();
        opponents.add(quickblox_id); //12345 - QBUser ID

        // Set user information
        // User can set any string key and value in user info
        // Then retrieve this data from sessions which is returned in callbacks
        // and parse them as he wish
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("key", "value");

        // Init session
        QBRTCSession session = QBRTCClient.getInstance(VideoActivity.this).createNewSessionWithOpponents(opponents, qbConferenceType);

        // Start call
        session.startCall(userInfo);

        System.out.println("ON VA APPELER L'UTILISATEUR #" + quickblox_id);

    }

    private void fillVideoView(int userId, QBRTCSurfaceView videoView, QBRTCVideoTrack videoTrack,
                               boolean remoteRenderer) {
        videoTrack.addRenderer(new VideoRenderer(videoView));
    }

    @Override
    public void onLocalVideoTrackReceive(QBRTCSession qbrtcSession, QBRTCVideoTrack qbrtcVideoTrack) {
        System.out.println("VIDEO: onLocalVideoTrackReceive()");
    }

    @Override
    public void onRemoteVideoTrackReceive(QBRTCSession qbrtcSession, QBRTCVideoTrack qbrtcVideoTrack, Integer integer) {
        System.out.println("VIDEO: onRemoteVideoTrackReceive()");
    }

    @Override
    public void onStartConnectToUser(QBRTCSession qbrtcSession, Integer integer) {
        System.out.println("VIDEO: onStartConnectToUser()");
    }

    @Override
    public void onConnectedToUser(QBRTCSession qbrtcSession, Integer integer) {
        System.out.println("VIDEO: onConnectedToUser()");
    }

    @Override
    public void onConnectionClosedForUser(QBRTCSession qbrtcSession, Integer integer) {
        System.out.println("VIDEO: onConnectionClosedForUser()");
    }

    @Override
    public void onDisconnectedFromUser(QBRTCSession qbrtcSession, Integer integer) {
        System.out.println("VIDEO: onDisconnectedFromUser()");
    }

    @Override
    public void onDisconnectedTimeoutFromUser(QBRTCSession qbrtcSession, Integer integer) {
        System.out.println("VIDEO: onDisconnectedTimeoutFromUser()");
    }

    @Override
    public void onConnectionFailedWithUser(QBRTCSession qbrtcSession, Integer integer) {
        System.out.println("VIDEO: onConnectionFailedWithUser()");
    }

    @Override
    public void onError(QBRTCSession qbrtcSession, QBRTCException e) {
        System.out.println("VIDEO: onError()");
    }

    @Override
    public void onLocalAudioTrackReceive(QBRTCSession qbrtcSession, QBRTCAudioTrack qbrtcAudioTrack) {
        System.out.println("VIDEO: onLocalAudioTrackReceive()");
    }

    @Override
    public void onRemoteAudioTrackReceive(QBRTCSession qbrtcSession, QBRTCAudioTrack qbrtcAudioTrack, Integer integer) {
        System.out.println("VIDEO: onRemoteAudioTrackReceive()");
    }

    @Override
    public void onUserNotAnswer(QBRTCSession qbrtcSession, Integer integer) {
        System.out.println("VIDEO: onUserNotAnswer()");
    }

    @Override
    public void onCallRejectByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
        System.out.println("VIDEO: onCallRejectByUser()");
    }

    @Override
    public void onCallAcceptByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
        System.out.println("VIDEO: onCallAcceptByUser()");
    }

    @Override
    public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
        System.out.println("VIDEO: onReceiveHangUpFromUser()");
    }

    @Override
    public void onSessionClosed(QBRTCSession qbrtcSession) {
        System.out.println("VIDEO: onSessionClosed()");
    }

    @Override
    public void onReceiveNewSession(QBRTCSession qbrtcSession) {
        System.out.println("VIDEO: onReceiveNewSession()");
        System.out.println("ON EST EN TRAIN DE T'APPELER MON POTE");

        // obtain received user info
//        Map<String,String> userInfo = qbrtcSession.getUserInfo();

        // .....
        // ..... your code
        // .....


        // Set userInfo
        // User can set any string key and value in user info
        Map<String,String> userInfo = new HashMap<String,String>();
        userInfo.put("Key", "Value");

        // Accept incoming call
        qbrtcSession.acceptCall(userInfo);
    }

    @Override
    public void onUserNoActions(QBRTCSession qbrtcSession, Integer integer) {
        System.out.println("VIDEO: onUserNoActions()");
    }

    @Override
    public void onSessionStartClose(QBRTCSession qbrtcSession) {
        System.out.println("VIDEO: onSessionStartClose()");
    }
}
