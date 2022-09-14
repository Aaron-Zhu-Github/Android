package com.picture;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.gyf.immersionbar.ImmersionBar;
import com.picture.dialog.BaseDialog;
import com.picture.dialog.ConfirmDialog;
import com.picture.dialog.InputTextDialog;
import com.picture.dialog.ListDialog;
import com.picture.dialog.OnDialogResultClickListener;
import com.picture.dialog.OnProgressBarDialogExecuteListener;
import com.picture.dialog.ProgressBarDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity implements OnProgressBarDialogExecuteListener, OnDialogResultClickListener {

    private Unbinder unbinder;
    protected Bundle selfSavedInstanceState;
    protected ProgressBarDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disableAutoFill();
        setContentView(getLayoutInflater().inflate(getLayout(), null, true));
        selfSavedInstanceState = savedInstanceState;
        initWhiteStatusBar();
        unbinder = ButterKnife.bind(this);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutoFill() {
        getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
    }

    protected abstract int getLayout();

    protected abstract void initView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    protected void initWhiteStatusBar() {
        ImmersionBar.with(this).statusBarDarkFont(false).init();
    }

    protected void initBlackStatusBar() {
        ImmersionBar.with(this).statusBarDarkFont(true, 0.2f).init();
    }

    protected void toLinkPage(Class<?> clazz) {
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        startActivity(intent);
        finish();
    }

    protected void toLinkPageNotFinished(Class<?> clazz) {
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        startActivity(intent);
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public String getMessageByRes(int resId) {
        return getResources().getString(resId);
    }

    public String[] getArrayFromRes(int resId) {
        return getResources().getStringArray(resId);
    }

    @Override
    public void onDialogResultClick(int calledByViewId, Class<? extends BaseDialog> dialogClass, DialogAction whichButton, String resultValue) {
        if (dialogClass == ProgressBarDialog.class) {
            onProgressBarResult(calledByViewId);
        } else if (dialogClass == ConfirmDialog.class) {
            onConfirmDialogResult(calledByViewId, whichButton, resultValue);
        } else if (dialogClass == InputTextDialog.class) {
            onInputTextDialogResult(calledByViewId, whichButton, resultValue);
        } else if (dialogClass == ListDialog.class) {
            onListDialogResult(calledByViewId, whichButton, resultValue);
        }
    }

    @Override
    public void exec(int callViewById) {
    }

    public void onProgressBarResult(int calledByViewId) {
    }

    public void onConfirmDialogResult(int calledByViewId, DialogAction whichButton, String resultValue) {
    }

    public void onInputTextDialogResult(int calledByViewId, DialogAction whichButton, String resultValue) {
    }

    protected void showProgressDialog(int calledByViewId) {
        progressDialog = new ProgressBarDialog(this, calledByViewId);
        progressDialog.show();
    }

    public void onListDialogResult(int calledByViewId, DialogAction whichButton, String resultValue) {
    }
}
