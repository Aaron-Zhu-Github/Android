package com.picture;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.DialogAction;
import com.gyf.immersionbar.ImmersionBar;
import com.picture.dialog.BaseDialog;
import com.picture.dialog.ConfirmDialog;
import com.picture.dialog.InputTextDialog;
import com.picture.dialog.OnDialogResultClickListener;
import com.picture.dialog.OnProgressBarDialogExecuteListener;
import com.picture.dialog.ProgressBarDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment implements OnProgressBarDialogExecuteListener, OnDialogResultClickListener {

    private Unbinder unbinder;
    protected View view;
    protected Bundle selfSavedInstanceState;
    protected ProgressBarDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = getLayoutInflater().inflate(getLayout(), container, false);
        disableAutoFill();
        unbinder = ButterKnife.bind(this, view);
        selfSavedInstanceState = savedInstanceState;
        initBlackStatusBar();
        initView();
        return view;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutoFill() {
        getActivity().getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
    }

    protected void initWhiteStatusBar() {
        ImmersionBar.with(this).statusBarDarkFont(false).init();
    }

    protected void initBlackStatusBar() {
        ImmersionBar.with(this).statusBarDarkFont(true, 0.2f).init();
    }

    protected abstract int getLayout();

    protected abstract void initView();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    protected void toLinkPage(Class<?> clazz) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), clazz);
        startActivity(intent);
        getActivity().finish();
    }

    protected void toLinkPageNotFinished(Class<?> clazz) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), clazz);
        startActivity(intent);
    }

    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public String getMessageByRes(int resId) {
        return view.getResources().getString(resId);
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
}
