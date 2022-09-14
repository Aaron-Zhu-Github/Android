package com.picture.fragment;

import android.Manifest;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.picture.BaseFragment;
import com.picture.R;
import com.picture.activity.MainActivity;
import com.picture.activity.photo.PhotoShowActivity;
import com.picture.common.Res;
import com.picture.dialog.ConfirmDialog;
import com.picture.entity.Album;
import com.picture.entity.TimeAlbum;
import com.picture.logic.PhotoAdapter;
import com.picture.util.DateUtil;
import com.picture.util.FileUtil;
import com.picture.view.SpaceGridItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.truizlop.sectionedrecyclerview.SectionedSpanSizeLookup;
import com.yanzhenjie.permission.AndPermission;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Photo page
 */
public class PhotoFragment extends BaseFragment implements PhotoAdapter.OnSelectListener {
    /**
     * Request photo browse page
     */
    public static final int REQUEST_SHOW_PHOTO_CODE = 1000;
    /**
     * Pull down to refresh the container
     */
    @BindView(R.id.srlPhoto)
    SmartRefreshLayout srlPhoto;
    /**
     * Secondary expansion container
     */
    @BindView(R.id.rlvPhoto)
    RecyclerView rlvPhoto;
    /**
     * Title at the top of the photo page
     */
    @BindView(R.id.tvPhotoTitle)
    TextView tvPhotoTitle;
    /**
     * "Select" button
     */
    @BindView(R.id.tvSelectPhoto)
    TextView tvSelectPhoto;
    /**
     * Number of photos
     */
    @BindView(R.id.tvPhotoCount)
    TextView tvPhotoCount;
    /**
     * Operating area
     */
    @BindView(R.id.layoutOperation)
    LinearLayout layoutOperation;
    /**
     * select all
     */
    @BindView(R.id.layoutAll)
    LinearLayout layoutAll;
    /**
     * Unselect all
     */
    @BindView(R.id.layoutUnAll)
    LinearLayout layoutUnAll;
    /**
     * Delete
     */
    @BindView(R.id.layoutDelete)
    LinearLayout layoutDelete;
    /**
     * Photo
     */
    private List<TimeAlbum> data;
    /**
     * Photo adapter
     */
    private PhotoAdapter adapter;
    /**
     * Selection mode
     */
    private boolean checkMode;

    @Override
    protected int getLayout() {
        return R.layout.fragment_photo;
    }

    @Override
    protected void initView() {
        data = new ArrayList<>();
        srlPhoto.setEnableLoadMore(false);
        initListView();
        onLoadRefresh();
    }

