package com.picture.logic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picture.R;
import com.picture.entity.TimeAlbum;
import com.picture.util.ImageUtil;

import java.util.List;

public class PictureAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_EMPTY = 1000;
    private Context context;
    private List<TimeAlbum> timeAlbums;
    private OnPictureListener listener;
    private boolean checkMode;

    public PictureAdapter(Context context, List<TimeAlbum> timeAlbums, OnPictureListener listener) {
        this.context = context;
        this.timeAlbums = timeAlbums;
        this.listener = listener;
    }

    public void setCheckMode(boolean mode) {
        this.checkMode = mode;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_picture_item, parent, false);
        PictureViewHolder viewHolder = new PictureViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof PictureViewHolder) {
            PictureViewHolder holder = (PictureViewHolder) vh;
            TimeAlbum ta = timeAlbums.get(holder.getAdapterPosition());
            if (ta.getCount() == 0) {
                ImageUtil.loadImgByUrl(holder.ivPhoto, R.drawable.icon_menu_folder);
            } else {
                ImageUtil.loadImgByUrl(holder.ivPhoto, ta.getAlbums().get(0).getPath());
            }
            holder.tvType.setText(ta.getType());
            holder.tvCount.setText(String.valueOf(ta.getCount()));

            // ★★★★★
            holder.chkType.setOnCheckedChangeListener(null);
            holder.chkType.setChecked(ta.isChecked());
            if (checkMode) {
                if (!ta.isFigureTag()) {
                    holder.chkType.setVisibility(View.VISIBLE);
                }
            } else {
                holder.chkType.setVisibility(View.INVISIBLE);
            }

            if (!checkMode) {
                View.OnClickListener clickListener = v -> listener.callbackPicture(v.getId(), position, false);
                holder.ivPhoto.setOnClickListener(clickListener);
            }
            holder.chkType.setOnCheckedChangeListener((buttonView, isChecked) -> listener.callbackPicture(buttonView.getId(), position, isChecked));
        }
    }

    @Override
    public int getItemCount() {
        return timeAlbums.size();
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlPicture;
        private ImageView ivPhoto;
        private CheckBox chkType;
        private TextView tvType;
        private TextView tvCount;

        public PictureViewHolder(@NonNull View itemView) {
            super(itemView);
            rlPicture = itemView.findViewById(R.id.rlPicture);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            chkType = itemView.findViewById(R.id.chkType);
            tvType = itemView.findViewById(R.id.tvType);
            tvCount = itemView.findViewById(R.id.tvCount);
        }
    }

    public interface OnPictureListener {
        void callbackPicture(int id, int section, boolean checked);
    }
}
