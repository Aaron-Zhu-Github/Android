package com.picture.activity.picture;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.picture.BaseActivity;
import com.picture.R;
import com.picture.activity.photo.PhotoShowActivity;
import com.picture.dialog.ConfirmDialog;
import com.picture.entity.Album;
import com.picture.logic.PictureShowAdapter;
import com.picture.util.FileUtil;
import com.picture.view.SpaceGridItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Folder picture display screen
 */
public class PictureShowActivity extends BaseActivity implements PictureShowAdapter.OnPictureListener {
    /**
     * Request photo editing page
     */
    public static final int REQUEST_EDIT_PICTURE_CODE = 2000;
    /**
     * Request to choose a picture
     */
    public static final int REQUEST_SELECT_PICTURE = 1000;
    /**
     * Parameter folder path
     */
    public static final String PARAM_FOLDER_PATH = "folderPath";
    /**
     * Parameter folder name
     */
    public static final String PARAM_FOLDER_NAME = "folderName";
    /**
     * Parametric character
     */
    public static final String PARAM_FIGURE = "figure";
    /**
     * Folder name
     */
    @BindView(R.id.tvFolderName)
    TextView tvFolderName;
    /**
     * Slide show
     */
    @BindView(R.id.ivSlideShow)
    ImageView ivSlideShow;
    /**
     * add pictures
     */
    @BindView(R.id.ivAdd)
    ImageView ivAdd;
    /**
     * Pull down to refresh
     */
    @BindView(R.id.srlFolder)
    SmartRefreshLayout srlFolder;
    /**
     * Data container
     */
    @BindView(R.id.rlvFolder)
    RecyclerView rlvFolder;
    /**
     * Operating area
     */
    @BindView(R.id.layoutOperation)
    LinearLayout layoutOperation;
    /**
     * Delete
     */
    @BindView(R.id.layoutDelete)
    LinearLayout layoutDelete;
    /**
     * Cancel
     */
    @BindView(R.id.layoutCancel)
    LinearLayout layoutCancel;
    /**
     * Picture refresh adapter
     */
    private PictureShowAdapter adapter;
    /**
     * Folder path
     */
    private String folderPath;
    /**
     * Folder name
     */
    private String folderName;
    /**
     * Album list
     */
    public static List<Album> albums;
    /**
     * Selection mode
     */
    private boolean checkMode;

    @Override
    protected int getLayout() {
        return R.layout.activity_show_picture;
    }

    @Override
    protected void initView() {
        initBlackStatusBar();
        srlFolder.setEnableLoadMore(false);
        albums = new ArrayList<>();
        folderPath = getIntent().getStringExtra(PARAM_FOLDER_PATH);
        folderName = getIntent().getStringExtra(PARAM_FOLDER_NAME);
        tvFolderName.setText(folderName);
        initListView();
        onLoadRefresh();
        // Load data
        showProgressDialog(0);
    }

    /**
     * Pull down to refresh loading data
     */
    private void onLoadRefresh() {
        srlFolder.setOnRefreshListener(refreshLayout -> {
            srlFolder.finishRefresh();
            showProgressDialog(0);
        });
    }

