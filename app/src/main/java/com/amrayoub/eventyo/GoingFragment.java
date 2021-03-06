package com.amrayoub.eventyo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class GoingFragment extends Fragment {
    ArrayList<EventInfo> myGoingList = new ArrayList<>();
    RecyclerView rv;
    RecyclerView.LayoutManager rvLm;
    MainRecyclerViewAdapter mainRecyclerViewAdapter;
    public GoingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_going, container, false);
        rv = (RecyclerView) view.findViewById(R.id.going_recyclerview);
        return view;
    }
    @Override
    public void onStart(){
        super.onStart();
        DatabaseHandler db = new DatabaseHandler(getContext());
        myGoingList = db.getAllEvents();
        rvLm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(rvLm);
        mainRecyclerViewAdapter = new MainRecyclerViewAdapter(myGoingList, getActivity());
        rv.setAdapter(mainRecyclerViewAdapter);
        mainRecyclerViewAdapter.notifyDataSetChanged();
        rv.addOnItemTouchListener(new RecyclerTouchListener(getContext(), rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(getActivity(), position+ " is selected successfully", Toast.LENGTH_SHORT).show();
                //handle click event
                Intent intent = new Intent(getActivity(),EventActivity.class);
                EventInfo event = myGoingList.get(position);
                intent.putExtra(getString(R.string.EventObject_Intent_Key),event);
                intent.putExtra(getString(R.string.Going_Tab_Identifier_Key),true);
                startActivity(intent);
            }
            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

}
