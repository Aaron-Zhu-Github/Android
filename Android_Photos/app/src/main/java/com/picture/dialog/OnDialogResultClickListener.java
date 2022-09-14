package com.picture.dialog;

import com.afollestad.materialdialogs.DialogAction;

public interface OnDialogResultClickListener {
    void onDialogResultClick(int calledByViewId,
                             Class<? extends BaseDialog> dialogClass, DialogAction whichButton,
                             String resultValue);
}
