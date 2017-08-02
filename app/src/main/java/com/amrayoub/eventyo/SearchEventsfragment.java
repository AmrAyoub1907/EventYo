package com.amrayoub.eventyo;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SearchEventsfragment extends Fragment {
    private DatabaseReference mDatabase;
    ArrayList<Event_info> myEventsList = new ArrayList<>();
    ArrayList<Event_info> myMiniEventsList = new ArrayList<>();
    TextView textView;
    ProgressBar progressBar;
    RecyclerView rv;
    RecyclerView.LayoutManager rvLm;
    SearchEventRecyclerViewAdapter searchEventRecyclerViewAdapter;
    public SearchEventsfragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setHasOptionsMenu(true);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_events, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.search_event_progress_bar);
        textView = (TextView) view.findViewById(R.id.searh_event_no_events);
        rv= (RecyclerView) view.findViewById(R.id.search_event);
        rvLm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(rvLm);
        searchEventRecyclerViewAdapter = new SearchEventRecyclerViewAdapter(myMiniEventsList,getActivity());
        rv.setAdapter(searchEventRecyclerViewAdapter);
        rv.addOnItemTouchListener(new RecyclerTouchListener(getContext(), rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(getActivity(), position+ " is selected successfully", Toast.LENGTH_SHORT).show();
                //handle click event
                Intent intent = new Intent(getActivity(),EventActivity.class);
                Event_info event = myMiniEventsList.get(position);
                intent.putExtra("EventObject",event);
                startActivity(intent);
            }
            @Override
            public void onLongClick(View view, int position) {
            }
        }));
        progressBar.setVisibility(View.VISIBLE);
        mDatabase.child("Events").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //data will be available on dataSnapshot.getValue();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                            for (DataSnapshot childpostSnapshot: postSnapshot.getChildren()){
                                if(!childpostSnapshot.getKey().equals("Null")){
                                    Event_info event = childpostSnapshot.getValue(Event_info.class);
                                    myEventsList.add(event);
                                }
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                        if(myEventsList.size()>0)
                            rv.setVisibility(View.VISIBLE);
                        else
                            textView.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("SearchEventsFragment", "getUser:onCancelled", databaseError.toException());
                    }
                });
        return view;
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                myMiniEventsList.clear();
                searchEventRecyclerViewAdapter.notifyDataSetChanged();
                for (int i=0;i<myEventsList.size();i++){
                    if(myEventsList.get(i).getmTitle().contains(query))
                        myMiniEventsList.add(myEventsList.get(i));
                }
                searchEventRecyclerViewAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myMiniEventsList.clear();
                searchEventRecyclerViewAdapter.notifyDataSetChanged();
                for (int i=0;i<myEventsList.size();i++){
                    if(myEventsList.get(i).getmTitle().contains(newText))
                        myMiniEventsList.add(myEventsList.get(i));
                }
                searchEventRecyclerViewAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

}
