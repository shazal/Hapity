package com.skhalid.hapity.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.skhalid.hapity.R;

public class BottomFragment extends Fragment implements OnClickListener
{
	public static ImageButton topics_btn;
	public static ImageButton profileButton;
	public static ImageButton alertButton;
	public static ImageButton homeButton;
	public static boolean isTypesActive = false;
	public static boolean isMyListsActive = false;
	public static boolean isAlertActive = false;
	public static boolean isHomeActive = false;
	LinearLayout topicLayout,homeLayout,profileLayout,alertLayout;
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView=  inflater.inflate(R.layout.bottom_fragment_layout, null);
		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		topics_btn = (ImageButton) getActivity().findViewById(R.id.browseBtn);
		profileButton = (ImageButton) getActivity().findViewById(R.id.mylistBtn);
		alertButton = (ImageButton) getActivity().findViewById(R.id.cameraBtn);
		homeButton = (ImageButton) getActivity().findViewById(R.id.listsBtn);
		homeLayout = (LinearLayout) getActivity().findViewById(R.id.listsEvent);
		alertLayout = (LinearLayout) getActivity().findViewById(R.id.cameraEvent);
		profileLayout = (LinearLayout) getActivity().findViewById(R.id.mylistEvent);
		topicLayout = (LinearLayout) getActivity().findViewById(R.id.browseEvent);
		topicLayout.setOnClickListener(this);
		profileLayout.setOnClickListener(this);
		alertLayout.setOnClickListener(this);
		homeLayout.setOnClickListener(this);
		homeButton.setOnClickListener(this);
		alertButton.setOnClickListener(this);
		topics_btn.setOnClickListener(this);
		profileButton.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		 if((v.getId() == R.id.browseEvent) || (v.getId() == R.id.browseBtn))
		{
			if(!isTypesActive)
			{
				try {
					isHomeActive = false;
					homeButton.setImageDrawable(getResources().getDrawable(R.drawable.lists_normal));
					isTypesActive = true;
					topics_btn.setImageDrawable(getResources().getDrawable(R.drawable.browse_pressed));
					isAlertActive = false;
					alertButton.setImageDrawable(getResources().getDrawable(R.drawable.camera_normal));
					isMyListsActive = false;
					profileButton.setImageDrawable(getResources().getDrawable(R.drawable.mylist_normal));

					BrowseFragment browseFragment = new BrowseFragment();
					FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.dash_container, browseFragment, "BrowseFragment");
					transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                    transaction.addToBackStack("browse");
                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
					transaction.commit();

				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}
			
		}
		else if((v.getId() == R.id.mylistBtn) || (v.getId() == R.id.mylistEvent))
		{
			if(!isMyListsActive)
			{
				try {
					isHomeActive = false;
					homeButton.setImageDrawable(getResources().getDrawable(R.drawable.lists_normal));
					isTypesActive = false;
					topics_btn.setImageDrawable(getResources().getDrawable(R.drawable.browse_normal));
					isAlertActive = false;
					alertButton.setImageDrawable(getResources().getDrawable(R.drawable.camera_normal));
					isMyListsActive = true;
					profileButton.setImageDrawable(getResources().getDrawable(R.drawable.mylist_pressed));

					MyListsFragment myListsFragment = new MyListsFragment();
					FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.dash_container, myListsFragment, "MyListsFragment");
					transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//					transaction.addToBackStack("profile");
					getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
					transaction.commit();

				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		else if((v.getId() == R.id.cameraBtn) || (v.getId() == R.id.cameraEvent))
		{
			if(!isAlertActive)
			{
				try {
					isHomeActive = false;
					homeButton.setImageDrawable(getResources().getDrawable(R.drawable.lists_normal));
					isTypesActive = false;
					topics_btn.setImageDrawable(getResources().getDrawable(R.drawable.browse_normal));
					isAlertActive = true;
					alertButton.setImageDrawable(getResources().getDrawable(R.drawable.camera_pressed));
					isMyListsActive = false;
					profileButton.setImageDrawable(getResources().getDrawable(R.drawable.mylist_normal));

					ShareBroadcast shareBroadcast = new ShareBroadcast();
					FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.dash_container, shareBroadcast, "ShareBroadcast");
					transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//					transaction.addToBackStack("share");
					getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
					transaction.commit();

				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
		}
		else if((v.getId() == R.id.listsBtn) || (v.getId() == R.id.listsEvent))
		{
			if(!isHomeActive)
			{

				try {

					isHomeActive = true;
					homeButton.setImageDrawable(getResources().getDrawable(R.drawable.lists_pressed));
					isTypesActive = false;
					topics_btn.setImageDrawable(getResources().getDrawable(R.drawable.browse_normal));
					isAlertActive = false;
					alertButton.setImageDrawable(getResources().getDrawable(R.drawable.camera_normal));
					isMyListsActive = false;
					profileButton.setImageDrawable(getResources().getDrawable(R.drawable.mylist_normal));

					BroadcastListFragment broadcastListFragment = new BroadcastListFragment();
					FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.dash_container, broadcastListFragment, "BroadcastListFragment");
					transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//					transaction.addToBackStack("posts");
					getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
					transaction.commit();

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}
		
	}
}
