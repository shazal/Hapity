package com.skhalid.hapity.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.skhalid.hapity.DashboardActivity;
import com.skhalid.hapity.GsonRequest;
import com.skhalid.hapity.Jsonexample;
import com.skhalid.hapity.R;
import com.skhalid.hapity.VolleySingleton;

import java.util.HashMap;

import static android.view.View.VISIBLE;

/**
 * Created by skhalid on 5/20/2015.
 */
public class ShareBroadcast extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.share_broadcast, container, false);

        final EditText shareTitle = (EditText) rootView.findViewById(R.id.share_title);
        ImageButton startRecordingBtn = (ImageButton) rootView.findViewById(R.id.startBroadcastBtn);
        CheckBox shareLocation = (CheckBox) rootView.findViewById(R.id.radio_button_share);

        startRecordingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("ShareTitle", shareTitle.getText().toString());
                RecordingFragment recordingFragment = new RecordingFragment();
                recordingFragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.dash_container, recordingFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack("recording");
//                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                transaction.commit();

            }
        });

        shareLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    DashboardActivity.hapityPref.edit().putBoolean("shareLocation",true).commit();
                } else
                    DashboardActivity.hapityPref.edit().putBoolean("shareLocation",false).commit();
            }
        });

        return rootView;

    }

}
