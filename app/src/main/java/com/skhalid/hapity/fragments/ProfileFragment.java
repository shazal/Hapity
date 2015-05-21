package com.skhalid.hapity.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.VideoView;

import com.skhalid.hapity.R;
import com.skhalid.hapity.RoundedImageView;
import com.skhalid.hapity.Users;
import com.skhalid.hapity.UsersAdapter;

import java.util.ArrayList;

import info.hoang8f.android.segmented.SegmentedGroup;

public class ProfileFragment extends Fragment {
    private VideoView myVideoView;
    private int position = 0;
    private ProgressDialog progressDialog;
    private MediaController mediaControls;
    ImageView imagep ;
    TextView viewProfile;
    LinearLayout overlayLL;
    ArrayList<Users> usersArray;
    UsersAdapter userAdapter ;
    ListView usersList;
    RadioButton rb1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile, container, false);
        SegmentedGroup segmented2 = (SegmentedGroup) rootView.findViewById(R.id.segmented2);
        segmented2.setTintColor(Color.parseColor("#554979"));

        myVideoView = (VideoView) rootView.findViewById(R.id.video_view1);
        imagep = (ImageView) rootView.findViewById(R.id.dp);
        viewProfile = (TextView) rootView.findViewById(R.id.view_profile);
        overlayLL = (LinearLayout) rootView.findViewById(R.id.overlay_LL);
        usersList = (ListView) rootView.findViewById(R.id.listView2);
         rb1 = (RadioButton) rootView.findViewById(R.id.button21);
        myVideoView.setEnabled(true);
        overlayLL.setVisibility(View.GONE);
        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RoundedImageView roundedImageView = new RoundedImageView(getActivity());

        Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.lums);
        Bitmap resized = Bitmap.createScaledBitmap(icon, 150, 150, true);
        resized = roundedImageView.getCroppedBitmap(resized, 150);
        imagep.setImageBitmap(resized);

        if (mediaControls == null) {
            mediaControls = new MediaController(getActivity());
        }
        try {
            myVideoView.setMediaController(mediaControls);
            myVideoView.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.example));

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        myVideoView.requestFocus();
        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                //progressDialog.dismiss();
                myVideoView.seekTo(position);
                if (position == 0) {
                    myVideoView.pause();
                } else {
                    myVideoView.pause();
                }
            }
        });

        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayLL.setVisibility(View.VISIBLE);
                myVideoView.setMediaController(null);
                rb1.setChecked(true);
            }
        });

        usersArray = new ArrayList<Users>();
        usersArray.add(new Users());
        usersArray.add(new Users());
        usersArray.add(new Users());
        usersArray.add(new Users());
        usersArray.add(new Users());

        userAdapter = new UsersAdapter(getActivity().getApplicationContext(), R.layout.userlistitem, usersArray);
        usersList.setAdapter(userAdapter);

    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);

    }

}
