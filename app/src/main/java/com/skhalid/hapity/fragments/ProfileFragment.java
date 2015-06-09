package com.skhalid.hapity.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.skhalid.hapity.DashboardActivity;
import com.skhalid.hapity.FollowersInfo;
import com.skhalid.hapity.GsonRequest;
import com.skhalid.hapity.Jsonexample;
import com.skhalid.hapity.ProfileInfo;
import com.skhalid.hapity.R;
import com.skhalid.hapity.RoundedImageView;
import com.skhalid.hapity.UserInfo;
import com.skhalid.hapity.UserInfo1;
import com.skhalid.hapity.Users;
import com.skhalid.hapity.UsersAdapter;
import com.skhalid.hapity.VolleySingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import info.hoang8f.android.segmented.SegmentedGroup;
import io.vov.vitamio.LibsChecker;

import static android.view.View.VISIBLE;

public class ProfileFragment extends Fragment implements io.vov.vitamio.MediaPlayer.OnInfoListener, io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener {
    private int position = 0;
    private ProgressDialog progressDialog;
    private MediaController mediaControls;
    ImageView imagep ;
    TextView viewProfile;
    LinearLayout overlayLL;
    ArrayList<FollowersInfo> usersArray;
    UsersAdapter userAdapter ;
    ListView usersList;
    RadioButton rb1;
    RadioButton rb2;
    Button FollowButton;
    TextView Name;
    EditText commentText;
    ImageView sendimg;

    String userid="0";
    String bID = "";
    String sURL = "";

    FollowersInfo[] followers;
    FollowersInfo[] following;
    UserInfo1[] ProfileInfo;

    private String path = "https://ia700401.us.archive.org/19/items/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
    private Uri uri;
    private io.vov.vitamio.widget.VideoView mVideoView;
    private ProgressBar pb;
    private TextView downloadRateView, loadRateView;
    private  io.vov.vitamio.widget.MediaController mc;
    private ImageButton mPlayButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.profile, container, false);
        Bundle bundle = getArguments();
        bID = bundle.getString("bID");
        sURL = bundle.getString("sURL");
        userid = bundle.getString("uID");
        SegmentedGroup segmented2 = (SegmentedGroup) rootView.findViewById(R.id.segmented2);
        segmented2.setTintColor(Color.parseColor("#554979"));

        mVideoView = (io.vov.vitamio.widget.VideoView) rootView.findViewById(R.id.buffer);
        pb = (ProgressBar) rootView.findViewById(R.id.probar);

        downloadRateView = (TextView) rootView.findViewById(R.id.download_rate);
        loadRateView = (TextView) rootView.findViewById(R.id.load_rate);


        // Setup a play button to start the video
        mPlayButton = (ImageButton) rootView.findViewById(R.id.play_button);
        mPlayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                    mVideoView.start();
                    // hide button once playback starts
                    mPlayButton.setVisibility(View.GONE);

            }
        });

        imagep = (ImageView) rootView.findViewById(R.id.dp);
        viewProfile = (TextView) rootView.findViewById(R.id.view_profile);
        overlayLL = (LinearLayout) rootView.findViewById(R.id.overlay_LL);
        usersList = (ListView) rootView.findViewById(R.id.listView2);
         rb1 = (RadioButton) rootView.findViewById(R.id.button21);
        rb2 = (RadioButton) rootView.findViewById(R.id.button22);
