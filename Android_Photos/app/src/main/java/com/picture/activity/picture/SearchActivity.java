package com.picture.activity.picture;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.picture.BaseActivity;
import com.picture.R;
import com.picture.dialog.ConfirmDialog;
import com.picture.dialog.ListDialog;
import com.picture.entity.Album;
import com.picture.entity.Tag;
import com.picture.logic.SearchAdapter;
import com.picture.manager.TagManager;
import com.picture.view.SpaceGridItemDecoration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Search page
 */
public class SearchActivity extends BaseActivity implements SearchAdapter.OnSearchListener {
    /**
     * Search tag
     */
    @BindView(R.id.tvSelectType)
    TextView tvSelectType;
    /**
     * Retrieve content
     */
    @BindView(R.id.etContent)
    EditText etContent;
    /**
     * Search button
     */
    @BindView(R.id.btnSearch)
    Button btnSearch;
    /**
     * Retrieve data container
     */
    @BindView(R.id.rlvSearch)
    RecyclerView rlvSearch;
    /**
     * data source
     */
    private List<Album> albums;
    /**
     * adapter
     */
    private SearchAdapter adapter;
    /**
     * tag type
     */
    private String tag;
    /**
     * content
     */
    private String searchContent;
    /**
     * Persistence
     */
    private TagManager manager;

    @Override
    protected int getLayout() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {
        initBlackStatusBar();
        albums = new ArrayList<>();
        manager = new TagManager(this);
        initListView();
    }

    /**
     * Initialization data list
     */
    private void initListView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        rlvSearch.setLayoutManager(layoutManager);
        rlvSearch.addItemDecoration(new SpaceGridItemDecoration(20));
        rlvSearch.setItemViewCacheSize(20);
        adapter = new SearchAdapter(this, albums, this);
        rlvSearch.setAdapter(adapter);
    }

    @OnClick({R.id.ivBack, R.id.tvSelectType, R.id.btnSearch})
    public void onViewClicked(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.tvSelectType:
                onTvSelectTypeClick();
                break;
            case R.id.btnSearch:
                onSearchClick();
                break;
        }
    }

    /**
     * Choose label type
     */
    private void onTvSelectTypeClick() {
        List<String> tags = Arrays.asList(getArrayFromRes(R.array.tag_type));
        ListDialog dialog = new ListDialog(this, "tag", tags);
        dialog.show();
    }

    @Override
    public void onListDialogResult(int calledByViewId, DialogAction whichButton, String resultValue) {
        if (whichButton == DialogAction.POSITIVE && !TextUtils.isEmpty(resultValue)) {
            tvSelectType.setText(resultValue);
        }
    }

    /**
     * Search button click
     */
    private void onSearchClick() {
        String tagType = String.valueOf(tvSelectType.getText());
        if (TextUtils.isEmpty(tagType)) {
            showToast("Please select label type");
            return;
        }
        tag = tagType;

        String content = String.valueOf(etContent.getText());
        if (TextUtils.isEmpty(content)) {
            showToast("Please enter search content");
            return;
        }
        searchContent = content;

        showProgressDialog(0);
    }

    @Override
    public void exec(int callViewById) {
        albums.clear();
        List<Tag> tags = manager.queryTags(tag, searchContent);
        if (tags != null) {
            for (Tag t : tags) {
                Album album = new Album();
                album.setPath(t.getFilePath());
                album.setFileName(t.getFileName());
                albums.add(album);
            }
        }
    }

    @Override
    public void onProgressBarResult(int calledByViewId) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void callbackSearch(int id, int position) {
        if (albums.size() == 0) {
            ConfirmDialog dialog = new ConfirmDialog(this, 0, "tips", "There is no photo, it cannot be changed to film display", true);
            dialog.show();
        }
        Intent intent = new Intent(this, SlideShowActivity.class);
        intent.putExtra(SlideShowActivity.PARAM_ALBUMS, (Serializable) albums);
        intent.putExtra(SlideShowActivity.PARAM_ALBUM_NAME, "");
        startActivity(intent);
    }
}