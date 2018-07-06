package com.edu.pc.courtadvisor;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatPrivateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatPrivateFragment extends Fragment {

    private static final int DATASET_COUNT = 60;

    protected RecyclerView mRecyclerView;
    protected ChatPrivateAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected String[] mDataset; // Data to render in the list

    public ChatPrivateFragment() {
        // Required empty public constructor
    }


    public static ChatPrivateFragment newInstance() {
        ChatPrivateFragment fragment = new ChatPrivateFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // TODO: load the dataset for the recyclerView
        initDataset();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_chat_private, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ChatPrivateAdapter(mDataset);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private void initDataset() {
        mDataset = new String[DATASET_COUNT];
        for (int i = 0; i < DATASET_COUNT; i++) {
            mDataset[i] = "This is private chat #" + i;
        }
    }

}
