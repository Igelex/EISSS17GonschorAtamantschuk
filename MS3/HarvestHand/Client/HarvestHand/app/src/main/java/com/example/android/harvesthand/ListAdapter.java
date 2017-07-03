package com.example.android.harvesthand;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.example.android.harvesthand.Contracts.*;


/**
 * Created by Pastuh on 07.06.2017.
 */

public class ListAdapter extends ArrayAdapter<Entry> {

    private Context mContext;
    private TextToSpeech speaker;
    private int userType;
    private CircleImageView cropImg;
    private Contracts contracts = new Contracts(null);
    private View listView;

    public ListAdapter(@NonNull Context context, @NonNull ArrayList entries) {
        super(context, 0, entries);
        this.mContext = context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        listView = convertView;

        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,
                    parent, false);
        }

        SharedPreferences sPref = mContext.getSharedPreferences(USER_SHARED_PREFS, MODE_PRIVATE);
        if (sPref != null) {
            userType = sPref.getInt(USER_SHARED_PREFS_TYPE, -1);
        }

        final Entry currentEntry = getItem(position);
        cropImg = listView.findViewById(R.id.item_image);
        setCropBackgroundImg(currentEntry.getCropId());

        final TextView entryName = listView.findViewById(R.id.name);
        final TextView entryArea = listView.findViewById(R.id.area);
        final TextView entryLocation = listView.findViewById(R.id.location);
        final String entryId = currentEntry.getEntryId();

        //TextViews in der Liste mit Inhalt füllen
        entryName.setText(currentEntry.getEntryName());
        entryLocation.setText(currentEntry.getLocation());
        entryArea.setText(String.valueOf(currentEntry.getArea()));

        if (userType == 1) {
            speaker = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        speaker.setLanguage(Locale.getDefault());
                    }
                }
            });
            final ImageButton earButton = listView.findViewById(R.id.item_speak_icon);
            earButton.setVisibility(View.VISIBLE);
            earButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    speak(mContext.getString(R.string.item_speaktext_name) + entryName.getText().toString()
                            + mContext.getString(R.string.item_speaktext_comma) +
                            mContext.getString(R.string.item_speaktext_location) + entryLocation.getText().toString()
                            + mContext.getString(R.string.item_speaktext_comma) +
                            mContext.getString(R.string.item_speaktext_area) + entryArea.getText().toString()
                            + mContext.getString(R.string.add_entry_m_squared));
                }
            });
        } else {
            final ImageButton menuButton = listView.findViewById(R.id.item_menu_button);
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
                                    showDeleteConfirmationDialog(listView, currentEntry.getEntryId());
                                    break;
                                case R.id.action_item_edit:
                                    onEditMenuClick(entryId);
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

    private void speak(String textToSpeech) {
        speaker.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void showDeleteConfirmationDialog(final View conainer, final String currentId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                final SendRequest request = new SendRequest();
                request.requestData(getContext(), Request.Method.DELETE, null, conainer, BASE_URL + URL_BASE_ENTRIES + currentId,
                        null, new AddNewEntry.ServerCallback() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try {
                                    boolean deleted = result.getBoolean("res");
                                    if (deleted) {
                                        contracts.showSnackbar(conainer, mContext.getString(R.string.msg_entry_deleted),
                                                false, true);
                                    } else {
                                        contracts.showSnackbar(conainer, mContext.getString(R.string.msg_entry_not_found),
                                                true, false);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();

                                }
                            }
                        });
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

    private void setCropBackgroundImg(int cropID) {
        switch (cropID) {
            case 1:
                cropImg.setImageResource(R.drawable.crop_0_caffe_img);
                break;

            case 2:
                cropImg.setImageResource(R.drawable.crop_1_cacao_img);
                break;
            case 3:
                cropImg.setImageResource(R.drawable.crop_2_banane_img);
                break;
            /*...*/
        }
    }

    private void onEditMenuClick(String entryId) {
        Intent intent = new Intent(getContext(), AddNewEntry.class);
        intent.putExtra("method", Request.Method.PUT);
        intent.putExtra("entry_id", entryId);
        getContext().startActivity(intent);
    }
}
