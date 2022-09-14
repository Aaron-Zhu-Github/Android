package com.picture.logic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picture.R;
import com.picture.entity.Album;
import com.picture.util.ImageUtil;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_EMPTY = 1000;
    private Context context;
    private List<Album> albums;
    private OnSearchListener listener;

    public SearchAdapter(Context context, List<Album> albums, OnSearchListener listener) {
        this.context = context;
        this.albums = albums;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_search_item, parent, false);
        SearchViewHolder viewHolder = new SearchViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof SearchViewHolder) {
            SearchViewHolder holder = (SearchViewHolder) vh;
            Album album = albums.get(holder.getAdapterPosition());
            ImageUtil.loadImgByUrl(holder.ivPhoto, album.getPath());

            View.OnClickListener clickListener = v -> listener.callbackSearch(v.getId(), position);
            holder.ivPhoto.setOnClickListener(clickListener);
        }
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
        }
    }

    public interface OnSearchListener {
        void callbackSearch(int id, int position);
    }
}
