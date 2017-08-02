package com.amrayoub.eventyo;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harrywhewell.scrolldatepicker.DayScrollDatePicker;
import com.harrywhewell.scrolldatepicker.OnDateSelectedListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class calenderFragment extends Fragment {

    private static final String TAG = "CalenderFragment";
    ArrayList<Event_info> myCalenderList = new ArrayList<>();
    TextView textView;
    String eventdate;
    ProgressBar progressBar;
    RecyclerView rv;
    RecyclerView.LayoutManager rvLm;
    MainRecyclerViewAdapter mainRecyclerViewAdapter;
    private DatabaseReference databaseReference;
    DayScrollDatePicker mPicker;

    public calenderFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calender, container, false);
        textView = (TextView) view.findViewById(R.id.calender_no_events);
        progressBar = (ProgressBar) view.findViewById(R.id.calender_progress_bar);
        mPicker = (DayScrollDatePicker) view.findViewById(R.id.date_picker);
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);
        mPicker.setStartDate(day, month, year);
        rv = (RecyclerView) view.findViewById(R.id.calender_recycleriew);
        rvLm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(rvLm);
        mainRecyclerViewAdapter = new MainRecyclerViewAdapter(myCalenderList, getActivity());
        rv.setAdapter(mainRecyclerViewAdapter);
        rv.addOnItemTouchListener(new RecyclerTouchListener(getContext(), rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(getActivity(), position+ " is selected successfully", Toast.LENGTH_SHORT).show();
                //handle click event
                Intent intent = new Intent(getActivity(),EventActivity.class);
                Event_info event = myCalenderList.get(position);
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
    public void onStart() {
        super.onStart();
        mPicker.getSelectedDate(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@Nullable Date date) {
                if (date != null) {
                    DateFormat df = new SimpleDateFormat("d-M-yyyy");
                    eventdate = df.format(date);
                    progressBar.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                    rv.setVisibility(View.GONE);

                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Events");
                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            myCalenderList.clear();
                            mainRecyclerViewAdapter.notifyDataSetChanged();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                for (DataSnapshot eventSnapshot : postSnapshot.getChildren()) {
                                    if (!eventSnapshot.getKey().equals("Null")) {
                                        Event_info event = eventSnapshot.getValue(Event_info.class);
                                        if(event.getmDate().equals(eventdate))
                                            myCalenderList.add(event);
                                    }
                                }
                            mainRecyclerViewAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            if(myCalenderList.size()>0)
                            {
                                rv.setVisibility(View.VISIBLE);
                                textView.setVisibility(View.GONE);
                            }else {
                                textView.setVisibility(View.VISIBLE);
                                rv.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, "onCancelled:" + databaseError.getMessage());
                        }
                    };
                    databaseReference.addListenerForSingleValueEvent(valueEventListener);
                }
            }
        });
    }
}
