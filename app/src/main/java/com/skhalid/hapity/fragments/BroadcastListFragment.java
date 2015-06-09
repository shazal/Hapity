package com.skhalid.hapity.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.internal.LinkedTreeMap;
import com.skhalid.hapity.BroadcastDetails;
import com.skhalid.hapity.BroadcastModal;
import com.skhalid.hapity.DashboardActivity;
import com.skhalid.hapity.FollowersInfo;
import com.skhalid.hapity.GsonRequest;
import com.skhalid.hapity.MainBroadcast;
import com.skhalid.hapity.ProfileInfo;
import com.skhalid.hapity.R;
import com.skhalid.hapity.BroadcastListAdapter;
import com.skhalid.hapity.UsersAdapter;
import com.skhalid.hapity.VolleySingleton;

public class BroadcastListFragment extends Fragment {

	ListView BroadcastList;
	BroadcastListAdapter adapter;
	public static ArrayList<BroadcastModal> broadcastArray = new ArrayList<BroadcastModal>();
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.broadcast_list_fragment_layout, container,false);
		BroadcastList = (ListView) rootView.findViewById(R.id.broadcastList);
        DashboardActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		adapter = new BroadcastListAdapter(getActivity(), broadcastArray, getActivity().getResources());

		if(BottomFragment.isHomeActive) {

			setListData();
//			tweatList.setAdapter(adapter);
		}

		return rootView;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


	}

	private void setListData() {

        String url = "http://testing.egenienext.com/project/hapity/webservice/getbroadcast?user_id=0";

        loadAPI(url, null);
		
//		for (int i = 1; i<=10; i++)
//		{
//			final BroadcastModal sched = new BroadcastModal();
//			sched.setUserName("John Doe");
//			tweatArray.add(sched);
//		}
//		Toast.makeText(getActivity(), tweatArray.size()+"", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

    private void loadAPI(String url, HashMap<String, String> params) {

        DashboardActivity.showCustomProgress(getActivity(), "", false);
        GsonRequest<MainBroadcast[]> myReq = new GsonRequest<MainBroadcast[]>(
                Request.Method.GET,
                url,
                MainBroadcast[].class,
                null,
                params,
                createMyReqSuccessListener(),
                createMyReqErrorListener());

        myReq.setRetryPolicy( new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(myReq);
    }


    private Response.Listener<MainBroadcast[]> createMyReqSuccessListener() {
        return new Response.Listener<MainBroadcast[]>() {
            @Override
            public void onResponse(MainBroadcast[] response) {
                try {

                    BroadcastDetails[] bDetails = response[1].broadcast;
//                    LinkedTreeMap<String,LinkedTreeMap<String,String>> obj = ((LinkedTreeMap) response);
                    broadcastArray.clear();
                    for(int i = 0; i<bDetails.length; i++){
                        final BroadcastModal sched = new BroadcastModal();
                        sched.setUserName(bDetails[i].screen_name);
                        sched.setTitle(bDetails[i].title);
                        sched.stream_url=bDetails[i].stream_url;
                        sched.id = bDetails[i].id;
                        sched.broadcast_image = bDetails[i].broadcast_image;
                        sched.numberofLikes = bDetails[i].likes;
                        sched.user_id = bDetails[i].user_id;
                        sched.onlinestatus = bDetails[i].online_status;
                        broadcastArray.add(sched);

                    }
                    Collections.reverse(broadcastArray);
                    DashboardActivity.dismissCustomProgress();
                    BroadcastList.setAdapter(adapter);
                    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                    scheduler.schedule(new Runnable() {

                        @Override
                        public void run() {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Fragment bFragment = (Fragment) getFragmentManager().findFragmentByTag("BroadcastListFragment");
                                    if(bFragment!=null && bFragment.isVisible()){
                                        String url = "http://testing.egenienext.com/project/hapity/webservice/getbroadcast?user_id=0";
                                        loadAPI(url, null);
                                    }
                                }
                            });
                        }
                    }, 3, TimeUnit.MINUTES);
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
                }else {
                    Toast.makeText(getActivity(), "Some Problem With Network", Toast.LENGTH_LONG).show();
                }

            }
        };
    }
	
}
