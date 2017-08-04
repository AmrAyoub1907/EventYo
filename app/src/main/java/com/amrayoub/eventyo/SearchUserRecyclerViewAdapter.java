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

public class SearchUserRecyclerViewAdapter extends RecyclerView.Adapter<SearchUserRecyclerViewAdapter.EventsViewHolder> {
    ArrayList<UserInfo> myUsers;
    Context mContext;

    SearchUserRecyclerViewAdapter(ArrayList<UserInfo> myUsers, Context mContext){
        this.myUsers = myUsers;
        this.mContext=mContext;
    }
    class EventsViewHolder extends RecyclerView.ViewHolder {
        TextView title,email;
        ImageView photo;

        public EventsViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.search_title);
            email = (TextView) itemView.findViewById(R.id.search_date);
            photo = (ImageView) itemView.findViewById(R.id.search_photo);
        }
    }
    @Override
    public SearchUserRecyclerViewAdapter.EventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_user_rv_item, parent, false);
        return  new SearchUserRecyclerViewAdapter.EventsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchUserRecyclerViewAdapter.EventsViewHolder holder, int position) {
        holder.email.setText(myUsers.get(position).getmEmail());
        holder.title.setText(myUsers.get(position).getmName());
        if(myUsers.get(position).getmPhoto().equals(""))
            myUsers.get(position).mPhoto="//";
        Picasso.with(mContext).load(myUsers.get(position).getmPhoto())
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return myUsers.size();
    }
}