    /**
     * Initialization list
     */
    private void initListView() {
        adapter = new PictureShowAdapter(this, albums, this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        rlvFolder.setLayoutManager(layoutManager);
        rlvFolder.addItemDecoration(new SpaceGridItemDecoration(20));
        rlvFolder.setItemViewCacheSize(20);
        rlvFolder.setAdapter(adapter);
    }

    @Override
    public void exec(int callViewById) {
        albums.clear();
        // Traverse folders, read pictures
        File fileDir = new File(folderPath);
        File[] filterFolder = fileDir.listFiles();
        Calendar cal = Calendar.getInstance();
        for (File file : filterFolder) {
            if (!file.getName().contains("hwbk")) {
                Album album = new Album();
                Date fileDate = FileUtil.parseDate(file);
                if (fileDate != null) {
                    cal.setTime(fileDate);
                    // Zero milliseconds
                    cal.set(Calendar.MILLISECOND, 0);
                    album.setFileName(file.getName());
                    album.setDate(cal.getTime().getTime());
                    album.setPath(file.getAbsolutePath());
                    albums.add(album);
                }
            }
        }
        // Sort date in reverse order
        if (albums.size() > 0) {
            Collections.sort(albums, (o1, o2) -> {
                if (o1.getDate() > o2.getDate()) {
                    return -1;
                } else if (o1.getDate() == o2.getDate()) {
                    return 0;
                }
                return 1;
            });
            for (int pos = 0; pos < albums.size(); pos++) {
                albums.get(pos).setPosition(pos);
            }
        }
    }

    @Override
    public void onProgressBarResult(int calledByViewId) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void callbackPicture(int id, int position, boolean checked) {
        if (!checkMode) {
            Intent intent = new Intent(this, PhotoShowActivity.class);
            intent.putExtra(PhotoShowActivity.PARAM_POSITION, position);
            intent.putExtra(PhotoShowActivity.PARAM_ALBUM, albums.get(position));
            intent.putExtra(PhotoShowActivity.PARAM_FOLDER, folderName);
            startActivityForResult(intent, REQUEST_EDIT_PICTURE_CODE);
        } else {
            Album album = albums.get(position);
            if (album != null) {
                album.setChecked(checked);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @OnClick({R.id.ivBack, R.id.ivSlideShow, R.id.ivAdd, R.id.layoutCancel, R.id.layoutDelete})
    public void onViewClicked(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.ivSlideShow:
                onIvSlideShowClick();
                break;
            case R.id.ivAdd:
                onIvAddClick();
                break;
            case R.id.layoutCancel:
                onLayoutCancelClick(id);
                break;
            case R.id.layoutDelete:
                onLayoutDeleteClick(id);
                break;
        }
    }

    /**
     * Slide show
     */
    private void onIvSlideShowClick() {
        if (albums.size() == 0) {
            ConfirmDialog dialog = new ConfirmDialog(this, 0, "tips", "There is no photo, it cannot be changed to film display", true);
            dialog.show();
        }
        Intent intent = new Intent(this, SlideShowActivity.class);
        intent.putExtra(SlideShowActivity.PARAM_ALBUMS, (Serializable) albums);
        intent.putExtra(SlideShowActivity.PARAM_ALBUM_NAME, folderName);
        startActivity(intent);
    }

    /**
     * add pictures
     */
    private void onIvAddClick() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, REQUEST_SELECT_PICTURE);
    }

    /**
     * @param id cancel
     */
    private void onLayoutCancelClick(int id) {
        checkMode = false;

        for (Album album : albums) {
            album.setChecked(false);
        }

        adapter.setCheckMode(false);
        adapter.notifyDataSetChanged();
        if (checkMode) {
            layoutOperation.setVisibility(View.VISIBLE);
        } else {
            layoutOperation.setVisibility(View.GONE);
        }
    }

    /**
     * @param id delete
     */
    private void onLayoutDeleteClick(int id) {
        // If no picture is selected, it is invalid
        int count = 0;
        for (Album album : albums) {
            if (album.isChecked()) {
                count++;
            }
        }
        if (count == 0) {
            showToast("No pictures selected to delete!");
            return;
        }

        ConfirmDialog dialog = new ConfirmDialog(this, id, "tips", "Do you want to delete the selected picture?", false);
        dialog.show();
    }

    @Override
    public void onConfirmDialogResult(int calledByViewId, DialogAction whichButton, String resultValue) {
        if (whichButton == DialogAction.POSITIVE) {
            if (calledByViewId == R.id.layoutDelete) {
                List<Album> addAlbums = new ArrayList<>();
                for (Album a : albums) {
                    if (a.isChecked()) {
                        addAlbums.add(a);
                    }
                }
                for (Album b : addAlbums) {
                    boolean isSuccess = FileUtil.deleteFile(b.getPath());
                    if (isSuccess) {
                        int index = getRemovePosition(b);
                        if (index != -1) {
                            albums.remove(index);
                        }
                    }
                }
                this.checkMode = false;
                adapter.setCheckMode(false);
                adapter.notifyDataSetChanged();
                // Otherwise it won't show
                if (checkMode) {
                    layoutOperation.setVisibility(View.VISIBLE);
                } else {
                    layoutOperation.setVisibility(View.GONE);
                }
            }
        }
    }

    private int getRemovePosition(Album album) {
        int index = -1;
        for (int i = 0; i < albums.size(); i++) {
            if (album.getPath().equals(albums.get(i).getPath()) && album.getDate() == albums.get(i).getDate()) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Select multiple pictures
        if (requestCode == REQUEST_SELECT_PICTURE) {
            if (data != null) {
                ClipData imageNames = data.getClipData();
                for (int i = 0; i < imageNames.getItemCount(); i++) {
                    Uri imageUri = imageNames.getItemAt(i).getUri();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(
                            imageUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    try {
                        FileUtils.copyFileToDirectory(new File(filePath), new File(folderPath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (imageNames.getItemCount() > 0) {
                    showProgressDialog(0);
                }
            }
        } else if (requestCode == REQUEST_EDIT_PICTURE_CODE) {
            showProgressDialog(0);
        }
    }

    @Override
    public void callbackLongPicture(int id, int position, boolean checked) {
        checkMode = true;

        adapter.setCheckMode(true);
        adapter.notifyDataSetChanged();
        if (checkMode) {
            layoutOperation.setVisibility(View.VISIBLE);
        } else {
            layoutOperation.setVisibility(View.GONE);
        }
    }
}