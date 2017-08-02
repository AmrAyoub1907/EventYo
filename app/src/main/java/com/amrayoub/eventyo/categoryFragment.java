package com.amrayoub.eventyo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class categoryFragment extends Fragment {
    private static final String TAG = "categoryFragment";
    ArrayList<Event_info> myCategoryList=new ArrayList<>();
    Spinner mSpinner;
    TextView textView;
    ProgressBar progressBar;
    String mCategory;
    RecyclerView rv;
    RecyclerView.LayoutManager rvLm;
    MainRecyclerViewAdapter mainRecyclerViewAdapter;
    private DatabaseReference databaseReference;


    public categoryFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference =  FirebaseDatabase.getInstance().getReference();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        textView = (TextView) view.findViewById(R.id.no_events);
        mSpinner = (Spinner) view.findViewById(R.id.category_spinner);
        rv = (RecyclerView) view.findViewById(R.id.category_recyclerview);
        rvLm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(rvLm);
        mainRecyclerViewAdapter = new MainRecyclerViewAdapter(myCategoryList,getActivity());
        rv.setAdapter(mainRecyclerViewAdapter);
        rv.addOnItemTouchListener(new RecyclerTouchListener(getContext(), rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(getActivity(), position+ " is selected successfully", Toast.LENGTH_SHORT).show();
                //handle click event
                Intent intent = new Intent(getActivity(),EventActivity.class);
                Event_info event = myCategoryList.get(position);
                intent.putExtra("EventObject",event);
                startActivity(intent);
            }
            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        return view;
    }
    @Override
    public void onStart(){
        super.onStart();
        Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH)+1;
        final int mDay = c.get(Calendar.DAY_OF_MONTH);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.Category, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, final View view, int position, long id) {
                progressBar.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                rv.setVisibility(View.GONE);
                myCategoryList.clear();
                mCategory = parent.getSelectedItem().toString();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Events").child(mCategory);
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        myCategoryList.clear();
                        mainRecyclerViewAdapter.notifyDataSetChanged();

                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                            if(!postSnapshot.getKey().equals("Null")){
                                Event_info event = postSnapshot.getValue(Event_info.class);
                                int day,month,year;
                                String[] separated = event.getmDate().split("-");
                                day= Integer.parseInt(separated[0]);
                                month= Integer.parseInt(separated[1]);
                                year= Integer.parseInt(separated[2]);
                                if(day>=mDay && month>=mMonth && year>=mYear)
                                    myCategoryList.add(event);
                            }
                        }
                        mainRecyclerViewAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        if(myCategoryList.size()>0)
                        {
                            rv.setVisibility(View.VISIBLE);
                            textView.setVisibility(View.GONE);
                        }else {
                            textView.setVisibility(View.VISIBLE);
                            rv.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError){
                        Log.d(TAG, "onCancelled:" + databaseError.getMessage());
                    }
                };
                databaseReference.addListenerForSingleValueEvent(valueEventListener);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getActivity(), "Nothing Selected", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
