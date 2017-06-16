package com.example.android.harvesthand;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.example.android.harvesthand.Contracts.*;


/**
 * Created by Pastuh on 07.06.2017.
 */

public class ListAdapter extends ArrayAdapter<Entry> {

    private Context mContext;
    private ImageButton earButton, menuButton;
    private TextToSpeech speaker;
    private int userType;

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

        SharedPreferences sPref = mContext.getSharedPreferences(USER_SHARED_PREFS, MODE_PRIVATE);
        if(sPref != null) {
            userType = sPref.getInt(USER_SP_TYPE, -1);
        }

        Entry currentEntry = getItem(position);

        final TextView entryName = listView.findViewById(R.id.name);
        final TextView entryArea = listView.findViewById(R.id.surface);
        final TextView entryLocation = listView.findViewById(R.id.location);
        final String entryId = currentEntry.getEntryId();

        entryName.setText(currentEntry.getEntryName());
        entryLocation.setText(currentEntry.getLocation());
        entryArea.setText(String.valueOf(currentEntry.getArea()));

        if (userType == 1) {
            speaker = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        speaker.setLanguage(Locale.getDefault());
                        Log.i("local: ", Locale.getDefault().toString());
                    }
                }
            });
            earButton = listView.findViewById(R.id.item_speak_icon);
            earButton.setVisibility(View.VISIBLE);
            earButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    speak(mContext.getString(R.string.item_speaktext_name) +entryName.getText().toString()
                            + mContext.getString(R.string.item_speaktext_coma) +
                            mContext.getString(R.string.item_speaktext_location) + entryLocation.getText().toString()
                            + mContext.getString(R.string.item_speaktext_coma)+
                            mContext.getString(R.string.item_speaktext_area) + entryArea.getText().toString());

                    Toast.makeText(mContext,"ID: " + entryId, Toast.LENGTH_LONG).show();

                }
            });
        } else {
            menuButton = listView.findViewById(R.id.item_menu_button);
            menuButton.setVisibility(View.VISIBLE);
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(getContext(), menuButton);
                    popupMenu.inflate(R.menu.item_menu);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            switch (menuItem.getItemId()) {
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
        }
        return listView;
    }

    private void speak(String textToSpeech){
        speaker.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH,null);
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