//        myVideoView.setEnabled(true);
        overlayLL.setVisibility(View.GONE);
        FollowButton = (Button) rootView.findViewById(R.id.Followbtn);
        Name = (TextView) rootView.findViewById(R.id.user_name);

        commentText = (EditText) rootView.findViewById(R.id.writeComment);
        sendimg = (ImageView) rootView.findViewById(R.id.sendimg);
        return rootView;

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (LibsChecker.checkVitamioLibs(getActivity())) {
            if(sURL.equalsIgnoreCase("")) {
                uri = Uri.parse(path);
            } else {
                uri = Uri.parse(sURL);
            }
            mVideoView.setVideoURI(uri);
            mVideoView.requestFocus();
            mVideoView.setOnInfoListener(this);
            mVideoView.setOnBufferingUpdateListener(this);
            mVideoView.setOnPreparedListener(new io.vov.vitamio.MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(io.vov.vitamio.MediaPlayer mediaPlayer) {
                    // optional need Vitamio 4.0
                    mediaPlayer.setPlaybackSpeed(1.0f);

                }
            });

            mVideoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (mVideoView.isPlaying()) {
                        mVideoView.pause();
                        mPlayButton.setVisibility(View.VISIBLE);
                    }

                    return false;
                }
            });
        }

        String url = "http://testing.egenienext.com/project/hapity/webservice/get_profile_info?user_id=" +userid ;
        loadAPI(url, null);

        RoundedImageView roundedImageView = new RoundedImageView(getActivity());

        Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.lums);
        Bitmap resized = Bitmap.createScaledBitmap(icon, 150, 150, true);
        resized = roundedImageView.getCroppedBitmap(resized, 150);
        imagep.setImageBitmap(resized);





        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayLL.setVisibility(View.VISIBLE);
