package com.example.android.harvesthand;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Pastuh on 07.06.2017.
 */

public class ListAdapter extends ArrayAdapter<Entry> {

    private TextView entryName, entryLocation, entrySurface;

    public ListAdapter(@NonNull Context context, @NonNull ArrayList entries) {
        super(context, 0, entries);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;

        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,
                    parent, false);
        }

        Entry currentEntry = getItem(position);

        entryName = (TextView) listView.findViewById(R.id.name);
        entrySurface = (TextView) listView.findViewById(R.id.surface);
        entryLocation = (TextView) listView.findViewById(R.id.location);

        entryName.setText(currentEntry.getEntryName().toString());
        entryLocation.setText("" + currentEntry.getEntryPhValue());
        entrySurface.setText("" + currentEntry.getEntryMinerals() + " ha");


        return listView;
    }
}
