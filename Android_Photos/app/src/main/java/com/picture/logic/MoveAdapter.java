package com.picture.logic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picture.R;
import com.picture.entity.Album;

import java.util.List;

public class MoveAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_EMPTY = 1000;
    private Context context;
    private List<Album> albums;
    private OnMoveListener listener;

    public MoveAdapter(Context context, List<Album> albums, OnMoveListener listener) {
        this.context = context;
        this.albums = albums;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EMPTY) {
            View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item_empty, parent, false);
            return new RecyclerView.ViewHolder(emptyView) {
            };
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_move_item, parent, false);
        MoveViewHolder viewHolder = new MoveViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof MoveViewHolder) {
            MoveViewHolder holder = (MoveViewHolder) vh;
            Album album = albums.get(holder.getAdapterPosition());
            holder.tvAlbumName.setText(album.getFileName());

            View.OnClickListener clickListener = v -> listener.callbackMove(v.getId(), position);
            holder.tvAlbumName.setOnClickListener(clickListener);
        }
    }

    @Override
    public int getItemCount() {
        if (albums.size() == 0) {
            return 1;
        }
        return albums.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (albums.size() == 0) {
            return VIEW_TYPE_EMPTY;
        }
        return super.getItemViewType(position);
    }

    public class MoveViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAlbumName;

        public MoveViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAlbumName = itemView.findViewById(R.id.tvAlbumName);
        }
    }

    public interface OnMoveListener {
        void callbackMove(int id, int position);
    }
}
