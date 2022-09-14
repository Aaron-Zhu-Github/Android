package com.picture.logic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.picture.R;
import com.picture.entity.Album;
import com.picture.util.ImageUtil;
import com.picture.view.viewpager.JazzyViewPager;
import com.picture.view.viewpager.OutlineContainer;

import java.util.List;

public class SlideShowPagerAdapter extends PagerAdapter {
    private Context context;
    private OnPagerChangeListener onPagerChangeListener;
    private LayoutInflater inflater;
    private JazzyViewPager pager;
    private List<Album> albums;

    public SlideShowPagerAdapter(Context context, JazzyViewPager pager, List<Album> albums) {
        this.context = context;
        this.pager = pager;
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.albums = albums;
    }

    @Override
    public int getCount() {
        if (albums == null) {
            return 0;
        }
        return albums.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        if (view instanceof OutlineContainer) {
            return ((OutlineContainer) view).getChildAt(0) == obj;
        } else {
            return view == obj;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = inflater.inflate(R.layout.activity_slide_show_item, container, false);
        ImageView ivPicUrl = view.findViewById(R.id.ivPicUrl);
        ImageUtil.loadImgByUrl(ivPicUrl, albums.get(position).getPath());

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = pager.findViewFromObject(position);
        container.removeView(view);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    public interface OnPagerChangeListener {
        void onClickPagerItem(int id, int position, boolean isRight);
    }

    public void setOnPagerChangeListener(OnPagerChangeListener onPagerChangeListener) {
        this.onPagerChangeListener = onPagerChangeListener;
    }
}
