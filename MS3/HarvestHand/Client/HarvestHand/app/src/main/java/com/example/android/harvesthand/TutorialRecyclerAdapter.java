package com.example.android.harvesthand;

import android.content.Context;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.mikhaellopez.hfrecyclerview.HFRecyclerView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Code von https://github.com/lopspower/HFRecyclerView
 */
public class TutorialRecyclerAdapter extends HFRecyclerView<Tutorial> {

    ArrayList<Tutorial> arrayList = new ArrayList<>();
    Context context;
    TextToSpeech speaker;

    public TutorialRecyclerAdapter(ArrayList<Tutorial> list) {
        //With Footer
        super(list, false, true);
        this.arrayList = list;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            //Image und jeweilige Description werden gesetzt
            itemViewHolder.img.setImageResource(arrayList.get(position).getmImageId());
            itemViewHolder.descriptionEarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Description zum Image wird Vorgelesen
                    //Speed
                    speaker.setSpeechRate((float) 0.8);
                    speaker.speak(arrayList.get(position).getmDescription(), TextToSpeech.QUEUE_FLUSH, null);
                }
            });
        } else if (holder instanceof HeaderViewHolder) {

        } else if (holder instanceof ItemVideoViewHolder) {
            final ItemVideoViewHolder itemViewHolder = (ItemVideoViewHolder) holder;
            //Image und jeweilige Description werden gesetzt

            itemViewHolder.video.setVideoURI(Uri.parse("android.resource://com.example.android.harvesthand/" + R.raw.tutorial_general_coffe_1));
            itemViewHolder.descriptionEyeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Description zum Image wird Vorgelesen
                    //Speed
                    if(itemViewHolder.video.isPlaying()) {
                        itemViewHolder.video.pause();
                    } else {
                        itemViewHolder.video.start();
                    }
                }
            });
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            try {
                if (position == arrayList.size()) {
                    // Norm-Value der Eigenschaft wird Vorgelesen
                    if (arrayList.get(0).getmNorm() == null) {
                        footerViewHolder.container.setVisibility(View.GONE);
                    } else {
                        footerViewHolder.normText.setText(arrayList.get(0).getmNorm());
                        footerViewHolder.normEar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                speaker.speak(context.getString(R.string.show_should_be_value) +
                                        arrayList.get(0).getmNorm(), TextToSpeech.QUEUE_FLUSH, null);
                            }
                        });
                    }

                }
            } catch (IndexOutOfBoundsException e) {
                Log.e("Tutorial list", "" + e);
            }

        }
    }

    //region Override Get ViewHolder
    @Override
    protected RecyclerView.ViewHolder getItemView(LayoutInflater inflater, ViewGroup parent) {
        context = parent.getContext();
        setTextToSpeech();
        if(!this.arrayList.get(parent.getChildCount()).isVideo())
            return new ItemViewHolder(inflater.inflate(R.layout.tutorial_item, parent, false));
        else
            return new ItemVideoViewHolder(inflater.inflate(R.layout.tutorial_video_item, parent, false));
    }

    @Override
    protected RecyclerView.ViewHolder getHeaderView(LayoutInflater inflater, ViewGroup parent) {
        return new HeaderViewHolder(inflater.inflate(R.layout.recycler_footer, parent, false));
    }

    @Override
    protected RecyclerView.ViewHolder getFooterView(LayoutInflater inflater, ViewGroup parent) {
        return new FooterViewHolder(inflater.inflate(R.layout.recycler_footer, parent, false));
    }
    //endregion

    //region ViewHolder Header and Footer
    class ItemVideoViewHolder extends RecyclerView.ViewHolder {
        VideoView video;
        ImageButton descriptionEyeButton;

        public ItemVideoViewHolder(View itemView) {
            super(itemView);
            video = itemView.findViewById(R.id.show_item_video);
            descriptionEyeButton = itemView.findViewById(R.id.footer_item_eye);
        }


    }

    //region ViewHolder Header and Footer
    class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        ImageButton descriptionEarButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.show_item_image);
            descriptionEarButton = itemView.findViewById(R.id.footer_item_ear);

        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView normText;
        ImageButton normEar;
        LinearLayout container;

        public FooterViewHolder(View itemView) {
            super(itemView);
            normText = itemView.findViewById(R.id.footer_norm_value);
            normEar = itemView.findViewById(R.id.footer_norm_value_ear);
            container = itemView.findViewById(R.id.footer_container);
        }
    }
    //endregion

    public void setTextToSpeech() {
        speaker = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    speaker.setLanguage(Locale.getDefault());
                }
            }
        });
    }
}