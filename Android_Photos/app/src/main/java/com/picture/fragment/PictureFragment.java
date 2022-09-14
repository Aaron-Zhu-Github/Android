package com.picture.fragment;

import android.Manifest;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.picture.BaseFragment;
import com.picture.R;
import com.picture.activity.MainActivity;
import com.picture.activity.picture.PictureShowActivity;
import com.picture.activity.picture.SearchActivity;
import com.picture.common.Res;
import com.picture.dialog.ConfirmDialog;
import com.picture.dialog.InputTextDialog;
import com.picture.entity.Album;
import com.picture.entity.TimeAlbum;
import com.picture.logic.PictureAdapter;
import com.picture.util.FileUtil;
import com.picture.view.SpaceGridItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yanzhenjie.permission.AndPermission;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class PictureFragment extends BaseFragment implements PictureAdapter.OnPictureListener {
    @BindView(R.id.tvSearch)
    TextView tvSearch;
    @BindView(R.id.tvSelectPicture)
    TextView tvSelectPicture;
    @BindView(R.id.tvNewPicture)
    TextView tvNewPicture;
    @BindView(R.id.srlPicture)
    SmartRefreshLayout srlPicture;
    @BindView(R.id.rlvPicture)
    RecyclerView rlvPicture;
    @BindView(R.id.layoutOperation)
    LinearLayout layoutOperation;
    @BindView(R.id.layoutDelete)
    LinearLayout layoutDelete;
    @BindView(R.id.layoutRename)
    LinearLayout layoutRename;
    private List<TimeAlbum> data;
    private PictureAdapter adapter;
    private boolean checkMode;
    private TimeAlbum renameTa;

    @Override
    protected int getLayout() {
        return R.layout.fragment_picture;
    }

    @Override
    protected void initView() {
        initBlackStatusBar();
        srlPicture.setEnableLoadMore(false);
        data = new ArrayList<>();
        initListView();
        onLoadRefresh();
    }

    private void onLoadRefresh() {
        srlPicture.setOnRefreshListener(refreshLayout -> {
            srlPicture.finishRefresh();
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
                    showProgressDialog(0);
                })
                .onDenied(data -> showToast("For better use, please allow all permissions"))
                .start();
    }

    private void initListView() {
        adapter = new PictureAdapter(getActivity(), data, this);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        rlvPicture.setLayoutManager(layoutManager);
        rlvPicture.addItemDecoration(new SpaceGridItemDecoration(20));
        rlvPicture.setItemViewCacheSize(20);
        rlvPicture.setAdapter(adapter);
    }

    @Override
    public void exec(int callViewById) {
        data.clear();
        Calendar cal = Calendar.getInstance();
        List<TimeAlbum> tmpData = new ArrayList<>();
        File fileDir = new File(Res.url.picture_other_uri);
        File[] filterFolder = fileDir.listFiles();
        if (filterFolder != null) {
            for (File file : filterFolder) {
                TimeAlbum taFolder = new TimeAlbum();
                Date fileDate = FileUtil.parseDate(file);
                if (fileDate != null) {
                    cal.setTime(fileDate);
                    cal.set(Calendar.MILLISECOND, 0);
                    taFolder.setDate(cal.getTime().getTime());
                    taFolder.setType(file.getName());
                    taFolder.setCount(file.listFiles() != null ? file.listFiles().length : 0);
                } else if (file.isDirectory()) {
                    taFolder.setDate(file.lastModified());
                    taFolder.setType(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1));
                    if (file.listFiles() != null && file.listFiles().length > 0) {
                        int count = 0;
                        for (File albumFile : file.listFiles()) {
                            if (!albumFile.getName().contains("hwbk")) {
                                Date fileDate2 = FileUtil.parseDate(albumFile);
                                cal.setTime(fileDate2);
                                cal.set(Calendar.MILLISECOND, 0);
                                Album album = new Album();
                                album.setDate(cal.getTime().getTime());
                                album.setPath(albumFile.getAbsolutePath());
                                taFolder.getAlbums().add(album);
                                count++;
                            }
                        }
                        taFolder.setCount(count);
                    }
                }
                taFolder.setPath(file.getAbsolutePath());
                taFolder.setFigureTag(false);
                tmpData.add(taFolder);
            }
        }
        if (tmpData.size() > 0) {
            Collections.sort(tmpData, (o1, o2) -> {
                if (o1.getDate() > o2.getDate()) {
                    return -1;
                } else if (o1.getDate() == o2.getDate()) {
                    return 0;
                }
                return 1;
            });
            data.addAll(tmpData);
        }
    }

    @Override
    public void onProgressBarResult(int calledByViewId) {
        adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.tvSearch, R.id.tvSelectPicture, R.id.tvNewPicture, R.id.layoutRename, R.id.layoutDelete})
    public void onViewClicked(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tvSearch:
                onTvSearchClick();
                break;
            case R.id.tvSelectPicture:
                onTvSelectPicture();
                break;
            case R.id.tvNewPicture:
                onTvNewPictureClick(id);
                break;
            case R.id.layoutRename:
                onLayoutRenameClick(id);
                break;
            case R.id.layoutDelete:
                onLayoutDeleteClick(id);
                break;
        }
    }

    private void onTvSearchClick() {
        toLinkPageNotFinished(SearchActivity.class);
    }

    private void onTvSelectPicture() {
        if (!checkMode) {
            tvSelectPicture.setText("cancel");
            checkMode = true;
        } else {
            tvSelectPicture.setText("choose");
            checkMode = false;
            for (int i = 1; i < data.size(); i++) {
                data.get(i).setChecked(false);
            }
        }
        adapter.setCheckMode(checkMode);
        adapter.notifyDataSetChanged();
        MainActivity.showControl(!checkMode);
        if (checkMode) {
            layoutOperation.setVisibility(View.VISIBLE);
            tvNewPicture.setVisibility(View.GONE);
        } else {
            layoutOperation.setVisibility(View.GONE);
            tvNewPicture.setVisibility(View.VISIBLE);
        }
    }

    private void onTvNewPictureClick(int id) {
        InputTextDialog dialog = new InputTextDialog(this, id, "New album");
        dialog.show();
    }

    private void onLayoutRenameClick(int id) {
        layoutRename.setEnabled(true);
        int selectCount = 0;
        for (int i = 0; i < data.size(); i++) {
            TimeAlbum ta = data.get(i);
            if (ta.isChecked()) {
                renameTa = ta;
                selectCount++;
            }
            if (selectCount > 1) {
                break;
            }
        }
        if (selectCount == 0 || selectCount > 1) {
            layoutRename.setEnabled(false);
        }
        else {
            InputTextDialog dialog = new InputTextDialog(this, id, "rename");
            dialog.show();
        }
    }

    private void onLayoutDeleteClick(int id) {
        ConfirmDialog dialog = new ConfirmDialog(this, id, "tips", "Do you want to delete this album (including photos in the album)?", false);
        dialog.show();
    }

    @Override
    public void onConfirmDialogResult(int calledByViewId, DialogAction whichButton, String resultValue) {
        if (whichButton == DialogAction.POSITIVE) {
            if (calledByViewId == R.id.layoutDelete) {
                List<TimeAlbum> addTimeAlbums = new CopyOnWriteArrayList<>();
                for (TimeAlbum ta : data) {
                    if (ta.isChecked()) {
                        addTimeAlbums.add(ta);
                    }
                }
                for (TimeAlbum ta : addTimeAlbums) {
                    for (Album album : ta.getAlbums()) {
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
                    File file = new File(ta.getPath());
                    if (file.isDirectory()) {
                        try {
                            int index = data.indexOf(new TimeAlbum(ta.getDate()));
                            data.remove(index);
                            file.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                tvSelectPicture.setText("choose");
                this.checkMode = false;
                adapter.setCheckMode(false);
                adapter.notifyDataSetChanged();
                MainActivity.showControl(!checkMode);
                if (checkMode) {
                    layoutOperation.setVisibility(View.VISIBLE);
                    tvNewPicture.setVisibility(View.GONE);
                } else {
                    layoutOperation.setVisibility(View.GONE);
                    tvNewPicture.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onInputTextDialogResult(int calledByViewId, DialogAction whichButton, String resultValue) {
        if (whichButton == DialogAction.POSITIVE) {
            if (calledByViewId == R.id.tvNewPicture) {
                if (!TextUtils.isEmpty(resultValue)) {
                    if (FileUtil.createFolder(Res.url.picture_other_uri, resultValue)) {
                        showProgressDialog(0);
                    }
                }
            }
            else if (calledByViewId == R.id.layoutRename) {
                String oldName = renameTa.getType();
                String newName = resultValue;
                if (newName.equals(oldName)) {
                    showToast("Cannot be the same as the old name!");
                    return;
                }
                File oldFile = new File(Res.url.picture_other_uri + "/" + oldName);
                File newFile = new File(Res.url.picture_other_uri + "/" + newName);
                if (!oldFile.exists()) {
                    showToast("The old folder does not exist!");
                    return;
                }
                if (newFile.exists()) {
                    showToast("Already exists, cannot be created repeatedly!");
                    return;
                }
                oldFile.renameTo(newFile);
                int index = data.indexOf(new TimeAlbum(renameTa.getDate()));
                data.get(index).setType(newName);
                data.get(index).setChecked(false);

                tvSelectPicture.setText("choose");
                this.checkMode = false;
                adapter.setCheckMode(false);
                adapter.notifyDataSetChanged();
                MainActivity.showControl(!checkMode);
                if (checkMode) {
                    layoutOperation.setVisibility(View.VISIBLE);
                    tvNewPicture.setVisibility(View.GONE);
                } else {
                    layoutOperation.setVisibility(View.GONE);
                    tvNewPicture.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void callbackPicture(int id, int section, boolean checked) {
        if (!checkMode) {
            TimeAlbum ta = data.get(section);
            boolean figureTag = ta.isFigureTag();
            Intent intent = new Intent(getActivity(), PictureShowActivity.class);
            intent.putExtra(PictureShowActivity.PARAM_FOLDER_PATH, ta.getPath());
            intent.putExtra(PictureShowActivity.PARAM_FOLDER_NAME, ta.getType());
            intent.putExtra(PictureShowActivity.PARAM_FIGURE, figureTag);
            startActivity(intent);
        } else {
            TimeAlbum ta = data.get(section);
            if (ta != null) {
                ta.setChecked(checked);
                adapter.notifyDataSetChanged();
            }
        }
    }
}