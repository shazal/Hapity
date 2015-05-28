package com.skhalid.hapity.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.skhalid.hapity.Comments;
import com.skhalid.hapity.CommentsAdapter;
import com.skhalid.hapity.R;

import java.util.ArrayList;

public class BroadcastFragment extends Fragment {
	ArrayList<Comments> commentsArray;
	CommentsAdapter commentAdapter ;
	ListView commentsList;
    private VideoView myVideoView;
    private int position = 0;
    private ProgressDialog progressDialog;
    private MediaController mediaControls;


   
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	 View rootView = inflater.inflate(R.layout.broadcast_view, container, false);
		commentsList = (ListView) rootView.findViewById(R.id.listView1);
        myVideoView = (VideoView) rootView.findViewById(R.id.video_view);
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

       return rootView;
       
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        commentsArray = new ArrayList<Comments>();
        commentsArray.add(new Comments());
        commentsArray.add(new Comments());
        commentsArray.add(new Comments());
        commentsArray.add(new Comments());
        commentsArray.add(new Comments());

        commentAdapter = new CommentsAdapter(getActivity(), R.layout.listitem, commentsArray);
        commentsList.setAdapter(commentAdapter);
	}
    
    @Override
	public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
		super.onAttach(activity);

	}

}
