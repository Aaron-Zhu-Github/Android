package com.picture.logic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.picture.R;
import com.picture.entity.Album;
import com.picture.entity.TimeAlbum;
import com.picture.util.DateUtil;
import com.picture.util.ImageUtil;
import com.truizlop.sectionedrecyclerview.SectionedRecyclerViewAdapter;

import java.util.List;

public class PhotoAdapter extends SectionedRecyclerViewAdapter<PhotoAdapter.HeaderViewHolder, PhotoAdapter.ItemViewHolder, PhotoAdapter.FooterViewHolder> {
    private Context context;
    private List<TimeAlbum> timeAlbums;
    private boolean checkMode;
    private OnSelectListener listener;

    public PhotoAdapter(Context context, List<TimeAlbum> timeAlbums, OnSelectListener listener) {
        this.context = context;
        this.timeAlbums = timeAlbums;
        this.listener = listener;
    }

    public void setCheckMode(boolean mode) {
        this.checkMode = mode;
    }

    @Override
    protected int getSectionCount() {
        return timeAlbums.size();
    }

    @Override
    protected int getItemCountForSection(int section) {
        return timeAlbums.get(section).getAlbums().size();
    }

    @Override
    protected boolean hasFooterInSection(int section) {
        return false;
    }

    @Override
    protected HeaderViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.fragment_photo_parent, parent, false);
        return new HeaderViewHolder(itemView);
    }

    @Override
    protected FooterViewHolder onCreateSectionFooterViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.fragment_photo_parent, parent, false);
        return new FooterViewHolder(itemView);
    }

    @Override
    protected ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.fragment_photo_child, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    protected void onBindSectionHeaderViewHolder(HeaderViewHolder holder, int section) {
        TimeAlbum timeAlbum = timeAlbums.get(section);
        if (null == timeAlbum) {
            return;
        }
        String date = DateUtil.convertToString(timeAlbum.getDate());
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        holder.tvDate.setText(DateUtil.formatYMD(year, month, day));
        // ★★★★★
        holder.chkParent.setOnCheckedChangeListener(null);
        holder.chkParent.setChecked(timeAlbum.isChecked());
        if (checkMode) {
            holder.chkParent.setVisibility(View.VISIBLE);
        } else {
            holder.chkParent.setVisibility(View.INVISIBLE);
        }

        holder.chkParent.setOnCheckedChangeListener((buttonView, isChecked) -> listener.callBackGroup(section, isChecked));
    }

    @Override
    protected void onBindSectionFooterViewHolder(FooterViewHolder holder, int section) {
    }

    @Override
    protected void onBindItemViewHolder(ItemViewHolder holder, int section, int position) {
        List<Album> albums = timeAlbums.get(section).getAlbums();
        if (null == albums) {
            return;
        }
        ImageUtil.loadImgByUrl(holder.ivPhoto, albums.get(position).getPath());
        // ★★★★★
        holder.chkChild.setOnCheckedChangeListener(null);
        holder.chkChild.setChecked(albums.get(position).isChecked());
        if (checkMode) {
            holder.chkChild.setVisibility(View.VISIBLE);
        } else {
            holder.chkChild.setVisibility(View.INVISIBLE);
        }

        if (!checkMode) {
            View.OnClickListener clickListener = v -> listener.callBackChild(section, position, holder.chkChild.isChecked());
            holder.ivPhoto.setOnClickListener(clickListener);
        }

        holder.chkChild.setOnCheckedChangeListener((buttonView, isChecked) -> listener.callBackChild(section, position, isChecked));
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private CheckBox chkChild;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            chkChild = itemView.findViewById(R.id.chkChild);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;
        private CheckBox chkParent;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            chkParent = itemView.findViewById(R.id.chkParent);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnSelectListener {
        void callBackGroup(int section, boolean isChecked);

        void callBackChild(int section, int position, boolean isChecked);
    }
}
