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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.internal.LinkedTreeMap;
import com.skhalid.hapity.BroadcastDetails;
import com.skhalid.hapity.BroadcastModal;
import com.skhalid.hapity.Comments;
import com.skhalid.hapity.CommentsAdapter;
import com.skhalid.hapity.DashboardActivity;
import com.skhalid.hapity.GsonRequest;
import com.skhalid.hapity.MainBroadcast;
import com.skhalid.hapity.R;
import com.skhalid.hapity.VolleySingleton;

import java.util.ArrayList;
import java.util.HashMap;

import it.carlom.stikkyheader.core.StikkyHeaderBuilder;
import it.carlom.stikkyheader.core.animator.AnimatorBuilder;
import it.carlom.stikkyheader.core.animator.HeaderStikkyAnimator;

public class BroadcastFragment extends Fragment {
	ArrayList<Comments> commentsArray;
	CommentsAdapter commentAdapter ;
	ListView commentsList;
//    private VideoView myVideoView;
    private ImageView myImageView;
    private int position = 0;
    private ProgressDialog progressDialog;
    private MediaController mediaControls;

    public String bID;

   
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	 View rootView = inflater.inflate(R.layout.broadcast_view, container, false);
        Bundle bundle = getArguments();
        bID = bundle.getString("bID");
		commentsList = (ListView) rootView.findViewById(R.id.listView1);
        myImageView = (ImageView) rootView.findViewById(R.id.header_image);


       return rootView;
       
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        StikkyHeaderBuilder.stickTo(commentsList)
                .setHeader(R.id.header, (ViewGroup) getView())
                .minHeightHeaderDim(R.dimen.min_header)
                .animator(new ParallaxStikkyAnimator())
                .build();
        commentsArray = new ArrayList<Comments>();
        String url = "http://testing.egenienext.com/project/hapity/webservice/get_comments?broadcast_id=" + bID;
        loadAPI(url, null);
	}
    
    @Override
	public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
		super.onAttach(activity);

	}

    private void loadAPI(String url, HashMap<String, String> params) {

        DashboardActivity.showCustomProgress(getActivity(), "", false);
        GsonRequest<Object> myReq = new GsonRequest<Object>(
                Request.Method.GET,
                url,
                Object.class,
                null,
                params,
                createMyReqSuccessListener(),
                createMyReqErrorListener());


        VolleySingleton.getInstance(getActivity()).addToRequestQueue(myReq);
    }


    private Response.Listener<Object> createMyReqSuccessListener() {
        return new Response.Listener<Object>() {
            @Override
            public void onResponse(Object response) {
                try {

                    ArrayList<LinkedTreeMap> resp = ((ArrayList)response);
//                    LinkedTreeMap<String,LinkedTreeMap<String,String>> obj = ((LinkedTreeMap) response);
                    commentsArray.clear();
                    for(int i = 1; i<resp.size(); i++){
                        LinkedTreeMap commentTree = resp.get(i);
                        Comments comment = new Comments();
                        comment.comment = (String)commentTree.get("comment");
                        comment.comment_id = (String)commentTree.get("comment_id");
                        comment.date = (String)commentTree.get("date");
                        comment.profile_picture = (String)commentTree.get("profile_picture");
                        comment.screen_name = (String)commentTree.get("screen_name");
                        comment.user_id = (String)commentTree.get("user_id");
                        commentsArray.add(comment);

                    }
                    DashboardActivity.dismissCustomProgress();
                    commentAdapter = new CommentsAdapter(getActivity(), R.layout.listitem, commentsArray);
                    commentsList.setAdapter(commentAdapter);
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
                if (error.networkResponse != null) {
                    int statuscode = error.networkResponse.statusCode;

                    Toast.makeText(getActivity(), "Some Problem With Web Service", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Some Problem With Network", Toast.LENGTH_LONG).show();
                }
            }
        };
    }


    private class ParallaxStikkyAnimator extends HeaderStikkyAnimator {

        @Override
        public AnimatorBuilder getAnimatorBuilder() {
            View mHeader_image = getHeader().findViewById(R.id.header_image);

            return AnimatorBuilder.create().applyVerticalParallax(mHeader_image);
        }
    }
}
