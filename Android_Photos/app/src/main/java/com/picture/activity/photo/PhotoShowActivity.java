package com.picture.activity.photo;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.DialogAction;
import com.picture.BaseActivity;
import com.picture.R;
import com.picture.dialog.ConfirmDialog;
import com.picture.dialog.ListDialog;
import com.picture.entity.Album;
import com.picture.entity.Tag;
import com.picture.manager.TagManager;
import com.picture.util.DateUtil;
import com.picture.util.FileUtil;
import com.picture.util.ImageUtil;
import com.picture.util.RotationUtil;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Photo display screen
 */
public class PhotoShowActivity extends BaseActivity {
    /**
     * Request photo editing page
     */
    public static final int REQUEST_EDIT_PHOTO_CODE = 1000;
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
     * Tag
     */
    @BindView(R.id.tvTagName)
    TextView tvTagName;
    /**
     * year month day
     */
    @BindView(R.id.tvDate)
    TextView tvDate;
    /**
     * Time
     */
    @BindView(R.id.tvTime)
    TextView tvTime;
    /**
     * Photo
     */
    @BindView(R.id.ivPhoto)
    ImageView ivPhoto;
    /**
     * Edit
     */
    @BindView(R.id.layoutEdit)
    LinearLayout layoutEdit;
    /**
     * Delete
     */
    @BindView(R.id.layoutDelete)
    LinearLayout layoutDelete;
    /**
     * Move the photo to another location
     */
    @BindView(R.id.layoutMove)
    LinearLayout layoutMove;
    /**
     * Tag management
     */
    @BindView(R.id.layoutTag)
    LinearLayout layoutTag;
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
     * Folder path
     */
    private String folderPath;
    /**
     * Label management
     */
    private TagManager manager;
    /**
     * tag
     */
    private Tag tag;
    /**
     * tag name
     */
    private String tagName;
    /**
     * Location
     */
    private Geocoder geocoder;
    /**
     * Operation result
     */
    private long ret;

    @Override
    protected int getLayout() {
        return R.layout.activity_photo_show;
    }

    @Override
    protected void initView() {
        manager = new TagManager(this);
        geocoder = new Geocoder(this, Locale.getDefault());
        initBlackStatusBar();
        receiveParameter();
        initData();
    }

    /**
     * Receive parameters
     */
    private void receiveParameter() {
        section = getIntent().getIntExtra(PARAM_SECTION, 0);
        position = getIntent().getIntExtra(PARAM_POSITION, 0);
        folderPath = getIntent().getStringExtra(PARAM_FOLDER);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageUtil.loadImgByUrl(ivPhoto, album.getPath());
        String fileName = album.getFileName();
        String filePath = album.getPath();
        tag = manager.query(fileName, filePath);
        if (tag != null) {
            tvTagName.setText(tag.getTagName());
        } else {
            tvTagName.setText("");
        }
    }

