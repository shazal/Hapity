package com.skhalid.hapity.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.skhalid.hapity.BroadcastListAdapter;
import com.skhalid.hapity.BroadcastModal;
import com.skhalid.hapity.R;

import java.util.ArrayList;

/**
 * Created by skhalid on 5/25/2015.
 */
public class MyListsFragment extends Fragment {

    ListView BroadcastList;
    BroadcastListAdapter adapter;
    public ArrayList<BroadcastModal> broadcastArray = new ArrayList<BroadcastModal>();
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.broadcast_list_fragment_layout, container,false);
        BroadcastList = (ListView) rootView.findViewById(R.id.broadcastList);

        adapter = new BroadcastListAdapter(getActivity(), broadcastArray, getActivity().getResources());

        if(BottomFragment.isMyListsActive) {
            broadcastArray.clear();
            setListData();
            BroadcastList.setAdapter(adapter);
        }

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private void setListData() {
        // TODO Auto-generated method stub

        for (int i = 1; i<=10; i++)
        {
            final BroadcastModal sched = new BroadcastModal();
            sched.setUserName("John Doe");
            broadcastArray.add(sched);
        }
        Toast.makeText(getActivity(), broadcastArray.size() + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }
}
