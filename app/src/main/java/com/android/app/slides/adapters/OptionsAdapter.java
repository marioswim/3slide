package com.android.app.slides.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.app.slides.R;
import java.util.List;

/**
 * Created by francisco on 28/6/15.
 */
public class OptionsAdapter extends RecyclerView.Adapter{

    public List<Integer> images;
    public List<String> titles;
    OnItemClickListener mItemClickListener;

    public OptionsAdapter(List<Integer> images, List<String> titles) {

        this.images = images;
        this.titles = titles;
    }

    public void add(int position, Integer image, String title) {
        images.add(position, image);
        titles.add(position, title);
        notifyItemInserted(position);
    }

    public void remove(Integer image, String title){
        int positionTitle = titles.indexOf(title);
        int positionImage = images.indexOf(image);
        titles.remove(positionTitle);
        images.remove(positionImage);
        notifyItemRemoved(positionTitle);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.optionsmenu_selector, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof ViewHolder){
            ViewHolder viewHolder = (ViewHolder)holder;
            viewHolder.TareaName.setText(titles.get(position));
            viewHolder.TareaIcon.setImageResource(images.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView TareaIcon;
        TextView TareaName;

        public ViewHolder(View itemView) {
            super(itemView);

            TareaName = (TextView) itemView.findViewById(R.id.TareaName);
            TareaIcon = (ImageView) itemView.findViewById(R.id.TareaIcon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mItemClickListener != null){
                mItemClickListener.onItemClick(v, getPosition());
            }
        }
    }
}
