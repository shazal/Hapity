package com.skhalid.hapity.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.skhalid.hapity.DashboardActivity;
import com.skhalid.hapity.GsonRequest;
import com.skhalid.hapity.NewBroadcastResponse;
import com.skhalid.hapity.R;
import com.skhalid.hapity.VolleySingleton;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspClient;
import net.majorkernelpanic.streaming.video.VideoQuality;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RecordingFragment extends Fragment implements View.OnClickListener,
        RtspClient.Callback,
        Session.Callback,
        SurfaceHolder.Callback,
        RadioGroup.OnCheckedChangeListener {

    public final static String TAG = "RecordingFragment";

    private Button mButtonSave;
    private Button mButtonVideo;
    private ImageButton mButtonStart;
    private ImageButton mButtonFlash;
    private ImageButton mButtonCamera;
    private ImageButton mButtonSettings;
    private RadioGroup mRadioGroup;
    private FrameLayout mLayoutVideoSettings;
    private FrameLayout mLayoutServerSettings;
    private SurfaceView mSurfaceView;
    private TextView mTextBitrate;
    private EditText mEditTextURI;
    private EditText mEditTextPassword;
    private EditText mEditTextUsername;
    private ProgressBar mProgressBar;
    private Session mSession;
    private RtspClient mClient;
    private String shareTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        View rootView=  inflater.inflate(R.layout.main, null);


        Bundle bundle = getArguments();
        shareTitle = bundle.getString("ShareTitle");

        mButtonVideo = (Button) rootView.findViewById(R.id.video);
        mButtonSave = (Button) rootView.findViewById(R.id.save);
        mButtonStart = (ImageButton) rootView.findViewById(R.id.start);
        mButtonFlash = (ImageButton) rootView.findViewById(R.id.flash);
        mButtonCamera = (ImageButton) rootView.findViewById(R.id.camera);
        mButtonSettings = (ImageButton) rootView.findViewById(R.id.settings);
        mSurfaceView = (SurfaceView) rootView.findViewById(R.id.surface);
        mEditTextURI = (EditText) rootView.findViewById(R.id.uri);
        mEditTextUsername = (EditText) rootView.findViewById(R.id.username);
        mEditTextPassword = (EditText) rootView.findViewById(R.id.password);
        mTextBitrate = (TextView) rootView.findViewById(R.id.bitrate);
        mLayoutVideoSettings = (FrameLayout) rootView.findViewById(R.id.video_layout);
        mLayoutServerSettings = (FrameLayout) rootView.findViewById(R.id.server_layout);
        mRadioGroup =  (RadioGroup) rootView.findViewById(R.id.radio);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);

        mRadioGroup.setOnCheckedChangeListener(this);
        mRadioGroup.setOnClickListener(this);

        mButtonStart.setOnClickListener(this);
        mButtonSave.setOnClickListener(this);
        mButtonFlash.setOnClickListener(this);
        mButtonCamera.setOnClickListener(this);
        mButtonVideo.setOnClickListener(this);
        mButtonSettings.setOnClickListener(this);
        mButtonFlash.setTag("off");

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        if (mPrefs.getString("uri", null) != null)
            mLayoutServerSettings.setVisibility(View.GONE);
        mEditTextURI.setText(mPrefs.getString("uri", getString(R.string.default_stream)));
        mEditTextPassword.setText(mPrefs.getString("password", "wozpubuser"));
        mEditTextUsername.setText(mPrefs.getString("username", "Pu8Eg3n3_"));

        // Configures the SessionBuilder
        mSession = SessionBuilder.getInstance()
                .setContext(getActivity().getApplicationContext())
                .setAudioEncoder(SessionBuilder.AUDIO_AAC)
                .setAudioQuality(new AudioQuality(8000,16000))
                .setVideoEncoder(SessionBuilder.VIDEO_H264)
                .setSurfaceView(mSurfaceView)
                .setPreviewOrientation(0)
                .setCallback(this)
                .build();

        // Configures the RTSP client
        mClient = new RtspClient();
        mClient.setSession(mSession);
        mClient.setCallback(this);

        // Use this to force streaming with the MediaRecorder API
        //mSession.getVideoTrack().setStreamingMethod(MediaStream.MODE_MEDIARECORDER_API);

        // Use this to stream over TCP, EXPERIMENTAL!
        //mClient.setTransportMode(RtspClient.TRANSPORT_TCP);

        // Use this if you want the aspect ratio of the surface view to
        // respect the aspect ratio of the camera preview
        //mSurfaceView.setAspectRatioMode(SurfaceView.ASPECT_RATIO_PREVIEW);

        mSurfaceView.getHolder().addCallback(this);

        selectQuality();

        return rootView;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mLayoutVideoSettings.setVisibility(View.GONE);
        mLayoutServerSettings.setVisibility(View.VISIBLE);
        selectQuality();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                mLayoutServerSettings.setVisibility(View.GONE);
                toggleStream();
                break;
            case R.id.flash:
                if (mButtonFlash.getTag().equals("on")) {
                    mButtonFlash.setTag("off");
                    mButtonFlash.setImageResource(R.drawable.ic_flash_on_holo_light);
                } else {
                    mButtonFlash.setImageResource(R.drawable.ic_flash_off_holo_light);
                    mButtonFlash.setTag("on");
                }
                mSession.toggleFlash();
                break;
            case R.id.camera:
                mSession.switchCamera();
                break;
            case R.id.settings:
                if (mLayoutVideoSettings.getVisibility() == View.GONE &&
                        mLayoutServerSettings.getVisibility() == View.GONE) {
                    mLayoutServerSettings.setVisibility(View.VISIBLE);
                } else {
                    mLayoutServerSettings.setVisibility(View.GONE);
                    mLayoutVideoSettings.setVisibility(View.GONE);
                }
                break;
            case R.id.video:
                mRadioGroup.clearCheck();
                mLayoutServerSettings.setVisibility(View.GONE);
                mLayoutVideoSettings.setVisibility(View.VISIBLE);
                break;
            case R.id.save:
                mLayoutServerSettings.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mClient.release();
        mSession.release();
        mSurfaceView.getHolder().removeCallback(this);
    }

    private void selectQuality() {
        int id = mRadioGroup.getCheckedRadioButtonId();
        RadioButton button = (RadioButton) getActivity().findViewById(id);
        if (button == null) return;

        String text = button.getText().toString();
        Pattern pattern = Pattern.compile("(\\d+)x(\\d+)\\D+(\\d+)\\D+(\\d+)");
        Matcher matcher = pattern.matcher(text);

        matcher.find();
        int width = Integer.parseInt(matcher.group(1));
        int height = Integer.parseInt(matcher.group(2));
        int framerate = Integer.parseInt(matcher.group(3));
        int bitrate = Integer.parseInt(matcher.group(4))*1000;

        mSession.setVideoQuality(new VideoQuality(width, height, framerate, bitrate));
        Toast.makeText(getActivity(), ((RadioButton) getActivity().findViewById(id)).getText(), Toast.LENGTH_SHORT).show();

        Log.d(TAG, "Selected resolution: " + width + "x" + height);
    }

    private void enableUI() {
        mButtonStart.setEnabled(true);
        mButtonCamera.setEnabled(true);
    }

    // Connects/disconnects to the RTSP server and starts/stops the stream
    public void toggleStream() {
        mProgressBar.setVisibility(View.VISIBLE);
        if (!mClient.isStreaming()) {
            String ip,port,path;

            // We save the content user inputs in Shared Preferences
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString("uri", mEditTextURI.getText().toString());
            editor.putString("password", mEditTextPassword.getText().toString());
            editor.putString("username", mEditTextUsername.getText().toString());
            editor.commit();

            // We parse the URI written in the Editext
            Pattern uri = Pattern.compile("rtsp://(.+):(\\d*)/(.+)");
            Matcher m = uri.matcher(mEditTextURI.getText()); m.find();
            ip = m.group(1);
            port = m.group(2);
            path = m.group(3);

            mClient.setCredentials(mEditTextUsername.getText().toString(), mEditTextPassword.getText().toString());
            mClient.setServerAddress(ip, Integer.parseInt(port));
            mClient.setStreamPath("/" + path);
            mClient.startStream();
            String url =null;
            if(DashboardActivity.hapityPref.getBoolean("shareLocation",false)){
                url = "http://testing.egenienext.com/project/hapity/webservice/startbroadcast/?title=" + shareTitle + "&geo_location="+DashboardActivity.hapityPref.getString("Lattitude", "0")+","+DashboardActivity.hapityPref.getString("Longitude", "0")+"&allow_user_messages=No&user_id=" + DashboardActivity.hapityPref.getInt("userid", 0);
            } else {
                url = "http://testing.egenienext.com/project/hapity/webservice/startbroadcast/?title=" + shareTitle + "&geo_location=0,0&allow_user_messages=No&user_id=" + DashboardActivity.hapityPref.getInt("userid", 0);
            }

            startBroadcast(url, null);

        } else {
            // Stops the stream and disconnects from the RTSP server
            mClient.stopStream();
        }
    }

    private void logError(final String msg) {
        final String error = (msg == null) ? "Error unknown" : msg;
        // Displays a popup to report the eror to the user
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBitrateUpdate(long bitrate) {
        mTextBitrate.setText("" + bitrate / 1000 + " kbps");
    }

    @Override
    public void onPreviewStarted() {
        if (mSession.getCamera() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mButtonFlash.setEnabled(false);
            mButtonFlash.setTag("off");
            mButtonFlash.setImageResource(R.drawable.ic_flash_on_holo_light);
        }
        else {
            mButtonFlash.setEnabled(true);
        }
    }

    @Override
    public void onSessionConfigured() {

    }

    @Override
    public void onSessionStarted() {
        enableUI();
        mButtonStart.setImageResource(R.drawable.ic_switch_video_active);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSessionStopped() {
        enableUI();
        mButtonStart.setImageResource(R.drawable.ic_switch_video);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSessionError(int reason, int streamType, Exception e) {
        mProgressBar.setVisibility(View.GONE);
        switch (reason) {
            case Session.ERROR_CAMERA_ALREADY_IN_USE:
                break;
            case Session.ERROR_CAMERA_HAS_NO_FLASH:
                mButtonFlash.setImageResource(R.drawable.ic_flash_on_holo_light);
                mButtonFlash.setTag("off");
                break;
            case Session.ERROR_INVALID_SURFACE:
                break;
            case Session.ERROR_STORAGE_NOT_READY:
                break;
            case Session.ERROR_CONFIGURATION_NOT_SUPPORTED:
                VideoQuality quality = mSession.getVideoTrack().getVideoQuality();
                logError("The following settings are not supported on this phone: " +
                        quality.toString() + " " +
                        "(" + e.getMessage() + ")");
                e.printStackTrace();
                return;
            case Session.ERROR_OTHER:
                break;
        }

        if (e != null) {
            logError(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onRtspUpdate(int message, Exception e) {
        switch (message) {
            case RtspClient.ERROR_CONNECTION_FAILED:
            case RtspClient.ERROR_WRONG_CREDENTIALS:
                mProgressBar.setVisibility(View.GONE);
                enableUI();
                logError(e.getMessage());
                e.printStackTrace();
                break;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSession.setPreviewOrientation(90);
        mSession.configure();
        mSession.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mClient.stopStream();
    }

    private void startBroadcast(String url, HashMap<String, String> params) {

        DashboardActivity.showCustomProgress(getActivity(), "", false);

        GsonRequest<NewBroadcastResponse> myReq = new GsonRequest<NewBroadcastResponse>(
                Request.Method.GET,
                url,
                NewBroadcastResponse.class,
                null,
                params,
                startBroadcastSuccessListener(),
                startBroadcastErrorListener());

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(myReq);
    }

    private Response.Listener<NewBroadcastResponse> startBroadcastSuccessListener() {
        return new Response.Listener<NewBroadcastResponse>() {
            @Override
            public void onResponse(NewBroadcastResponse response) {
                try {

                    DashboardActivity.dismissCustomProgress();

                    if(response.status.equalsIgnoreCase("success")){

                    }
                    else
                        Toast.makeText(getActivity(), "Broadcast Not Started. Try Again.", Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        };
    }

    private Response.ErrorListener startBroadcastErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                DashboardActivity.dismissCustomProgress();

                if (error.networkResponse!=null) {
                    if (error.networkResponse.statusCode == 500) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Some Problem With Network", Toast.LENGTH_LONG).show();
                }
            }
        };
    }
}
