package com.picture.dialog;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.DialogAction;

/**
 * Confirm popup
 */
public class ConfirmDialog extends BaseDialog {
    private Context context;
    private String title;
    private String content;
    private boolean isSingleButton;

    public ConfirmDialog(Context context, int calledByViewId, String title, String content, boolean isSingleButton) {
        super(context);
        this.context = context;
        this.title = title;
        this.content = content;
        this.calledByViewId = calledByViewId;
        this.isSingleButton = isSingleButton;
        initialize();
    }

    public ConfirmDialog(Fragment fragment, int calledByViewId, String title, String content, boolean isSingleButton) {
        this(fragment.getActivity(), calledByViewId, title, content, isSingleButton);
        setResult(fragment);
    }

    private void initialize() {
        title(title);
        content(content);
        positiveText("OK");
        onPositive((dialog, which) -> onResultClick(DialogAction.POSITIVE));
        if (isSingleButton == false) {
            negativeText("Cancel");
            onNegative((dialog, which) -> onResultClick(DialogAction.NEGATIVE));
        }
    }
}
