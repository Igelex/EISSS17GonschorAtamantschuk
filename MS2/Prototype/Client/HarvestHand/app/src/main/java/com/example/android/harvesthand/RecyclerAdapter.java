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

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.EntryViewHolder> {

    ArrayList<Entry> arrayList = new ArrayList<Entry>();
    private final String URL_BASE = "http://192.168.0.19:3001/entries/"; // muss am jeweiligen rechner angepasst werden
    private final String URL_TUTORIAL = "/tutorials/";

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
    public void onBindViewHolder(final EntryViewHolder holder, final int position) {

        //Cardview mit Daten füllen
        holder.entryName.setText(arrayList.get(position).getEntryName());
        holder.entryId.setText(arrayList.get(position).getEntryId());
        holder.entryPhValue.setText(String.valueOf(arrayList.get(position).getEntryPhValue()));
        holder.entryWater.setText(String.valueOf(arrayList.get(position).getEntryWater()));
        holder.entryMinerals.setText(String.valueOf(arrayList.get(position).getEntryMinerals()));
        holder.clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Starte Tutorial Activity und übergebe die URL und weitere Daten
                Intent intent = new Intent(holder.clickView.getContext(), EntryTutorialActivity.class);
                intent.putExtra("URL", URL_BASE + arrayList.get(position).getEntryId() + URL_TUTORIAL
                                + arrayList.get(position).getTutorialId());
                intent.putExtra("ph", arrayList.get(position).getEntryPhValue());
                intent.putExtra("name", arrayList.get(position).getEntryName());
                intent.putExtra("water", arrayList.get(position).getEntryWater());
                intent.putExtra("minerals", arrayList.get(position).getEntryMinerals());
                holder.clickView.getContext().startActivity(intent);

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
            entryId = (TextView) itemView.findViewById(R.id.entry_id);
            entryPhValue = (TextView) itemView.findViewById(R.id.entry_ph);
            entryWater = (TextView) itemView.findViewById(R.id.entry_water);
            entryMinerals = (TextView) itemView.findViewById(R.id.entry_minerals);
        }
    }

}
