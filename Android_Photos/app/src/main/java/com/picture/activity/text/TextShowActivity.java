package com.picture.activity.text;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.picture.BaseActivity;
import com.picture.R;
import com.picture.entity.Album;
import com.picture.util.DateUtil;
import com.picture.util.ImageUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Text photo display
 */
public class TextShowActivity extends BaseActivity {
    /**
     * Photo parameters
     */
    public static final String PARAM_ALBUM = "album";
    /**
     * year month day
     */
    @BindView(R.id.tvDate)
    TextView tvDate;
    /**
     * time
     */
    @BindView(R.id.tvTime)
    TextView tvTime;
    /**
     * photo
     */
    @BindView(R.id.ivPhoto)
    ImageView ivPhoto;
    /**
     * Photo path
     */
    private Album album;

    @Override
    protected int getLayout() {
        return R.layout.activity_text_show;
    }

    @Override
    protected void initView() {
        initBlackStatusBar();
        receiveParameter();
        initData();
    }

    /**
     * Receive parameters
     */
    private void receiveParameter() {
        album = getIntent().getParcelableExtra(PARAM_ALBUM);
    }

    /**
     * Initialization data
     */
    private void initData() {
        String date = DateUtil.convertToString(album.getDate());
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        tvDate.setText(DateUtil.formatYMD(year, month, day));
        tvTime.setText(DateUtil.formatTime(album.getDate()));
        ImageUtil.loadImgByUrl(ivPhoto, album.getPath());
    }

    @OnClick({R.id.ivBack})
    public void onViewClicked(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ivBack:
                finish();
                break;
        }
    }
}