//                myVideoView.setMediaController(null);
                rb1.setChecked(true);
            }
        });

        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < followers.length; i++) {
                    usersArray.add(followers[i]);
                }

                userAdapter = new UsersAdapter(getActivity().getApplicationContext(), R.layout.userlistitem, usersArray);
                usersList.setAdapter(userAdapter);
            }
        });

        rb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersArray.clear();
                for (int i = 0; i < following.length; i++) {
                    usersArray.add(following[i]);
                }

                userAdapter = new UsersAdapter(getActivity().getApplicationContext(), R.layout.userlistitem, usersArray);
                usersList.setAdapter(userAdapter);
            }
        });
        usersArray = new ArrayList<FollowersInfo>();

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        FollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FollowButton.getText().toString().equalsIgnoreCase("UnFollow")) {
                    DashboardActivity.showCustomProgress(getActivity(), "", false);
                    String url = "http://testing.egenienext.com/project/hapity/webservice/unfollow_user?follower_id=" + DashboardActivity.hapityPref.getInt("userid", 0) + "&following_id=" + userid;
                    GsonRequest<Jsonexample> myReq = new GsonRequest<Jsonexample>(
                            Request.Method.GET,
                            url,
                            Jsonexample.class,
                            null,
                            null,
                            createMyReqSuccessListenerFUF(),
                            createMyReqErrorListenerFUF());
                    myReq.setRetryPolicy( new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    VolleySingleton.getInstance(getActivity()).addToRequestQueue(myReq);
                } else {
                    DashboardActivity.showCustomProgress(getActivity(), "", false);
                    String url = "http://testing.egenienext.com/project/hapity/webservice/follow_user?follower_id=" + DashboardActivity.hapityPref.getInt("userid", 0) + "&following_id=" + userid;
                    GsonRequest<Jsonexample> myReq = new GsonRequest<Jsonexample>(
                            Request.Method.GET,
                            url,
                            Jsonexample.class,
                            null,
                            null,
                            createMyReqSuccessListenerFUF(),
                            createMyReqErrorListenerFUF());

                    myReq.setRetryPolicy( new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    VolleySingleton.getInstance(getActivity()).addToRequestQueue(myReq);

                }
            }
        });

        sendimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DashboardActivity.showCustomProgress(getActivity(), "", false);
                String url = "http://testing.egenienext.com/project/hapity/webservice/post_comment?user_id=" + DashboardActivity.hapityPref.getInt("userid", 0) + "&broadcast_id=" + bID + "&comment=" + commentText.getText().toString();
                GsonRequest<Jsonexample> myReq = new GsonRequest<Jsonexample>(
                        Request.Method.GET,
                        url,
                        Jsonexample.class,
                        null,
                        null,
                        createMyReqSuccessListenerComment(),
                        createMyReqErrorListenerComment());

                myReq.setRetryPolicy( new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                VolleySingleton.getInstance(getActivity()).addToRequestQueue(myReq);
            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);

    }

    private void loadAPI(String url, HashMap<String, String> params) {


        DashboardActivity.showCustomProgress(getActivity(), "", false);
        GsonRequest<ProfileInfo[]> myReq = new GsonRequest<ProfileInfo[]>(
                Request.Method.GET,
                url,
                ProfileInfo[].class,
                null,
                params,
                createMyReqSuccessListener(),
                createMyReqErrorListener());

        myReq.setRetryPolicy( new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(myReq);
    }


    private Response.Listener<ProfileInfo[]> createMyReqSuccessListener() {
        return new Response.Listener<ProfileInfo[]>() {
            @Override
            public void onResponse(ProfileInfo[] response) {
                try {
                    DashboardActivity.dismissCustomProgress();

                    String Status = response[0].status;
                    followers = response[2].followers;
                     following = response[3].following;
                    ProfileInfo = response[1].profile_info;
                    for(int i =0; i < followers.length; i++) {
                        usersArray.add(followers[i]);
                        if(followers[i].sid.equalsIgnoreCase(DashboardActivity.hapityPref.getInt("userid",0)+"")){
                            FollowButton.setText("UnFollow");
                        }
                    }

                    userAdapter = new UsersAdapter(getActivity().getApplicationContext(), R.layout.userlistitem, usersArray);
                    usersList.setAdapter(userAdapter);
                    Name.setText(ProfileInfo[0].screen_name);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DashboardActivity.dismissCustomProgress();
                if (error.networkResponse!=null) {
                    int statuscode = error.networkResponse.statusCode;
                    Toast.makeText(getActivity(), "Some Problem With Web Service", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getActivity(), "Some Problem With Network", Toast.LENGTH_LONG).show();
                }

            }
        };
    }

    private Response.Listener<Jsonexample> createMyReqSuccessListenerFUF() {
        return new Response.Listener<Jsonexample>() {
            @Override
            public void onResponse(Jsonexample response) {
                try {
                    DashboardActivity.dismissCustomProgress();
                    Toast.makeText(getActivity(), "Successfully Followed/Unfollowed", Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        };
    }

    private Response.ErrorListener createMyReqErrorListenerFUF() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DashboardActivity.dismissCustomProgress();
                if (error.networkResponse!=null) {
                    int statuscode = error.networkResponse.statusCode;

                    Toast.makeText(getActivity(), "Some Problem With Web Service", Toast.LENGTH_LONG).show();

                }else {
                    Toast.makeText(getActivity(), "Some Problem With Network", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private Response.Listener<Jsonexample> createMyReqSuccessListenerComment() {
        return new Response.Listener<Jsonexample>() {
            @Override
            public void onResponse(Jsonexample response) {
                try {
                    DashboardActivity.dismissCustomProgress();
                    Toast.makeText(getActivity(), "Comment Posted Successfully", Toast.LENGTH_LONG).show();
                    commentText.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        };
    }

    private Response.ErrorListener createMyReqErrorListenerComment() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DashboardActivity.dismissCustomProgress();
                if (error.networkResponse!=null) {
                    int statuscode = error.networkResponse.statusCode;

                    Toast.makeText(getActivity(), "Some Problem With Web Service", Toast.LENGTH_LONG).show();

                }else {
                    Toast.makeText(getActivity(), "Some Problem With Network", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    @Override
    public void onBufferingUpdate(io.vov.vitamio.MediaPlayer mp, int percent) {
        loadRateView.setText(percent + "%");

    }

    @Override
    public boolean onInfo(io.vov.vitamio.MediaPlayer mp, int what, int extra) {
        switch (what) {
            case io.vov.vitamio.MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    pb.setVisibility(View.VISIBLE);
                    downloadRateView.setText("");
                    loadRateView.setText("");
                    downloadRateView.setVisibility(View.VISIBLE);
                    loadRateView.setVisibility(View.VISIBLE);

                }
                break;
            case io.vov.vitamio.MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mVideoView.start();
                pb.setVisibility(View.GONE);
                downloadRateView.setVisibility(View.GONE);
                loadRateView.setVisibility(View.GONE);
                break;
            case io.vov.vitamio.MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                downloadRateView.setText("" + extra + "kb/s" + "  ");
                break;
        }
        return true;
    }
}
