package com.amrayoub.eventyo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Amr Ayoub on 8/2/2017.
 */

public class SearchEventRecyclerViewAdapter extends RecyclerView.Adapter<SearchEventRecyclerViewAdapter.EventsViewHolder> {
    ArrayList<EventInfo> myEvents;
    Context mContext;

    SearchEventRecyclerViewAdapter(ArrayList<EventInfo> myEvents, Context mContext){
        this.myEvents = myEvents;
        this.mContext=mContext;
    }
    class EventsViewHolder extends RecyclerView.ViewHolder {
        TextView title,date;
        ImageView photo;

        public EventsViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.search_date);
            title = (TextView) itemView.findViewById(R.id.search_title);
            photo = (ImageView) itemView.findViewById(R.id.search_photo);
        }
    }
    @Override
    public SearchEventRecyclerViewAdapter.EventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_rv_item, parent, false);
        return  new SearchEventRecyclerViewAdapter.EventsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventsViewHolder holder, int position) {
        holder.date.setText(myEvents.get(position).getmDate());
        holder.title.setText(myEvents.get(position).getmTitle());
        Picasso.with(mContext).load(myEvents.get(position).getmPhotoUrl())
                .placeholder(R.drawable.event_pic)
                .error(R.drawable.event_pic)
                .into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return myEvents.size();
    }
}
