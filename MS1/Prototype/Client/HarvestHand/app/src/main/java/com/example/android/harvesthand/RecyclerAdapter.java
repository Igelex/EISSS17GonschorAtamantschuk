package com.example.android.harvesthand;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Pastuh on 13.05.2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.EntryViewHolder> {

    ArrayList<Entry> arrayList = new ArrayList<Entry>();

    public RecyclerAdapter(ArrayList<Entry> arrayList) {

        this.arrayList = arrayList;
    }

    @Override
    public EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_view, parent, false);
        EntryViewHolder entryViewHolder = new EntryViewHolder(view);
        return entryViewHolder;
    }

    @Override
    public void onBindViewHolder(EntryViewHolder holder, int position) {
        holder.entryName.setText(arrayList.get(position).getEntryName());
        holder.entryId.setText(arrayList.get(position).getEntryId().toString());
        //holder.entryPhValue.setText(arrayList.get(position).getEntryPhValue());
        holder.entryWater.setText(arrayList.get(position).getEntryWater());
        holder.entryMinerals.setText(arrayList.get(position).getEntryMinerals());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class EntryViewHolder extends RecyclerView.ViewHolder{
        TextView entryName, entryId, entryPhValue, entryWater, entryMinerals;

        public EntryViewHolder(View itemView) {
            super(itemView);
            entryName = (TextView) itemView.findViewById(R.id.name);
            entryId = (TextView) itemView.findViewById(R.id.entry_id);
            entryPhValue = (TextView) itemView.findViewById(R.id.entry_ph);
            entryWater = (TextView) itemView.findViewById(R.id.entry_water);
            entryMinerals = (TextView) itemView.findViewById(R.id.entry_minerals);
        }
    }
}
