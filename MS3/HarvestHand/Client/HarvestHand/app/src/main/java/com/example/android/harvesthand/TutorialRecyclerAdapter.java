package com.example.android.harvesthand;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Pastuh on 13.05.2017.
 */

public class TutorialRecyclerAdapter extends RecyclerView.Adapter<TutorialRecyclerAdapter.EntryViewHolder> {

    ArrayList<Entry> arrayList = new ArrayList<Entry>();

    public TutorialRecyclerAdapter(ArrayList<Entry> arrayList) {

        this.arrayList = arrayList;
    }

    @Override
    public EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tutorial_item, parent, false);
        EntryViewHolder entryViewHolder = new EntryViewHolder(view);
        return entryViewHolder;
    }

    @Override
    public void onBindViewHolder(final EntryViewHolder holder, final int position) {

        //Cardview mit Daten füllen
        holder.entryName.setText(arrayList.get(position).getEntryName());
        holder.entryId.setText(arrayList.get(position).getEntryId());
        holder.clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView entryName, entryId, entryPhValue, entryWater, entryMinerals;
        View clickView;

        public EntryViewHolder(final View itemView) {
            super(itemView);
            clickView = itemView;
            entryName = (TextView) itemView.findViewById(R.id.name);

        }
    }

}