package com.edu.pc.courtadvisor;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class EventFeedFragment extends Fragment implements OnRecyclerViewItemClickListener {

    private DatabaseHandler db;

    protected RecyclerView mRecyclerView;
    protected EventFeedAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<Match> mDataset; // Data to render in the list

    FloatingActionButton fab;

    public EventFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        db = new DatabaseHandler(getContext());

        // TODO: load the dataset for the recyclerView
        initDataset();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_event_feed, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new EventFeedAdapter(mDataset);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchAddMatchActivity();
            }
        });

        return view;
    }

    private void launchAddMatchActivity() {
        Intent intent = new Intent(this.getActivity(), AddMatchActivity.class);
        startActivityForResult(intent, 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Match newMatch = data.getExtras().getParcelable("result");
                db = new DatabaseHandler(getContext());
                db.addMatch(newMatch);
//                mDB.playGroundDao().insertPlayGround(listCourts.get(listCourts.size()-1));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void initDataset() {
        mDataset = db.allMatchs();
    }

    @Override
    public void onRecyclerViewItemClicked(int position, int id) {
            Intent intent = new Intent(getContext(), ShowEventActivity.class);
            intent.putExtra("com.edu.pc.courtadvisor.match", mDataset.get(position));
            getContext().startActivity(intent);
    }
}
