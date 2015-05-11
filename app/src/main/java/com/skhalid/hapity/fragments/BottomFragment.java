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

import com.skhalid.hapity.DashboardActivity;
import com.skhalid.hapity.R;

import static android.view.View.VISIBLE;

public class BottomFragment extends Fragment implements OnClickListener
{
	public static ImageButton topics_btn;
	public static ImageButton profileButton;
	public static ImageButton alertButton;
	public static ImageButton homeButton;
	public static boolean isTypesActive = false;
	public static boolean isProfileActive = false;
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
		topics_btn = (ImageButton) getActivity().findViewById(R.id.topicsBtn);
		profileButton = (ImageButton) getActivity().findViewById(R.id.profileBtn);
		alertButton = (ImageButton) getActivity().findViewById(R.id.alertsBtn);
		homeButton = (ImageButton) getActivity().findViewById(R.id.homeBtn);
		homeLayout = (LinearLayout) getActivity().findViewById(R.id.homeEvent);
		alertLayout = (LinearLayout) getActivity().findViewById(R.id.alertEvent);
		profileLayout = (LinearLayout) getActivity().findViewById(R.id.profileEvent);
		topicLayout = (LinearLayout) getActivity().findViewById(R.id.topicEvent);
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
		 if((v.getId() == R.id.topicEvent) || (v.getId() == R.id.topicsBtn))
		{
			if(!isTypesActive)
			{
				try {
					TestFragment test_fragment = TestFragment.newInstance("Test Fragment");
					FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.dash_container, test_fragment);
					transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.addToBackStack("topics");
                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
					transaction.commit();
					isHomeActive = false;
					homeButton.setImageDrawable(getResources().getDrawable(R.drawable.home_icon_pressed));
					isTypesActive = true;
					topics_btn.setImageDrawable(getResources().getDrawable(R.drawable.topics_icon_normal));
					isAlertActive = false;
					alertButton.setImageDrawable(getResources().getDrawable(R.drawable.alerts_icon_pressed));
					isProfileActive = false;
					profileButton.setImageDrawable(getResources().getDrawable(R.drawable.profile_icon_pressed));
				} catch (Exception e) {
					e.printStackTrace();
				}
//				TopicsFragment topics = new TopicsFragment();
//				FragmentTransaction transaction = getFragmentManager().beginTransaction();
//				transaction.replace(R.id.dash_container, topics);
//				transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//				transaction.addToBackStack("profile");
//				getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//				transaction.commit();
				
				
			}
			
		}
		else if((v.getId() == R.id.profileBtn) || (v.getId() == R.id.profileEvent))
		{
			if(!isProfileActive)
			{
				try {
					TestFragment test_fragment = TestFragment.newInstance("Test Fragment");
					FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.dash_container, test_fragment);
					transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
					transaction.addToBackStack("posts");
					getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
					transaction.commit();
					isHomeActive = false;
					homeButton.setImageDrawable(getResources().getDrawable(R.drawable.home_icon_pressed));
					isTypesActive = false;
					topics_btn.setImageDrawable(getResources().getDrawable(R.drawable.topics_icon_pressed));
					isAlertActive = false;
					alertButton.setImageDrawable(getResources().getDrawable(R.drawable.alerts_icon_pressed));
					isProfileActive = true;
					profileButton.setImageDrawable(getResources().getDrawable(R.drawable.profile_icon_normal));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		else if((v.getId() == R.id.alertsBtn) || (v.getId() == R.id.alertEvent))
		{
			if(!isAlertActive)
			{
				try {
					TestFragment test_fragment = TestFragment.newInstance("Test Fragment");
					FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.dash_container, test_fragment);
					transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
					transaction.addToBackStack("alerts");
					getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
					transaction.commit();
					isHomeActive = false;
					homeButton.setImageDrawable(getResources().getDrawable(R.drawable.home_icon_pressed));
					isTypesActive = false;
					topics_btn.setImageDrawable(getResources().getDrawable(R.drawable.topics_icon_pressed));
					isAlertActive = true;
					alertButton.setImageDrawable(getResources().getDrawable(R.drawable.alerts_icon_normal));
					isProfileActive = false;
					profileButton.setImageDrawable(getResources().getDrawable(R.drawable.profile_icon_pressed));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
		}
		else if((v.getId() == R.id.homeBtn) || (v.getId() == R.id.homeEvent))
		{
			if(!isHomeActive)
			{

				try {
					TestFragment test_fragment = TestFragment.newInstance("Test Fragment");
					FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.dash_container, test_fragment);
					transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
					transaction.addToBackStack("posts");
					getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
					transaction.commit();
					isHomeActive = true;
					homeButton.setImageDrawable(getResources().getDrawable(R.drawable.home_icon_normal));
					isTypesActive = false;
					topics_btn.setImageDrawable(getResources().getDrawable(R.drawable.topics_icon_pressed));
					isAlertActive = false;
					alertButton.setImageDrawable(getResources().getDrawable(R.drawable.alerts_icon_pressed));
					isProfileActive = false;
					profileButton.setImageDrawable(getResources().getDrawable(R.drawable.profile_icon_pressed));
				} catch (Exception e) {
					e.printStackTrace();
				}
//				DreamListFragment dreamListFragment = new DreamListFragment();
//				FragmentTransaction transaction = getFragmentManager().beginTransaction();
//				transaction.replace(R.id.dash_container, dreamListFragment,"DreamList");
//				transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//				transaction.addToBackStack("home");
//				getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//				transaction.commit();
				
			}
			
		}
		
	}
}
