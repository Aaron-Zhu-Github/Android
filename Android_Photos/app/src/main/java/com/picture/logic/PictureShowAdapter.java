package com.picture.logic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picture.R;
import com.picture.entity.Album;
import com.picture.util.ImageUtil;

import java.util.List;

public class PictureShowAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Album> albums;
    private OnPictureListener listener;
    private boolean checkMode;

    public PictureShowAdapter(Context context, List<Album> albums, OnPictureListener listener) {
        this.context = context;
        this.albums = albums;
        this.listener = listener;
    }

    public void setCheckMode(boolean mode) {
        this.checkMode = mode;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_picture_item, parent, false);
        PictureViewHolder viewHolder = new PictureViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof PictureViewHolder) {
            PictureViewHolder holder = (PictureViewHolder) vh;
            Album album = albums.get(holder.getAdapterPosition());
            ImageUtil.loadImgByUrl(holder.ivPhoto, album.getPath());

            // ★★★★★
            holder.chkCheck.setOnCheckedChangeListener(null);
            holder.chkCheck.setChecked(album.isChecked());
            if (checkMode) {
                holder.chkCheck.setVisibility(View.VISIBLE);
            } else {
                holder.chkCheck.setVisibility(View.INVISIBLE);
            }

            if (!checkMode) {
                View.OnClickListener clickListener = v -> listener.callbackPicture(v.getId(), position, false);
                holder.ivPhoto.setOnClickListener(clickListener);

                View.OnLongClickListener longClickListener = v -> {
                    listener.callbackLongPicture(v.getId(), position, false);
                    return true;
                };
                holder.ivPhoto.setOnLongClickListener(longClickListener);
            }
            holder.chkCheck.setOnCheckedChangeListener((buttonView, isChecked) -> listener.callbackPicture(buttonView.getId(), position, isChecked));
        }
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private CheckBox chkCheck;

        public PictureViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            chkCheck = itemView.findViewById(R.id.chkCheck);
        }
    }

    public interface OnPictureListener {
        void callbackPicture(int id, int position, boolean checked);

        void callbackLongPicture(int id, int position, boolean checked);
    }
}
