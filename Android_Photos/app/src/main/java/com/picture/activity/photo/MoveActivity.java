package com.picture.activity.photo;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.picture.BaseActivity;
import com.picture.R;
import com.picture.common.Res;
import com.picture.entity.Album;
import com.picture.logic.MoveAdapter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Move photos to album
 */
public class MoveActivity extends BaseActivity implements MoveAdapter.OnMoveListener {
    /**
     * Original image path
     */
    public static final String PARAM_SOURCE_PATH = "sourcePath";
    /**
     * Back
     */
    @BindView(R.id.ivBack)
    ImageView ivBack;
    /**
     * Album name list
     */
    @BindView(R.id.rlvAlbum)
    RecyclerView rlvAlbum;
    /**
     * adapter
     */
    private MoveAdapter adapter;
    /**
     * Album folder name setting
     */
    private List<Album> albums;
    /**
     * Original path
     */
    private String sourcePath;

    @Override
    protected int getLayout() {
        return R.layout.activity_move;
    }

    @Override
    protected void initView() {
        albums = new ArrayList<>();
        initBlackStatusBar();
        receiverParameter();
        initListView();
        showProgressDialog(0);
    }

    /**
     * Receive parameters
     */
    private void receiverParameter() {
        sourcePath = getIntent().getStringExtra(PARAM_SOURCE_PATH);
    }

    /**
     * Initialize the list container
     */
    private void initListView() {
        rlvAlbum.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MoveAdapter(this, albums, this);
        rlvAlbum.setAdapter(adapter);
    }

    @Override
    public void exec(int callViewById) {
        File fileDir = new File(Res.url.picture_other_uri);
        File[] filterFolder = fileDir.listFiles();
        for (File file : filterFolder) {
            // If the file is a folder, it is expressed as the album name
            if (file.isDirectory()) {
                Album album = new Album();
                album.setFileName(file.getName());
                album.setPath(file.getAbsolutePath());
                albums.add(album);
            }
        }
    }

    @Override
    public void onProgressBarResult(int calledByViewId) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void callbackMove(int id, int position) {
        Album album = albums.get(position);
        if (album != null) {
            String targetDir = album.getPath();
            try {
                FileUtils.moveFileToDirectory(new File(sourcePath), new File(targetDir), false);
                finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Bind the Click event of the View such as buttons
     */
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