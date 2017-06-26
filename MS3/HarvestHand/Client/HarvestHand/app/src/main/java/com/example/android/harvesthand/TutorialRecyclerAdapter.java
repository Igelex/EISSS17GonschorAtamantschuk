package com.example.android.harvesthand;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.hfrecyclerview.HFRecyclerView;

import java.util.ArrayList;
/**
 * Code von https://github.com/lopspower/HFRecyclerView
 */
public class TutorialRecyclerAdapter extends HFRecyclerView <Tutorial> {

    ArrayList<Tutorial> arrayList = new ArrayList<>();

    public TutorialRecyclerAdapter(ArrayList<Tutorial> list) {
        //With Footer
        super(list, false, true);
        this.arrayList = list;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.img.setImageResource(arrayList.get(position).getmImageId());
        } else if (holder instanceof HeaderViewHolder) {

        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.normText.setText("60-80");
        }
    }

    //region Override Get ViewHolder
    @Override
    protected RecyclerView.ViewHolder getItemView(LayoutInflater inflater, ViewGroup parent) {
        return new ItemViewHolder(inflater.inflate(R.layout.tutorial_item, parent, false));
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
    class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ItemViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.show_item_image);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView normText;
        public FooterViewHolder(View itemView) {
            super(itemView);
            normText = itemView.findViewById(R.id.footer_norm_value);
        }
    }
    //endregion
}