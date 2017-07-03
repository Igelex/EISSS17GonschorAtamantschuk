package com.example.android.harvesthand;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * ArrayAdapter für Liste der Collaborators, füllt einzelne Listeneinträge mit den Daten
 */

public class CollabListAdapter extends ArrayAdapter <String> {

    public CollabListAdapter(@NonNull Context context, @NonNull ArrayList <String> collaborators) {
        super(context, 0, collaborators);

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;

        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.collab_list_item,
                    parent, false);
        }

        String currentCollaborator = getItem(position);

        TextView collaborator = listView.findViewById(R.id.list_item_collab);
        collaborator.setText(currentCollaborator);

        return listView;
    }
}
