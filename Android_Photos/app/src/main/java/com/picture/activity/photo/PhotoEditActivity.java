package com.picture.activity.photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.afollestad.materialdialogs.DialogAction;
import com.picture.BaseActivity;
import com.picture.R;
import com.picture.camera.BottomSaveDialog;
import com.picture.camera.SaveEnum;
import com.picture.common.Res;
import com.picture.dialog.ConfirmDialog;
import com.picture.entity.Album;
import com.picture.util.BitmapUtil;
import com.picture.util.DateUtil;
import com.picture.util.ImageUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Photo editing page
 */
public class PhotoEditActivity extends BaseActivity implements BottomSaveDialog.OnSaveListener {
    /**
     * Request photo crop
     */
    public static final int REQUEST_PHOTO_PICK = 1000;
    /**
     * Photo parameters
     */
    public static final String PARAM_ALBUM = "album";
    /**
     * Row parameter
     */
    public static final String PARAM_SECTION = "section";
    /**
     * Detailed parameters
     */
    public static final String PARAM_POSITION = "position";
    /**
     * Path parameter
     */
    public static final String PARAM_FOLDER = "folderPath";
    /**
     * Saved path
     */
    public static final String SAVED_PATH = "savedPath";
    /**
     * Save
     */
    @BindView(R.id.ivSave)
    ImageView ivSave;
    /**
     * Photo
     */
    @BindView(R.id.ivPhoto)
    ImageView ivPhoto;
    /**
     * Crop
     */
    @BindView(R.id.layoutCut)
    LinearLayout layoutCut;
    /**
     * Mirror
     */
    @BindView(R.id.layoutImage)
    LinearLayout layoutImage;
    /**
     * Rotate
     */
    @BindView(R.id.layoutRotate)
    LinearLayout layoutRotate;
    /**
     * Row position
     */
    private int section;
    /**
     * Detailed location
     */
    private int position;
    /**
     * Photo path
     */
    private Album album;
    /**
     * Crop picture and save
     */
    private Uri smallUri;
    /**
     * Transformed image
     */
    private Bitmap bitmap;
    /**
     * Saved path
     */
    private String savedPath;
    /**
     * Folder path
     */
    private String folderPath;

    @Override
    protected int getLayout() {
        return R.layout.activity_photo_edit;
    }

    @Override
    protected void initView() {
        initWhiteStatusBar();
        receiveParameter();
        initData();
    }

    /**
     * Receive parameters
     */
    private void receiveParameter() {
        section = getIntent().getIntExtra(PARAM_SECTION, 0);
        position = getIntent().getIntExtra(PARAM_POSITION, 0);
        album = getIntent().getParcelableExtra(PARAM_ALBUM);
        folderPath = getIntent().getStringExtra(PARAM_FOLDER);
        bitmap = BitmapUtil.getBitmapFromSDCard(album.getPath());
    }

    /**
     * Initialization data
     */
    private void initData() {
        ImageUtil.loadImgByUrl(ivPhoto, bitmap);
    }

    /**
     * Bind the Click event of the View such as buttons
     */
    @OnClick({R.id.ivBack, R.id.ivSave, R.id.layoutCut, R.id.layoutImage, R.id.layoutRotate})
    public void onViewClicked(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ivBack:
                ConfirmDialog dialog = new ConfirmDialog(this, id, "tips", "Give up this modification?", false);
                dialog.show();
                break;
            case R.id.ivSave:
                onIvSaveClick();
                break;
            case R.id.layoutCut:
                onTrbCutClick();
                break;
            case R.id.layoutImage:
                onTrbImageClick(id);
                break;
            case R.id.layoutRotate:
                onTrbRotateClick(id);
                break;
        }
    }

    /**
     * Save
     */
    private void onIvSaveClick() {
        if (bitmap == null) {
            return;
        }
        BottomSaveDialog saveDialog = new BottomSaveDialog(this);
        saveDialog.show();
    }

    /**
     * Crop
     */
    private synchronized void onTrbCutClick() {
        File imageFile = createImageFile();
        smallUri = Uri.fromFile(imageFile);

        File file = new File(album.getPath());
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, "com.picture.fileprovider", file);
            intent.setDataAndType(contentUri, "image/*");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "image/*");
        }
        intent.putExtra("crop", "true");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, smallUri); //设置大图保存到文件
        intent.putExtra("scale", true);
        startActivityForResult(intent, REQUEST_PHOTO_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PHOTO_PICK) {
            if (resultCode == RESULT_OK) {
                if (smallUri != null) {
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(smallUri));
                        ImageUtil.loadImgByUrl(ivPhoto, bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Mirror
     */
    private void onTrbImageClick(int id) {
        showProgressDialog(id);
    }

    /**
     * Rotate
     */
    private void onTrbRotateClick(int id) {
        showProgressDialog(id);
    }

    /**
     * @return Create a class for cropping file output
     */
    private File createImageFile() {
        String imageFileName = "JPEG_picture_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    @Override
    public void callbackSave(SaveEnum saveEnum) {
        if (saveEnum == SaveEnum.SAVE_AS) {
            String fileName = "/IMG_" + DateUtil.nowFormat() + ".jpg";
            if (TextUtils.isEmpty(folderPath)) {
                savedPath = Res.url.photo_uri + fileName;
            } else {
                savedPath = folderPath + fileName;
            }
            BitmapUtil.saveBitmapToSDCard(bitmap, savedPath);
            Intent intent = new Intent();
            intent.putExtra(SAVED_PATH, savedPath);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void exec(int callViewById) {
        switch (callViewById) {
            case R.id.layoutImage:
                bitmap = BitmapUtil.toHorizontalMirror(bitmap);
                break;
            case R.id.layoutRotate:
                bitmap = BitmapUtil.rotateBitmap(bitmap, 90);
                break;
        }
    }

    @Override
    public void onProgressBarResult(int calledByViewId) {
        if (calledByViewId == R.id.layoutImage) {
            initData();
        } else if (calledByViewId == R.id.layoutRotate) {
            initData();
        }
    }

    @Override
    public void onConfirmDialogResult(int calledByViewId, DialogAction whichButton, String resultValue) {
        if (whichButton == DialogAction.POSITIVE) {
            if (calledByViewId == R.id.ivBack) {
                finish();
            }
        }
    }
}