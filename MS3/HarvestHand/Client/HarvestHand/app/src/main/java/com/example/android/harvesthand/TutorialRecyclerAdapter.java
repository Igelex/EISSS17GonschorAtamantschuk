package com.example.android.harvesthand;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Pastuh on 13.05.2017.
 */

public class TutorialRecyclerAdapter extends RecyclerView.Adapter<TutorialRecyclerAdapter.TutorialViewHolder> {

    ArrayList<Tutorial> arrayList = new ArrayList<>();

    public TutorialRecyclerAdapter(ArrayList<Tutorial> arrayList) {

        this.arrayList = arrayList;
    }

    @Override
    public TutorialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tutorial_item, parent, false);
        TutorialViewHolder tutorialViewHolder = new TutorialViewHolder(view);
        return tutorialViewHolder;
    }

    @Override
    public void onBindViewHolder(final TutorialViewHolder holder, final int position) {

        //Cardview mit Daten füllen
        holder.img.setImageResource(arrayList.get(position).getmImageId());
        /*holder.clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class TutorialViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public TutorialViewHolder(final View itemView) {
            super(itemView);
             img = itemView.findViewById(R.id.show_item_image);

        }
    }

}