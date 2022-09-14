package com.picture.fragment;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.picture.BaseFragment;
import com.picture.R;
import com.picture.activity.photo.PhotoShowActivity;
import com.picture.activity.text.TextShowActivity;
import com.picture.common.Res;
import com.picture.entity.Album;
import com.picture.logic.TextAdapter;
import com.picture.util.FileUtil;
import com.picture.util.RotationUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yanzhenjie.permission.AndPermission;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

public class TextFragment extends BaseFragment implements TextAdapter.OnTextListener {
    @BindView(R.id.tvTextCount)
    TextView tvTextCount;
    @BindView(R.id.srlText)
    SmartRefreshLayout srlText;
    @BindView(R.id.rlvText)
    RecyclerView rlvText;
    private List<Album> albums;
    private Geocoder geocoder;
    private TextAdapter adapter;

    @Override
    protected int getLayout() {
        return R.layout.fragment_text;
    }

    @Override
    protected void initView() {
        albums = new ArrayList<>();
        srlText.setEnableLoadMore(false);
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        initListView();
        onLoadRefresh();
        AndPermission.with(this)
                .runtime()
                .permission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .onGranted(data -> {
                    showProgressDialog(0);
                })
                .onDenied(data -> showToast("For better use, please allow all permissions"))
                .start();
    }

    private void onLoadRefresh() {
        srlText.setOnRefreshListener(refreshLayout -> {
            srlText.finishRefresh();
            showProgressDialog(0);
        });
    }

    private void initListView() {
        rlvText.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TextAdapter(getActivity(), albums, this);
        rlvText.setAdapter(adapter);
    }

    @Override
    public void exec(int callViewById) {
        albums.clear();
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
        Collections.sort(files, (o1, o2) -> {
            if (o1.lastModified() > o2.lastModified()) {
                return -1;
            } else if (o1.lastModified() == o2.lastModified()) {
                return 0;
            }
            return 1;
        });

        Calendar cal = Calendar.getInstance();

        for (File file : files) {
            try {
                Album album = new Album();
                album.setFileName(file.getName().substring(0, file.getName().indexOf(".jpg")));
                album.setPath(file.getAbsolutePath());
                ExifInterface exif = new ExifInterface(file.getAbsoluteFile());
                if (exif != null) {
                    Date fileDate = FileUtil.parseDate(file);
                    cal.setTime(fileDate);
                    cal.set(Calendar.MILLISECOND, 0);
                    album.setDate(cal.getTime().getTime());
                    String lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                    String lng = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                    String latRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                    String lngRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                    if (lat != null && lng != null && latRef != null && lngRef != null) {
                        float latitude = RotationUtil.convertRationalLatLonToFloat(lat, latRef);
                        float longitude = RotationUtil.convertRationalLatLonToFloat(lng, lngRef);
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
                            album.setLocation(city + place);
                        }
                    }
                }

                albums.add(album);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onProgressBarResult(int calledByViewId) {
        adapter.notifyDataSetChanged();
        tvTextCount.setText(albums.size() + " photos");
    }

    @Override
    public void callbackText(int id, int position) {
        Intent intent = new Intent(getActivity(), TextShowActivity.class);
        intent.putExtra(PhotoShowActivity.PARAM_ALBUM, albums.get(position));
        startActivity(intent);
    }
}