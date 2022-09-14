package com.picture.dialog;

import android.content.Context;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * dialog base class
 */
public class BaseDialog extends MaterialDialog.Builder implements View.OnClickListener {

    protected Object resultObject;
    protected int calledByViewId;
    protected MaterialDialog dialog;

    public BaseDialog(Context context) {
        super(context);
        resultObject = context;
        // dialog means outside area, click can not close
        cancelable(false);
        canceledOnTouchOutside(false);
    }

    public void setResult(Object result) {
        this.resultObject = result;
    }

    public void onProgressBarResult(ProgressBarDialog.IProgressBarResult progressBarResult) {
        if (progressBarResult != null) {
            progressBarResult.destroy();
        }
        onResultClick(DialogAction.POSITIVE);
    }

    public void onResultClick(DialogAction which, String resultValue) {
        if (resultObject instanceof OnDialogResultClickListener) {
            OnDialogResultClickListener resultObject = (OnDialogResultClickListener) this.resultObject;
            resultObject.onDialogResultClick(calledByViewId, this.getClass(),
                    which, resultValue);
        }
    }

    public void onResultClick(DialogAction which) {
        onResultClick(which, "");
    }

    public void setCalledByViewId(int viewId) {
        this.calledByViewId = viewId;
    }

    @Override
    public void onClick(View v) {

    }

    protected void onBeep() {

    }
}
