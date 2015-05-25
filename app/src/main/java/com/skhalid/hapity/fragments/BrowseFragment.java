package com.skhalid.hapity.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skhalid.hapity.BroadcastListAdapter;
import com.skhalid.hapity.BroadcastModal;
import com.skhalid.hapity.R;

import java.util.ArrayList;

/**
 * Created by skhalid on 5/25/2015.
 */
public class BrowseFragment extends Fragment {

    ListView BroadcastList;
    BroadcastListAdapter adapter;
    public ArrayList<BroadcastModal> broadcastArray = new ArrayList<BroadcastModal>();
    private TextView searchEditText;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.browse_broadcast_list_fragment_layout, container,false);
        BroadcastList = (ListView) rootView.findViewById(R.id.broadcastList);
        searchEditText = (EditText) rootView.findViewById(R.id.searchEditText);

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
        adapter = new BroadcastListAdapter(getActivity(), broadcastArray, getActivity().getResources());

        if(BottomFragment.isTypesActive) {
            broadcastArray.clear();
            setListData();
            BroadcastList.setAdapter(adapter);
        }

        return rootView;
    }

    private void performSearch() {

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
