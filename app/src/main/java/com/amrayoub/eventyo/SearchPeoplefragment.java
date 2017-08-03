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

public class SearchPeoplefragment extends Fragment {
    private DatabaseReference mDatabase;
    ArrayList<UserInfo> myUsersList = new ArrayList<>();
    ArrayList<UserInfo> myMiniUsersList = new ArrayList<>();
    TextView textView;
    ProgressBar progressBar;
    RecyclerView rv;
    RecyclerView.LayoutManager rvLm;
    SearchUserRecyclerViewAdapter searchUserRecyclerViewAdapter;

    public SearchPeoplefragment() {
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
        View view = inflater.inflate(R.layout.fragment_search_people, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.search_user_progress_bar);
        textView = (TextView) view.findViewById(R.id.searh_user_no_users);
        rv= (RecyclerView) view.findViewById(R.id.search_user);
        rvLm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(rvLm);
        searchUserRecyclerViewAdapter = new SearchUserRecyclerViewAdapter(myMiniUsersList,getActivity());
        rv.setAdapter(searchUserRecyclerViewAdapter);
        rv.addOnItemTouchListener(new RecyclerTouchListener(getContext(), rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(getActivity(), position+ " is selected successfully", Toast.LENGTH_SHORT).show();
                //handle click event
                Intent intent = new Intent(getActivity(),UserAccountActivity.class);
                UserInfo user = myMiniUsersList.get(position);
                intent.putExtra(getString(R.string.UserObject_Intent_Key),user);
                startActivity(intent);
            }
            @Override
            public void onLongClick(View view, int position) {
            }
        }));
        progressBar.setVisibility(View.VISIBLE);
        mDatabase.child(getString(R.string.Firebase_database_user_path)).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //data will be available on dataSnapshot.getValue();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                            UserInfo user = postSnapshot.getValue(UserInfo.class);
                            myUsersList.add(user);
                        }
                        progressBar.setVisibility(View.GONE);
                        if(myUsersList.size()>0)
                            rv.setVisibility(View.VISIBLE);
                        else
                            textView.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
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
                myMiniUsersList.clear();
                searchUserRecyclerViewAdapter.notifyDataSetChanged();
                for (int i=0;i<myUsersList.size();i++){
                    if(myUsersList.get(i).getmName().contains(query))
                        myMiniUsersList.add(myUsersList.get(i));
                }
                searchUserRecyclerViewAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myMiniUsersList.clear();
                searchUserRecyclerViewAdapter.notifyDataSetChanged();
                for (int i=0;i<myUsersList.size();i++){
                    if(myUsersList.get(i).getmName().contains(newText))
                        myMiniUsersList.add(myUsersList.get(i));
                }
                searchUserRecyclerViewAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }
}
