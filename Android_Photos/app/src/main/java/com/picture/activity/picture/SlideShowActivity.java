package com.picture.activity.picture;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.picture.BaseActivity;
import com.picture.R;
import com.picture.entity.Album;
import com.picture.logic.SlideShowPagerAdapter;
import com.picture.view.viewpager.JazzyViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Slide show
 */
public class SlideShowActivity extends BaseActivity implements SlideShowPagerAdapter.OnPagerChangeListener, ViewPager.OnPageChangeListener {
    /**
     * albums
     */
    public static final String PARAM_ALBUMS = "albums";
    /**
     * album name
     */
    public static final String PARAM_ALBUM_NAME = "name";
    /**
     * album name
     */
    @BindView(R.id.tvAlbumName)
    TextView tvAlbumName;
    /**
     * Previous
     */
    @BindView(R.id.layoutPre)
    LinearLayout layoutPre;
    /**
     * Next
     */
    @BindView(R.id.layoutNext)
    LinearLayout layoutNext;
    /**
     * Pagination container
     */
    @BindView(R.id.viewPager)
    JazzyViewPager viewPager;
    /**
     * Photo album
     */
    private List<Album> albums;
    /**
     * Folder name
     */
    private String folderName;
    /**
     * Current page number
     */
    private int curPage = 0;
    /**
     * adapter
     */
    private SlideShowPagerAdapter adapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_slide_show;
    }

    @Override
    protected void initView() {
        initWhiteStatusBar();
        albums = new ArrayList<>();
        albums = (List<Album>) getIntent().getSerializableExtra(PARAM_ALBUMS);
        folderName = getIntent().getStringExtra(PARAM_ALBUM_NAME);
        tvAlbumName.setText(folderName);

        initViewPager();
    }

    /**
     * Initialize viewpager
     */
    private void initViewPager() {
        adapter = new SlideShowPagerAdapter(this, viewPager, albums);
        adapter.setOnPagerChangeListener(this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
    }

    @OnClick({R.id.ivBack, R.id.layoutPre, R.id.layoutNext})
    public void onViewClicked(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.layoutPre:
                if (curPage > 0) {
                    curPage--;
                    viewPager.setCurrentItem(curPage);
                }
                break;
            case R.id.layoutNext:
                if (curPage < albums.size() - 1) {
                    curPage++;
                    viewPager.setCurrentItem(curPage);
                }
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        curPage = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClickPagerItem(int id, int position, boolean isRight) {

    }
}