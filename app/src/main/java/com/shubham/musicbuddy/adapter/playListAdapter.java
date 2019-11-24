package com.shubham.musicbuddy.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.shubham.musicbuddy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class playListAdapter extends RecyclerView.Adapter<playListAdapter.viewHolder> {

    public List<String> songName;
    public List<String> songUrl;
    public List<String> artistsName;
    public List<String> coverImage;
    Context context;
    SongItemClickListner songItemClickListner;

    public playListAdapter(List<String> songName, List<String> songUrl, List<String> artistsName, List<String> coverImage, Context context, SongItemClickListner songItemClickListner){
        this.songName = songName;
        this.songUrl = songUrl;
        this.artistsName = artistsName;
        this.coverImage = coverImage;
        this.context = context;
        this.songItemClickListner = songItemClickListner;
    }

    public interface SongItemClickListner {
        void onTapToPlay(View view);
    }
    @NonNull
    @Override
    public playListAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.song_item_card, parent,false);
        return new playListAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull playListAdapter.viewHolder holder, int position) {
        holder.songName.setText(this.songName.get(position));
        holder.artistsName.setText(this.artistsName.get(position));
        holder.songUrl.setText(this.songUrl.get(position));
        Picasso.get().load(this.coverImage.get(position)).into(holder.cover_Image);

    }

    @Override
    public int getItemCount() {
        return this.songName.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView songName;
        TextView artistsName;
        TextView songUrl;
        ImageView cover_Image;
        CardView cardView;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.song_name);
            artistsName = itemView.findViewById(R.id.artists_name);
            cover_Image = itemView.findViewById(R.id.songCoverImage);
            songUrl = itemView.findViewById(R.id.songUrl);
            cardView = itemView.findViewById(R.id.cv);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    songItemClickListner.onTapToPlay(v);
                }
            });

        }
    }
}