    /**
     * Pull down to refresh loading
     */
    private void onLoadRefresh() {
        srlPhoto.setOnRefreshListener(refreshLayout -> {
            srlPhoto.finishRefresh();
            showProgressDialog(0);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        AndPermission.with(this)
                .runtime()
                .permission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .onGranted(data -> {
                    // 加载数据
                    showProgressDialog(0);
                })
                .onDenied(data -> showToast("For better use, please allow all permissions"))
                .start();
    }

    /**
     * Initialize data sources
     */
    private void initListView() {
        adapter = new PhotoAdapter(getActivity(), data, this);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        SectionedSpanSizeLookup lookup = new SectionedSpanSizeLookup(adapter, layoutManager);
        layoutManager.setSpanSizeLookup(lookup);
        rlvPhoto.setLayoutManager(layoutManager);
        rlvPhoto.addItemDecoration(new SpaceGridItemDecoration(20));
        rlvPhoto.setItemViewCacheSize(20);
        rlvPhoto.setAdapter(adapter);
    }

    /**
     * @param callViewById Associated operation ID
     *                     Used to load pictures asynchronously
     */
    @Override
    public void exec(int callViewById) {
        data.clear();
        // 1. Filter files with the suffix ".jpg"
        File filePhoto = new File(Res.url.photo_uri);
        File[] filterPhotos = filePhoto.listFiles((pathname) -> {
            if (pathname.isDirectory()) {
                return false;
            }
            String fileName = pathname.getName();
            return fileName.endsWith(".jpg");
        });
        List<File> files = new ArrayList<>();
        files.addAll(Arrays.asList(filterPhotos));
        // 2. Fill in the photo
        Calendar cal = Calendar.getInstance();

        TimeAlbum ta = new TimeAlbum();
        // Sort files in reverse order
        Collections.sort(files, (o1, o2) -> {
            if (o1.lastModified() > o2.lastModified()) {
                return -1;
            } else if (o1.lastModified() == o2.lastModified()) {
                return 0;
            }
            return 1;
        });

        boolean hasMore = false;

        for (File file : files) {
            Date fileDate = FileUtil.parseDate(file);
            if (fileDate != null) {
                cal.setTime(fileDate);
                cal.set(Calendar.MILLISECOND, 0);
                Album album = new Album();
                album.setDate(cal.getTime().getTime());
                album.setPath(file.getAbsolutePath());
                album.setFileName(file.getName());

                // The first photo of each date
                if (ta.getAlbums().size() == 0) {
                    ta.setDate(album.getDate());
                    if (!hasMore) {
                        data.add(ta);
                    }
                }
                // If the first 10 digits of the date photo object are not the same, it means that there is a date collection photo
                // Need to regenerate a new object to store the collection of photos
                // The old collection is stored in data
                else {
                    String oldDateOfTop10 = DateUtil.convertToString(ta.getDate()).substring(0, 10);
                    String newDateOfTop10 = DateUtil.convertToString(album.getDate()).substring(0, 10);
                    // If the same, continue to insert in the old collection
                    // If they are not the same, create a new set
                    if (!newDateOfTop10.equals(oldDateOfTop10)) {
                        // do a sub-sort before saving
                        Collections.sort(ta.getAlbums(), (o1, o2) -> {
                            if (o1.getDate() > o2.getDate()) {
                                return -1;
                            } else if (o1.getDate() == o2.getDate()) {
                                return 0;
                            }
                            return 1;
                        });
                        if (hasMore) {
                            data.add(ta);
                        }
                        ta = new TimeAlbum();
                        ta.setDate(album.getDate());
                        hasMore = true;
                    }
                }
                ta.getAlbums().add(album);
            }
        }
        // 3. Sort
        Collections.sort(data, (o1, o2) -> {
            if (o1.getDate() > o2.getDate()) {
                return -1;
            } else if (o1.getDate() == o2.getDate()) {
                return 0;
            }
            return 1;
        });
    }

    @Override
    public void onProgressBarResult(int calledByViewId) {
        adapter.notifyDataSetChanged();
        resetPhotoCount();
    }

    /**
     * Show the number of photos
     */
    private void resetPhotoCount() {
        int count = 0;
        for (TimeAlbum ta : data) {
            if (ta.getAlbums() != null) {
                count += ta.getAlbums().size();
            }
        }
        tvPhotoCount.setText(count + " photos");
    }

    @OnClick({R.id.tvSelectPhoto, R.id.layoutAll, R.id.layoutUnAll, R.id.layoutDelete})
    public void onViewClicked(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tvSelectPhoto:
                onTvSelectPhotoClick();
                break;
            case R.id.layoutAll:
                onLayoutAllClick(true);
                break;
            case R.id.layoutUnAll:
                onLayoutAllClick(false);
                break;
            case R.id.layoutDelete:
                onLayoutDeleteClick();
                break;
        }
    }

    /**
     * Delete
     */
    private void onLayoutDeleteClick() {
        // If one is not selected, the button is unavailable
        boolean hasSelected = false;
        for (TimeAlbum ta : data) {
            if (ta.isChecked()) {
                hasSelected = true;
                break;
            }
            if (ta.getAlbums() != null) {
                for (Album album : ta.getAlbums()) {
                    if (album.isChecked()) {
                        hasSelected = true;
                        break;
                    }
                }
            }
        }
        // If selected, the pop-up window will be deleted
        if (hasSelected) {
            ConfirmDialog dialog = new ConfirmDialog(this, 0, "tips", "Do you want to delete the photo?", false);
            dialog.show();
        }
    }

    /**
     * Select all/deselect all
     */
    private void onLayoutAllClick(boolean allChecked) {
        if (allChecked) {
            layoutAll.setVisibility(View.GONE);
            layoutUnAll.setVisibility(View.VISIBLE);
        } else {
            layoutAll.setVisibility(View.VISIBLE);
            layoutUnAll.setVisibility(View.GONE);
        }
        for (TimeAlbum ta : data) {
            ta.setChecked(allChecked);
            if (ta.getAlbums() != null) {
                for (Album album : ta.getAlbums()) {
                    album.setChecked(allChecked);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * "Select" and "Cancel" switch
     */
    private void onTvSelectPhotoClick() {
        if (!checkMode) {
            tvSelectPhoto.setText("cancel");
            checkMode = true;
        } else {
            tvSelectPhoto.setText("choose");
            checkMode = false;
            // If it changes from "Cancel" to "Selected", all become unselected
            for (TimeAlbum ta : data) {
                ta.setChecked(false);
                if (ta.getAlbums() != null) {
                    for (Album album : ta.getAlbums()) {
                        album.setChecked(false);
                    }
                }
            }
        }
        adapter.setCheckMode(checkMode);
        adapter.notifyDataSetChanged();
        // Control whether the "Photo" and "Album" button areas of the main page are displayed
        MainActivity.showControl(!checkMode);
        // If "Select" changes to "Cancel", select all and delete fields at the bottom of the display
        // Otherwise, no display
        if (checkMode) {
            layoutOperation.setVisibility(View.VISIBLE);
        } else {
            layoutOperation.setVisibility(View.GONE);
        }
    }

    @Override
    public void callBackGroup(int section, boolean isChecked) {
        // If the parent is selected, the child also changes according to the parent
        TimeAlbum ta = data.get(section);
        ta.setChecked(isChecked);
        if (ta.getAlbums() != null) {
            for (Album album : ta.getAlbums()) {
                album.setChecked(isChecked);
            }
        }
        adapter.setCheckMode(checkMode);
        // If it is in the selected state, mark that the current row has child nodes selected
        data.get(section).setExistSelected(isChecked);
        // refresh page
        if (!rlvPhoto.isComputingLayout()) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void callBackChild(int section, int position, boolean isChecked) {
        if (!checkMode) {
            // If it is not selected, then jump to the edit page
            Intent intent = new Intent(getActivity(), PhotoShowActivity.class);
            intent.putExtra(PhotoShowActivity.PARAM_SECTION, section);
            intent.putExtra(PhotoShowActivity.PARAM_POSITION, position);
            intent.putExtra(PhotoShowActivity.PARAM_ALBUM, data.get(section).getAlbums().get(position));
            startActivityForResult(intent, REQUEST_SHOW_PHOTO_CODE);
        } else {
            // If all the children are selected, the parent is also selected
            // other parents are not selected
            TimeAlbum ta = data.get(section);
            if (ta.getAlbums() != null) {
                ta.getAlbums().get(position).setChecked(isChecked);
                boolean allChecked = true;
                for (Album album : ta.getAlbums()) {
                    if (!album.isChecked()) {
                        allChecked = false;
                        break;
                    }
                }
                ta.setChecked(allChecked);
            }
            boolean existSelected = false;
            for (Album album : ta.getAlbums()) {
                if (album.isChecked()) {
                    existSelected = true;
                    break;
                }
            }
            // If the child node is selected, it indicates that the current row is selected
            data.get(section).setExistSelected(existSelected);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onConfirmDialogResult(int calledByViewId, DialogAction whichButton, String resultValue) {
        if (whichButton == DialogAction.POSITIVE) {
            processDelete();
            resetPhotoCount();
        }
    }

    /**
     * Process photo deletion
     */
    private void processDelete() {
        // Get all selected nodes first
        List<TimeAlbum> addTimeAlbums = new CopyOnWriteArrayList<>();
        for (TimeAlbum ta : data) {
            if (ta.isExistSelected()) {
                TimeAlbum addTA = new TimeAlbum();
                addTA.setDate(ta.getDate());
                // If there are selected items, start filtering which child nodes are selected
                List<Album> addAlbums = new ArrayList<>();
                for (Album album : ta.getAlbums()) {
                    if (album.isChecked()) {
                        addAlbums.add(album);
                    }
                }
                addTA.setAlbums(addAlbums);
                addTimeAlbums.add(addTA);
            }
        }

        // delete photo
        for (TimeAlbum ta : addTimeAlbums) {
            for (Album album : ta.getAlbums()) {
                // Is selected, delete
                boolean isSuccess = FileUtil.deleteFile(album.getPath());
                if (isSuccess) {
                    int index = data.indexOf(new TimeAlbum(ta.getDate()));
                    TimeAlbum delTA = data.get(index);
                    delTA.getAlbums().remove(album);
                    if (delTA.getAlbums().size() == 0) {
                        data.remove(index);
                    }
                }
            }
        }
        // After the deletion is complete, "Cancel" becomes "Selected" status
        tvSelectPhoto.setText("choose");
        this.checkMode = false;
        adapter.setCheckMode(false);
        adapter.notifyDataSetChanged();
        // Control whether the "Photo" and "Album" button areas of the main page are displayed
        MainActivity.showControl(!checkMode);
        // If "Select" changes to "Cancel", select all and delete fields at the bottom of the display
        // Otherwise, no display
        if (checkMode) {
            layoutOperation.setVisibility(View.VISIBLE);
        } else {
            layoutOperation.setVisibility(View.GONE);
        }
    }
}