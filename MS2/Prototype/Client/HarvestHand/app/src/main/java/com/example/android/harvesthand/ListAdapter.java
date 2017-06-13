package com.example.android.harvesthand;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by Pastuh on 07.06.2017.
 */

public class ListAdapter extends ArrayAdapter<Entry> {

    private Context mContext;
    private TextView entryName, entryLocation, entrySurface;

    public ListAdapter(@NonNull Context context, @NonNull ArrayList entries) {
        super(context, 0, entries);
        this.mContext = context;

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

        entryName.setText(currentEntry.getEntryName());
        entryLocation.setText("" + currentEntry.getEntryPhValue());
        entrySurface.setText(currentEntry.getEntryMinerals() + " ha");

        final ImageButton menuButton = (ImageButton) listView.findViewById(R.id.item_menu_button_kek);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu  = new PopupMenu(getContext(),menuButton);
                popupMenu.inflate(R.menu.item_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()){
                            case R.id.action_item_delete:
                                showDeleteConfirmationDialog();
                                break;
                            case R.id.action_item_edit:
                                Toast.makeText(mContext, "EDIT", Toast.LENGTH_LONG).show();
                                break;
                        }

                        return true;
                    }
                });

                popupMenu.show();

            }
        });


        return listView;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(mContext, "DELETE", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
