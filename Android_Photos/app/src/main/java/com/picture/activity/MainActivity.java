package com.picture.activity;

import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.picture.BaseActivity;
import com.picture.R;
import com.picture.fragment.PhotoFragment;
import com.picture.fragment.PictureFragment;
import com.picture.fragment.TextFragment;
import com.zaaach.tabradiobutton.TabRadioButton;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Home page
 */
public class MainActivity extends BaseActivity {
    /**
     * [Text] button at the bottom
     */
    @BindView(R.id.trbText)
    TabRadioButton trbText;
    /**
     * [Photo] button at the bottom
     */
    @BindView(R.id.trbPhoto)
    TabRadioButton trbPhoto;
    /**
     * [Album] button at the bottom
     */
    @BindView(R.id.trbPicture)
    TabRadioButton trbPicture;
    /**
     * Bottom navigator
     */
    static LinearLayout layoutControl;
    /**
     * Text Fragment
     */
    private Fragment textFragment = new TextFragment();
    /**
     * Photo Fragment
     */
    private Fragment photoFragment = new PhotoFragment();
    /**
     * Picture Fragment
     */
    private Fragment pictureFragment = new PictureFragment();
    /**
     * current Fragment
     */
    private static Fragment currentFragment = new Fragment();

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        trbText.setChecked(true);
        trbPhoto.setChecked(false);
        trbPicture.setChecked(false);
        switchFragment(textFragment).commit();
        layoutControl = findViewById(R.id.layoutControl);
    }

    /**
     * Page switching
     */
    public FragmentTransaction switchFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (targetFragment.isAdded() == false) {
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.fragment, targetFragment, targetFragment.getClass().getName());
        } else {
            transaction.hide(currentFragment).show(targetFragment);
        }
        currentFragment = targetFragment;
        return transaction;
    }

    @OnClick({R.id.trbText, R.id.trbPhoto, R.id.trbPicture})
    public void onViewClicked(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.trbText:
                switchFragment(textFragment).commit();
                break;
            case R.id.trbPhoto:
                switchFragment(photoFragment).commit();
                break;
            case R.id.trbPicture:
                switchFragment(pictureFragment).commit();
                break;
        }
    }

    /**
     * @param isShow Display flag
     */
    public static void showControl(boolean isShow) {
        if (isShow) {
            layoutControl.setVisibility(View.VISIBLE);
        } else {
            layoutControl.setVisibility(View.GONE);
        }
    }
}