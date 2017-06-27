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
 * Created by Pastuh on 07.06.2017.
 */

public class CollabListAdapter extends ArrayAdapter <String> {

    private Context mContext;

    public CollabListAdapter(@NonNull Context context, @NonNull ArrayList collaborators) {
        super(context, 0, collaborators);
        this.mContext = context;

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

        ImageButton removeCollaborator = listView.findViewById(R.id.list_item_remove_collab);
        removeCollaborator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        return listView;
    }
}