    /**
     * Bind the Click event of the View such as buttons
     */
    @OnClick({R.id.ivBack, R.id.layoutEdit, R.id.layoutDelete, R.id.layoutMove, R.id.layoutTag})
    public void onViewClicked(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.layoutEdit:
                onEditClick();
                break;
            case R.id.layoutDelete:
                onDeleteClick();
                break;
            case R.id.layoutMove:
                onMoveClick();
                break;
            case R.id.layoutTag:
                onTagClick(v);
                break;
        }
    }

    /**
     * Edit
     */
    private void onEditClick() {
        Intent intent = new Intent(this, PhotoEditActivity.class);
        intent.putExtra(PhotoEditActivity.PARAM_SECTION, section);
        intent.putExtra(PhotoEditActivity.PARAM_POSITION, position);
        intent.putExtra(PhotoEditActivity.PARAM_ALBUM, album);
        intent.putExtra(PhotoEditActivity.PARAM_FOLDER, folderPath);
        startActivityForResult(intent, REQUEST_EDIT_PHOTO_CODE);
    }

    /**
     * Delete
     */
    private void onDeleteClick() {
        ConfirmDialog dialog = new ConfirmDialog(this, 0, "tips", "Do you want to delete the photo?", false);
        dialog.show();
    }

    /**
     * Move the photo to another location
     */
    private void onMoveClick() {
        Intent intent = new Intent(this, MoveActivity.class);
        intent.putExtra(MoveActivity.PARAM_SOURCE_PATH, album.getPath());
        startActivity(intent);
    }

    /**
     * Tag management
     */
    private void onTagClick(View view) {
        List<String> operateList = Arrays.asList(getArrayFromRes(R.array.tag_operation));
        ListDialog operationDialog = new ListDialog(this, 1, "operation", operateList);
        operationDialog.show();
    }

    @Override
    public void onListDialogResult(int calledByViewId, DialogAction whichButton, String resultValue) {
        if (whichButton == DialogAction.POSITIVE) {
            if (calledByViewId == 1) {
                if ("Add Tag".equals(resultValue)) {
                    List<String> tagList = Arrays.asList(getArrayFromRes(R.array.tag_type));
                    ListDialog tagDialog = new ListDialog(this, 2, "type", tagList);
                    tagDialog.show();
                }
                else if ("Remove Tag".equals(resultValue)) {
                    if (tag == null) {
                        String fileName = album.getFileName();
                        String filePath = album.getPath();
                        tag = manager.query(fileName, filePath);
                    }
                    int ret = manager.delete(tag.getTagNo());
                    if (ret > 0) {
                        tagName = "";
                        tvTagName.setText(tagName);
                    }
                    tag = null;
                }
            }
            else if (calledByViewId == 2) {
                // person
                if ("person".equals(resultValue)) {
                    tagName = "person";
                }
                // location
                else if ("location".equals(resultValue)) {
                    tagName = "location";
                }
                if (tag == null) {
                    showProgressDialog(0);
                }
                else {
                    int ret = manager.update(tag.getTagNo(), tagName);
                    if (ret > 0) {
                        tvTagName.setText(tagName);
                    }
                }
            }
        }
    }

    @Override
    public void exec(int callViewById) {
        File file = new File(album.getPath());
        try {
            ExifInterface exif = new ExifInterface(file.getAbsoluteFile());
            String location = "";
            if (exif != null) {
                Calendar cal = Calendar.getInstance();
                // Shooting date
                Date fileDate = FileUtil.parseDate(file);
                cal.setTime(fileDate);
                // Zero milliseconds
                cal.set(Calendar.MILLISECOND, 0);
                album.setDate(cal.getTime().getTime());
                // Latitude and longitude information
                String lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String lng = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                String latRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                String lngRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                // Perform latitude and longitude conversion
                if (lat != null && lng != null && latRef != null && lngRef != null) {
                    float latitude = RotationUtil.convertRationalLatLonToFloat(lat, latRef);
                    float longitude = RotationUtil.convertRationalLatLonToFloat(lng, lngRef);
                    // Reverse geocoding
                    List<Address> addresses = geocoder.getFromLocation(latitude,
                            longitude, 1);
                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);
                        String data = address.toString();
                        int startCity = data.indexOf("1:\"") + "1:\"".length();
                        int endCity = data.indexOf("\"", startCity);
                        String city = data.substring(startCity, endCity);

                        int startPlace = data.indexOf("feature=") + "feature=".length();
                        int endPlace = data.indexOf(",", startPlace);
                        String place = data.substring(startPlace, endPlace);
                        // Shooting location
                        location = place;
                    }
                }
            }
            ret = manager.add(tagName, album.getFileName(), album.getPath(), location);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgressBarResult(int calledByViewId) {
        if (ret > 0) {
            tvTagName.setText(tagName);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_PHOTO_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String savedPath = data.getStringExtra(PhotoEditActivity.SAVED_PATH);
                    if (!TextUtils.isEmpty(savedPath)) {
                        album.setPath(savedPath);
                    }
                }
            }
        }
    }

    @Override
    public void onConfirmDialogResult(int calledByViewId, DialogAction whichButton, String resultValue) {
        if (whichButton == DialogAction.POSITIVE) {
            boolean isSuccess = FileUtil.deleteFile(album.getPath());
            if (isSuccess) {
                finish();
            } else {
                showToast("Failed to delete photo");
            }
        }
    }
}